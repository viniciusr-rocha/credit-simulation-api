package br.com.vinicius.creditsimulation.api.service

import br.com.vinicius.creditsimulation.api.models.request.CalculateLoanRequest
import br.com.vinicius.creditsimulation.api.models.response.CreditSimulationResponse

interface LoanCalculationService {
    suspend fun calculate(request: CalculateLoanRequest): CreditSimulationResponse
}
