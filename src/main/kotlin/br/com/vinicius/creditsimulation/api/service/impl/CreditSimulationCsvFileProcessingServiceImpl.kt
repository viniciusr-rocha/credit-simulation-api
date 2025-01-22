package br.com.vinicius.creditsimulation.api.service.impl

import br.com.vinicius.creditsimulation.api.models.request.CreditSimulationRequest
import br.com.vinicius.creditsimulation.api.models.response.CreditSimulationResponse
import br.com.vinicius.creditsimulation.api.service.CreditSimulationCsvFileProcessingService
import br.com.vinicius.creditsimulation.api.service.LoanCalculationService
import com.opencsv.CSVReader
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import java.io.InputStreamReader
import java.math.BigDecimal

@Service
class CreditSimulationCsvFileProcessingServiceImpl(
    @Value("\${credits-simulation.csv-file.batch-size}")
    private val bachSize: Int,
    private val loanCalculationService: LoanCalculationService
) : CreditSimulationCsvFileProcessingService {
    val logger = KotlinLogging.logger {}

    @Cacheable(value = ["creditSimulationFile"], key = "#filePart.filename()")
    override suspend fun processFilePart(filePart: FilePart) {
        runCatching {
            val startTime = System.nanoTime()
            logger.info { "Starting the CSV file processing for file: ${filePart.filename()}" }
            process(filePart, startTime)
        }.getOrElse {
            logger.error(it) { "Error while processing file: ${filePart.filename()}" }
            throw it
        }
    }

    private suspend fun process(
        filePart: FilePart,
        startTime: Long
    ) {
        val inputStream =
            filePart
                .content()
                .toMono()
                .awaitFirst()
                .asInputStream()

        withContext(Dispatchers.IO) {
            InputStreamReader(inputStream).use { reader ->
                CSVReader(reader).use { csvReader ->
                    csvReader.readNext()

                    val responses = mutableListOf<Deferred<List<CreditSimulationResponse>>>()
                    val batch = mutableListOf<Array<String>>()

                    csvReader.forEach { line ->
                        batch.add(line)
                        if (batch.size >= bachSize) {
                            executeBatch(batch, responses)
                            batch.clear()
                        }
                    }

                    if (batch.isNotEmpty()) {
                        executeBatch(batch, responses)
                    }

                    val allResults = responses.awaitAll().flatten()
                    logger.info { "Processing completed. Total results: ${allResults.size}" }

                    allResults.first().validUntil

                    val endTime = System.nanoTime()
                    val durationInMillis = (endTime - startTime) / 1_000_000
                    logger.info { "Processing time: $durationInMillis ms" }
                }
            }
        }
    }

    private fun CoroutineScope.executeBatch(
        batch: MutableList<Array<String>>,
        responses: MutableList<Deferred<List<CreditSimulationResponse>>>
    ) {
        val currentBatch = batch.toList()
        val deferredBatch =
            async(Dispatchers.Default) {
                processBatch(currentBatch)
            }
        responses.add(deferredBatch)
    }

    private suspend fun processBatch(batch: List<Array<String>>): List<CreditSimulationResponse> =
        batch.map { line ->
            runCatching {
                val variableRate =
                    if (line.size > 5 && line[5].isNotBlank()) {
                        line[5].toBigDecimalOrNull()
                    } else {
                        null
                    }
                val request =
                    CreditSimulationRequest(
                        email = line[0],
                        loanAmount = BigDecimal(line[1]),
                        customerDateOfBirth = line[2],
                        paymentTermInMonths = line[3].toInt(),
                        interestRateType = line[4].trim().uppercase(),
                        annualVariableInterestRate = variableRate
                    )
                loanCalculationService.calculate(request.toCalculateLoanRequest())
            }.getOrElse {
                logger.error(it) { "Error processing line ${line.joinToString()}: ${it.message}" }
                throw it
            }
        }
}
