package br.com.vinicius.creditsimulation.api.models.response

import br.com.vinicius.creditsimulation.api.models.ValidUntil
import java.math.BigDecimal
import java.time.Instant

data class CreditSimulationResponse(
    val totalAmountToBePaid: BigDecimal,
    val monthInstallmentAmount: BigDecimal,
    val totalInterestPaid: BigDecimal,
    val createdAt: Instant? = Instant.now(),
    val validUntil: ValidUntil
)
