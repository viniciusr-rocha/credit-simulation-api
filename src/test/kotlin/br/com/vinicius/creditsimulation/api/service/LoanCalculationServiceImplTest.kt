package br.com.vinicius.creditsimulation.api.service

import br.com.vinicius.creditsimulation.api.enums.InterestRateType
import br.com.vinicius.creditsimulation.api.exception.UnprocessableEntityInterestRateException
import br.com.vinicius.creditsimulation.api.exception.UnprocessableEntityLoanCalculationException
import br.com.vinicius.creditsimulation.api.models.request.CalculateLoanRequest
import br.com.vinicius.creditsimulation.api.service.impl.LoanCalculationServiceImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.util.ReflectionTestUtils
import java.math.BigDecimal

class LoanCalculationServiceImplTest {
    private val ageInterestRateService: FindAgeInterestRateService = mockk()
    private val validPeriodInMinutes = 1440L // 1 dia
    private val service = LoanCalculationServiceImpl(validPeriodInMinutes, ageInterestRateService)

    @Test
    fun `should calculate loan successfully with fixed interest rate`() =
        runTest {
            val request =
                CalculateLoanRequest(
                    email = "test@test.com",
                    loanAmount = BigDecimal("10000"),
                    paymentTermInMonths = 12,
                    interestRateType = InterestRateType.FIXED,
                    annualVariableInterestRate = null,
                    age = 30
                )
            val fixedInterestRate = BigDecimal("5") // 5%

            coEvery { ageInterestRateService.findRate(request.age) } returns fixedInterestRate

            val response = service.calculate(request)

            assertNotNull(response)
            assertEquals(BigDecimal("10272.90"), response.totalAmountToBePaid)
            assertEquals(BigDecimal("856.07"), response.monthInstallmentAmount)
            assertEquals(BigDecimal("272.90"), response.totalInterestPaid)
            coVerify(exactly = 1) { ageInterestRateService.findRate(request.age) }
        }

    @Test
    fun `should calculate loan successfully with variable interest rate`() =
        runTest {
            val request =
                CalculateLoanRequest(
                    email = "test@test.com",
                    loanAmount = BigDecimal("10000"),
                    paymentTermInMonths = 12,
                    interestRateType = InterestRateType.VARIABLE,
                    annualVariableInterestRate = BigDecimal("5"),
                    age = 30
                )

            val response = service.calculate(request)

            assertNotNull(response)
            assertEquals(BigDecimal("10272.90"), response.totalAmountToBePaid)
            assertEquals(BigDecimal("856.07"), response.monthInstallmentAmount)
            assertEquals(BigDecimal("272.90"), response.totalInterestPaid)
            coVerify(exactly = 0) { ageInterestRateService.findRate(request.age) }
        }

    @Test
    fun `should throw LoanCalculationException and also trigger InvalidInterestRateException when variable interest rate is null`() =
        runTest {
            val request =
                CalculateLoanRequest(
                    email = "test@test.com",
                    loanAmount = BigDecimal("10000"),
                    paymentTermInMonths = 12,
                    interestRateType = InterestRateType.VARIABLE,
                    annualVariableInterestRate = null,
                    age = 30
                )

            val exception =
                assertThrows<UnprocessableEntityLoanCalculationException> {
                    service.calculate(request)
                }

            assertEquals("Failed to calculate loan", exception.message)

            val cause = exception.cause
            assertTrue(cause is UnprocessableEntityInterestRateException)
            assertEquals("Variable rate must be provided when interestRateType is VARIABLE", cause?.message)
        }

    @Test
    fun `should handle exception during calculation`() =
        runTest {
            val request =
                CalculateLoanRequest(
                    email = "test@test.com",
                    loanAmount = BigDecimal("10000"),
                    paymentTermInMonths = 12,
                    interestRateType = InterestRateType.FIXED,
                    annualVariableInterestRate = null,
                    age = 30
                )

            val exception = RuntimeException("Database error")
            coEvery { ageInterestRateService.findRate(request.age) } throws exception

            val thrownException =
                assertThrows<UnprocessableEntityLoanCalculationException> {
                    service.calculate(request)
                }

            assertEquals("Failed to calculate loan", thrownException.message)
        }

    @Test
    fun `should calculate monthly installment correctly`() {
        val loanAmount = BigDecimal("10000")
        val interestRateMonthly = BigDecimal("0.0041667") // 0.05 / 12
        val months = 12
        val expectedInstallment = BigDecimal("856.0750011899069")

        val installment =
            ReflectionTestUtils.invokeMethod<BigDecimal>(
                service,
                "calculateMonthlyInstallment",
                loanAmount,
                interestRateMonthly,
                months
            )

        assertNotNull(installment)
        assertEquals(expectedInstallment, installment)
    }
}
