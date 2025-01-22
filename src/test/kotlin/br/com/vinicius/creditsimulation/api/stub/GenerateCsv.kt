package br.com.vinicius.creditsimulation.api.stub

import br.com.vinicius.creditsimulation.api.enums.InterestRateType
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.random.Random

fun main() {
    val fileUuid = UUID.randomUUID().toString()
    // Criar o arquivo CSV
    val file = File("$fileUuid.csv")

    file.bufferedWriter(Charsets.UTF_8).use { writer ->
        // Escrever cabeçalho do CSV
        writer.write("email,loanAmount,customerDateOfBirth,paymentTermInMonths,interestRateType,variableRate")
        writer.newLine()

        repeat(10_000) {
            runCatching {
                // Gera os dados da simulação
                val email = generateEmail()
                val loanAmount = Random.nextInt(10_000, 5_000_000)
                val customerDateOfBirth = generateRandomDate()
                val paymentTermInMonths = listOf(12, 24, 36, 48, 60).random()
                val interestRateType = InterestRateType.entries.random().name

                // Se o tipo de taxa de juros for FIXED, variableRate deve ser vazio
                val variableRate =
                    if (interestRateType == InterestRateType.VARIABLE.name) {
                        listOf(0.01, 0.02, 0.03, 0.05, 0.08, 0.13).random()
                    } else {
                        null
                    }

                // Cria a linha CSV garantindo que nenhum campo esteja vazio ou inválido
                val csvLine =
                    buildCsvLine(
                        email = email,
                        loanAmount = loanAmount,
                        customerDateOfBirth = customerDateOfBirth,
                        paymentTermInMonths = paymentTermInMonths,
                        interestRateType = interestRateType,
                        variableRate = variableRate
                    )

                // Escrever a linha CSV
                writer.write(csvLine)
                writer.newLine()
            }.getOrElse {
                println("Erro ao gerar linha: ${it.message}")
            }
        }
    }

    println("Arquivo $file gerado com sucesso!")
}

fun generateEmail(): String {
    val randomPart = (1..5).map { Random.nextInt(0, 10) }.joinToString("")
    return "user+$randomPart@gmail.com"
}

private fun generateRandomDate(): String {
    val startYear = 1950
    val endYear = 2003
    val randomYear = Random.nextInt(startYear, endYear + 1)
    val randomMonth = Random.nextInt(1, 13)
    val randomDay = Random.nextInt(1, 29) // Garantir que o dia seja válido para qualquer mês

    val randomDate = LocalDate.of(randomYear, randomMonth, randomDay)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return randomDate.format(formatter)
}

private fun buildCsvLine(
    email: String,
    loanAmount: Int,
    customerDateOfBirth: String,
    paymentTermInMonths: Int,
    interestRateType: String,
    variableRate: Double?
): String {
    // Remove espaços extras e garante que os campos sejam válidos
    return listOf(
        email.trim(),
        loanAmount.toString(),
        customerDateOfBirth.trim(),
        paymentTermInMonths.toString(),
        interestRateType.trim(),
        variableRate?.toString()?.trim() ?: "null" // Evitar nulo e garantir que a variável seja vazia se não houver valor
    ).joinToString(separator = ",")
}
