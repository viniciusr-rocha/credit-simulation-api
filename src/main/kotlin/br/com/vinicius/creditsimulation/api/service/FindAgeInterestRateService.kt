package br.com.vinicius.creditsimulation.api.service

import java.math.BigDecimal

interface FindAgeInterestRateService {
    suspend fun findRate(age: Int): BigDecimal
}
