package br.com.vinicius.creditsimulation.api.controller

import br.com.vinicius.creditsimulation.api.models.request.CreditSimulationRequest
import br.com.vinicius.creditsimulation.api.models.response.CreditSimulationResponse
import br.com.vinicius.creditsimulation.api.service.CreditSimulationCsvFileProcessingService
import br.com.vinicius.creditsimulation.api.service.CreditSimulationProcessingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/credit-simulations")
class CreditSimulatorController(
    private val creditSimulationProcessingService: CreditSimulationProcessingService,
    private val creditSimulationCsvFileProcessingService: CreditSimulationCsvFileProcessingService
) {
    @PostMapping
    suspend fun simulator(
        @RequestHeader("idempotency-key") idempotencyKey: String,
        @RequestBody request: CreditSimulationRequest
    ): CreditSimulationResponse = creditSimulationProcessingService.processSimulation(idempotencyKey, request.toCalculateLoanRequest())

    @PostMapping("/batch", consumes = ["multipart/form-data"])
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun processFile(
        @RequestPart("file") file: FilePart
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            creditSimulationCsvFileProcessingService.processFilePart(file)
        }
    }
}
