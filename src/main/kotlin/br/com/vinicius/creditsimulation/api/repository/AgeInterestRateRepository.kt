package br.com.vinicius.creditsimulation.api.repository

import java.math.BigDecimal

interface AgeInterestRateRepository {
    suspend fun findAgeRate(age: Int): BigDecimal
}
