package br.com.vinicius.creditsimulation.api.service.impl

import br.com.vinicius.creditsimulation.api.repository.AgeInterestRateRepository
import br.com.vinicius.creditsimulation.api.service.FindAgeInterestRateService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class FindAgeInterestRateServiceImpl(
    private val ageInterestRateRepository: AgeInterestRateRepository
) : FindAgeInterestRateService {
    private val logger = KotlinLogging.logger {}

    @Cacheable(value = ["interestRates"], key = "#age")
    override suspend fun findRate(age: Int): BigDecimal =
        runCatching {
            ageInterestRateRepository.findAgeRate(age)
        }.getOrElse {
            logger.error(it) { "Error finding interest rate for age: $age" }
            throw it
        }
}
