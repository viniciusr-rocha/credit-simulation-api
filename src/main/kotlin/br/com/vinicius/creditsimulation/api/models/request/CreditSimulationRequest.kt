package br.com.vinicius.creditsimulation.api.models.request

import java.math.BigDecimal

data class CreditSimulationRequest(
    val email: String,
    val loanAmount: BigDecimal,
    val customerDateOfBirth: String,
    val paymentTermInMonths: Int,
    val interestRateType: String,
    val annualVariableInterestRate: BigDecimal? = null
) {
    fun toCalculateLoanRequest(): CalculateLoanRequest = CalculateLoanRequest(this)
}
