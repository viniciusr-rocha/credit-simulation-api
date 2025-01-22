package br.com.vinicius.creditsimulation.api.service

import br.com.vinicius.creditsimulation.api.models.ValidUntil
import br.com.vinicius.creditsimulation.api.models.response.CreditSimulationResponse
import br.com.vinicius.creditsimulation.api.service.impl.CreditSimulationCsvFileProcessingServiceImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import java.math.BigDecimal

class CreditSimulationCsvFileProcessingServiceImplTest {
    private val loanCalculationService: LoanCalculationService = mockk()
    private val batchSize: Int = 1000
    private val service = CreditSimulationCsvFileProcessingServiceImpl(batchSize, loanCalculationService)

    @Test
    fun `test processFilePart processes CSV file with 10k values successfully`() =
        runTest {
            val filePart: FilePart = mockk()
            val resource = ClassPathResource("testFile.csv")
            val csvContent = resource.inputStream.bufferedReader().readText()

            val dataBuffer: DataBuffer = DefaultDataBufferFactory().wrap(csvContent.toByteArray())

            val calculationResponse =
                CreditSimulationResponse(
                    totalAmountToBePaid = BigDecimal("10272.90"),
                    monthInstallmentAmount = BigDecimal("856.07"),
                    totalInterestPaid = BigDecimal("272.90"),
                    validUntil = ValidUntil.create(600)
                )

            every { filePart.filename() } returns "testFile.csv"
            every { filePart.content() } returns Flux.just(dataBuffer)

            coEvery { loanCalculationService.calculate(any()) } returns calculationResponse

            service.processFilePart(filePart)

            coVerify(exactly = 10000) { loanCalculationService.calculate(any()) }
        }
}
