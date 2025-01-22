package br.com.vinicius.creditsimulation.api.controller

import br.com.vinicius.creditsimulation.api.enums.InterestRateType
import br.com.vinicius.creditsimulation.api.models.request.CalculateLoanRequest
import br.com.vinicius.creditsimulation.api.models.request.CreditSimulationRequest
import br.com.vinicius.creditsimulation.api.models.response.CreditSimulationResponse
import br.com.vinicius.creditsimulation.api.service.CreditSimulationProcessingService
import br.com.vinicius.creditsimulation.api.service.impl.CreditSimulationCsvFileProcessingServiceImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import java.math.BigDecimal
import kotlin.test.assertEquals

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreditSimulationControllerIntegrationTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var creditSimulationProcessingService: CreditSimulationProcessingService

    @Autowired
    private lateinit var creditSimulationCsvFileProcessingService: CreditSimulationCsvFileProcessingServiceImpl

    @Test
    fun `should process credit simulation request`() =
        runTest {
            val request =
                CreditSimulationRequest(
                    email = "34234@gmail.com",
                    loanAmount = BigDecimal(10000),
                    customerDateOfBirth = "01/01/1995",
                    paymentTermInMonths = 12,
                    interestRateType = InterestRateType.VARIABLE.name,
                    annualVariableInterestRate = BigDecimal(5)
                )
            val calculateLoanRequest = CalculateLoanRequest(request)

            creditSimulationProcessingService.processSimulation("test-idempotency-key", calculateLoanRequest)

            val requestHttp =
                webTestClient
                    .post()
                    .uri("/v1/credit-simulations")
                    .header("idempotency-key", "test-idempotency-key")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBody(CreditSimulationResponse::class.java)
                    .returnResult()

            val response = requestHttp.responseBody

            assertNotNull(response)
            assertEquals(BigDecimal("10272.90"), response?.totalAmountToBePaid)
            assertEquals(BigDecimal("856.07"), response?.monthInstallmentAmount)
            assertEquals(BigDecimal("272.90"), response?.totalInterestPaid)
        }

    @Test
    fun `should process file for batch credit simulations and persist data in database`() =
        runTest {
            val filePart: FilePart = mockk()
            val resource = ClassPathResource("testFile.csv")
            val csvContent = resource.inputStream.bufferedReader().readText()

            val dataBuffer: DataBuffer = DefaultDataBufferFactory().wrap(csvContent.toByteArray())
            val httpHeaders =
                HttpHeaders().apply {
                    set(HttpHeaders.CONTENT_TYPE, "text/csv")
                }

            every { filePart.name() } returns "file"
            every { filePart.filename() } returns "testFile.csv"
            every { filePart.headers() } returns httpHeaders
            every { filePart.content() } returns Flux.just(dataBuffer)

            creditSimulationCsvFileProcessingService.processFilePart(filePart)

            webTestClient
                .post()
                .uri("/v1/credit-simulations/batch")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(filePart)
                .exchange()
                .expectStatus()
                .isAccepted
        }
}
