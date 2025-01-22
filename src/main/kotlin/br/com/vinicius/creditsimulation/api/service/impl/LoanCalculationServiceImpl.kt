package br.com.vinicius.creditsimulation.api.service.impl

import br.com.vinicius.creditsimulation.api.enums.InterestRateType
import br.com.vinicius.creditsimulation.api.exception.UnprocessableEntityInterestRateException
import br.com.vinicius.creditsimulation.api.exception.UnprocessableEntityLoanCalculationException
import br.com.vinicius.creditsimulation.api.models.ValidUntil
import br.com.vinicius.creditsimulation.api.models.request.CalculateLoanRequest
import br.com.vinicius.creditsimulation.api.models.response.CreditSimulationResponse
import br.com.vinicius.creditsimulation.api.service.FindAgeInterestRateService
import br.com.vinicius.creditsimulation.api.service.LoanCalculationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

@Service
class LoanCalculationServiceImpl(
    @Value("\${credits-simulation.valid-period-in-minutes}")
    private val validPeriodInMinutes: Long,
    private val findAgeInterestRateService: FindAgeInterestRateService
) : LoanCalculationService {
    private val logger = KotlinLogging.logger {}

    override suspend fun calculate(request: CalculateLoanRequest): CreditSimulationResponse =
        runCatching {
            calculateLoan(request)
        }.getOrElse {
            logger.error(it) { "Error occurred while calculating loan for request: $request" }
            throw UnprocessableEntityLoanCalculationException("Failed to calculate loan", it)
        }

    private suspend fun calculateLoan(request: CalculateLoanRequest): CreditSimulationResponse {
        val loanAmount = request.loanAmount
        val paymentTermInMonths = request.paymentTermInMonths
        val interestRate = annualInterestRateByType(request)

        val interestRateMonthly = interestRate.divide(BigDecimal(12), MathContext.DECIMAL64)

        val pmt = calculateMonthlyInstallment(loanAmount, interestRateMonthly, paymentTermInMonths)

        val totalAmountToBePaid = pmt.multiply(BigDecimal(paymentTermInMonths), MathContext.DECIMAL64)
        val totalInterestPaid = totalAmountToBePaid.subtract(loanAmount, MathContext.DECIMAL64)

        return CreditSimulationResponse(
            totalAmountToBePaid = totalAmountToBePaid.setScale(2, RoundingMode.HALF_UP),
            monthInstallmentAmount = pmt.setScale(2, RoundingMode.HALF_UP),
            totalInterestPaid = totalInterestPaid.setScale(2, RoundingMode.HALF_UP),
            validUntil = ValidUntil.create(validPeriodInMinutes)
        )
    }

    private suspend fun annualInterestRateByType(request: CalculateLoanRequest): BigDecimal {
        val interestRate =
            when (request.interestRateType) {
                InterestRateType.FIXED -> findAgeInterestRateService.findRate(request.age)
                InterestRateType.VARIABLE ->
                    request.annualVariableInterestRate
                        ?: throw UnprocessableEntityInterestRateException("Variable rate must be provided when interestRateType is VARIABLE")
            }
        return interestRate.divide(BigDecimal(100), MathContext.DECIMAL64)
    }

    private fun calculateMonthlyInstallment(
        loanAmount: BigDecimal,
        interestRateMonthly: BigDecimal,
        months: Int
    ): BigDecimal =
        with(interestRateMonthly) {
            val onePlusRate = BigDecimal.ONE.add(this, MathContext.DECIMAL64)
            val denominator =
                BigDecimal.ONE.subtract(onePlusRate.pow(-months, MathContext.DECIMAL64), MathContext.DECIMAL64)
            loanAmount.multiply(this, MathContext.DECIMAL64).divide(denominator, MathContext.DECIMAL64)
        }
}
