package br.com.vinicius.creditsimulation.api.service

import org.springframework.http.codec.multipart.FilePart

interface CreditSimulationCsvFileProcessingService {
    suspend fun processFilePart(filePart: FilePart)
}
