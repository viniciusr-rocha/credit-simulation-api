package br.com.vinicius.creditsimulation.api.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
data class AgeInterestRateRepositoryImpl(
    private val databaseClient: DatabaseClient
) : AgeInterestRateRepository {
    override suspend fun findAgeRate(age: Int): BigDecimal {
        val query =
            """
            SELECT
                   interest_rate
            FROM annual_interest_rate_by_age
            WHERE min_age <= :age
              AND (max_age IS NULL OR max_age >= :age);
            """.trimIndent()

        return databaseClient
            .sql(query)
            .bind("age", age)
            .map { row -> row["interest_rate"] as BigDecimal }
            .one()
            .awaitSingle()
    }
}
