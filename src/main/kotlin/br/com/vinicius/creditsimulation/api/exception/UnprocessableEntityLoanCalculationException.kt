package br.com.vinicius.creditsimulation.api.exception

import org.springframework.http.HttpStatus

class UnprocessableEntityLoanCalculationException(
    message: String,
    cause: Throwable,
    httpStatus: HttpStatus = HttpStatus.UNPROCESSABLE_ENTITY
) : BaseException(httpStatus, message, cause)
