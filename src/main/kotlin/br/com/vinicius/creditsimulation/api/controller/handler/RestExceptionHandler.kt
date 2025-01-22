package br.com.vinicius.creditsimulation.api.controller.handler

import br.com.vinicius.creditsimulation.api.exception.BaseException
import br.com.vinicius.creditsimulation.api.exception.InvalidEnumException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpServerErrorException.InternalServerError
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.UnsupportedMediaTypeStatusException
import java.util.concurrent.TimeoutException

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(InvalidEnumException::class)
    fun handleInvalidEnumException(
        request: ServerWebExchange,
        ex: InvalidEnumException
    ): ProblemDetail {
        val enums = ex.enumEntries
        return createProblemDetail(
            HttpStatus.REQUEST_TIMEOUT,
            ex.message,
            request,
            mapOf("validEnums" to enums)
        )
    }

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(
        request: ServerWebExchange,
        ex: BaseException
    ): ProblemDetail =
        createProblemDetail(
            ex.httpStatus,
            ex.message,
            request
        )

    @ExceptionHandler(UnsupportedMediaTypeStatusException::class)
    fun handleUnsupportedMediaTypeStatusException(
        request: ServerWebExchange,
        ex: UnsupportedMediaTypeStatusException
    ): ProblemDetail {
        logger.error(ex) { "Unsupported media type status: ${ex.message}" }
        return createProblemDetail(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "Unsupported media type. Only CSV files are accepted. " +
                "Please ensure your file is in CSV format and try again.",
            request
        )
    }

    @ExceptionHandler(TimeoutException::class)
    fun handleTimeoutException(
        request: ServerWebExchange,
        ex: TimeoutException
    ): ProblemDetail {
        logger.error(ex) { "Timeout exception: ${ex.message}" }
        return createProblemDetail(
            HttpStatus.REQUEST_TIMEOUT,
            "Timeout reached. Please try again later.",
            request
        )
    }

    @ExceptionHandler(value = [Exception::class, InternalServerError::class])
    fun handleInternalException(
        request: ServerWebExchange,
        ex: Exception
    ): ProblemDetail {
        ex.printStackTrace()
        return createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please try again later. If the problem persists, contact support.",
            request
        )
    }

    private companion object {
        private val logger = KotlinLogging.logger {}

        private fun createProblemDetail(
            httpStatus: HttpStatus,
            message: String,
            request: ServerWebExchange,
            errors: Any? = null
        ): ProblemDetail {
            val problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, message)
            problemDetail.type = request.request.uri
            problemDetail.title = httpStatus.reasonPhrase
            problemDetail.properties =
                mapOf(
                    "errors" to errors
                )
            return problemDetail
        }
    }
}
