package br.com.vinicius.creditsimulation.api.service

import br.com.vinicius.creditsimulation.api.models.request.CalculateLoanRequest
import br.com.vinicius.creditsimulation.api.models.response.CreditSimulationResponse
import br.com.vinicius.creditsimulation.api.service.impl.CreditSimulationProcessingServiceImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class CreditSimulationProcessingServiceImplTest {
    private val loanCalculationService: LoanCalculationService = mockk()
    private val service = CreditSimulationProcessingServiceImpl(loanCalculationService)

    @Test
    fun `processSimulation should return correct response`() =
        runTest {
            val idempotencyKey = UUID.randomUUID().toString()
            val request = mockk<CalculateLoanRequest>()
            val expectedResponse = mockk<CreditSimulationResponse>()

            coEvery { loanCalculationService.calculate(request) } returns expectedResponse

            val response = service.processSimulation(idempotencyKey, request)

            assertEquals(expectedResponse, response)

            coVerify(exactly = 1) { loanCalculationService.calculate(request) }
        }
}
