package br.com.vinicius.creditsimulation.api.service

import org.springframework.http.codec.multipart.FilePart

interface CreditSimulationFileProcessingService {
    suspend fun processFilePart(filePart: FilePart)
}
