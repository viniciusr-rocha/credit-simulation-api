package br.com.vinicius.creditsimulation.api.service

import br.com.vinicius.creditsimulation.api.models.request.CalculateLoanRequest
import br.com.vinicius.creditsimulation.api.models.response.CreditSimulationResponse

interface CreditSimulationProcessingService {
    suspend fun processSimulation(
        idempotencyKey: String,
        request: CalculateLoanRequest
    ): CreditSimulationResponse
}
