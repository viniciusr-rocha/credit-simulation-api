package br.com.vinicius.creditsimulation.api.exception

import org.springframework.http.HttpStatus

class InvalidEnumException(
    val enumEntries: List<Enum<*>>,
    message: String = "Invalid enum value",
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
) : BaseException(httpStatus, message)
