package br.com.vinicius.creditsimulation.api.models.request

import br.com.vinicius.creditsimulation.api.enums.InterestRateType
import br.com.vinicius.creditsimulation.api.models.ValidUntil
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

data class CalculateLoanRequest(
    val email: String,
    val loanAmount: BigDecimal,
    val age: Int,
    val paymentTermInMonths: Int,
    val interestRateType: InterestRateType,
    val annualVariableInterestRate: BigDecimal? = null,
    val validUntil: ValidUntil? = null
) {
    constructor(creditSimulationRequest: CreditSimulationRequest) : this(
        email = creditSimulationRequest.email,
        loanAmount = creditSimulationRequest.loanAmount,
        age = calculateAge(creditSimulationRequest.customerDateOfBirth),
        paymentTermInMonths = creditSimulationRequest.paymentTermInMonths,
        interestRateType = InterestRateType.fromValue(creditSimulationRequest.interestRateType),
        annualVariableInterestRate = creditSimulationRequest.annualVariableInterestRate
    )

    companion object {
        private fun calculateAge(customerDateOfBirth: String): Int {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(customerDateOfBirth, formatter)
            val currentDate = LocalDate.now()
            val period = Period.between(birthDate, currentDate)
            return period.years
        }
    }
}
