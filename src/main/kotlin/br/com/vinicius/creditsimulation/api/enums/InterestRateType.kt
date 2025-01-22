package br.com.vinicius.creditsimulation.api.enums

import br.com.vinicius.creditsimulation.api.exception.InvalidEnumException

enum class InterestRateType {
    FIXED,
    VARIABLE;

    companion object {
        fun fromValue(value: String?): InterestRateType =
            entries.find { it.name.equals(value, ignoreCase = true) }
                ?: throw InvalidEnumException(entries)
    }
}
