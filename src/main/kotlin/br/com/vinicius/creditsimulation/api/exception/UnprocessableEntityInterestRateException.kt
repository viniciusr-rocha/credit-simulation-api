package br.com.vinicius.creditsimulation.api.exception

import org.springframework.http.HttpStatus

class UnprocessableEntityInterestRateException(
    message: String,
    httpStatus: HttpStatus = HttpStatus.UNPROCESSABLE_ENTITY
) : BaseException(httpStatus, message)
