package br.com.vinicius.creditsimulation.api.models

import java.time.Instant
import java.time.temporal.ChronoUnit

@JvmInline
value class ValidUntil(
    private val value: Instant
) {
    companion object {
        fun create(validPeriodInMinutes: Long): ValidUntil {
            val now = Instant.now()
            val validity = now.plus(validPeriodInMinutes, ChronoUnit.MINUTES)
            return ValidUntil(validity)
        }
    }
}
