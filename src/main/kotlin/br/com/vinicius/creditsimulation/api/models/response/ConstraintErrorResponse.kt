package br.com.vinicius.creditsimulation.api.models.response

data class ConstraintErrorResponse(
    val field: String,
    val message: String
)
