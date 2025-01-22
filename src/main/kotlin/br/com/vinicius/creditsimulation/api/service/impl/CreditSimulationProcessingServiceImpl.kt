package br.com.vinicius.creditsimulation.api.service.impl

import br.com.vinicius.creditsimulation.api.models.request.CalculateLoanRequest
import br.com.vinicius.creditsimulation.api.models.response.CreditSimulationResponse
import br.com.vinicius.creditsimulation.api.service.CreditSimulationProcessingService
import br.com.vinicius.creditsimulation.api.service.LoanCalculationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CreditSimulationProcessingServiceImpl(
    private val loanCalculationService: LoanCalculationService
) : CreditSimulationProcessingService {
    private val logger = KotlinLogging.logger {}

    @Cacheable(value = ["creditSimulation"], key = "#idempotencyKey")
    override suspend fun processSimulation(
        idempotencyKey: String,
        request: CalculateLoanRequest
    ): CreditSimulationResponse =
        loanCalculationService
            .calculate(request)
            .also { logger.info { "Credit simulation processed successfully" } }
}
