package br.com.vinicius.creditsimulation.api.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig(
    @Value("\${credits-simulation.valid-period-in-minutes}")
    private val validPeriodInMinutes: Long
) {
    @Bean
    fun caffeineCacheManager(): CacheManager {
        val cacheManager =
            CaffeineCacheManager("interestRates")
        cacheManager.setCaffeine(caffeineCacheBuilder())
        return cacheManager
    }

    private fun caffeineCacheBuilder(): Caffeine<Any, Any> =
        Caffeine
            .newBuilder()
            .initialCapacity(50)
            .maximumSize(120)
            .expireAfterWrite(validPeriodInMinutes, TimeUnit.MINUTES)
}
