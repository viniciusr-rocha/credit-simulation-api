package br.com.vinicius.creditsimulation.api.service

import br.com.vinicius.creditsimulation.api.repository.AgeInterestRateRepository
import br.com.vinicius.creditsimulation.api.service.impl.FindAgeInterestRateServiceImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class FindAgeInterestRateServiceImplTest {
    private val ageInterestRateRepository: AgeInterestRateRepository = mockk()
    private val service = FindAgeInterestRateServiceImpl(ageInterestRateRepository)

    @Test
    fun `findRate should return correct interest rate`() =
        runTest {
            val age = 50
            val expectedRate = BigDecimal("5")

            coEvery { ageInterestRateRepository.findAgeRate(age) } returns expectedRate

            val rate = service.findRate(age)

            assertEquals(expectedRate, rate)
            coVerify(exactly = 1) { ageInterestRateRepository.findAgeRate(age) }
        }

    @Test
    fun `should throw AgeInterestRateException when repository fails`() =
        runTest {
            val age = 50
            val exception = RuntimeException("Database error")
            coEvery { ageInterestRateRepository.findAgeRate(age) } throws exception

            val thrownException =
                assertThrows<RuntimeException> {
                    service.findRate(age)
                }

            assertEquals("Database error", thrownException.message)
        }
}
