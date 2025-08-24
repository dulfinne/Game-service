package com.dulfinne.randomgame.gameservice.exception


import com.dulfinne.randomgame.gameservice.util.ExceptionKeys
import com.dulfinne.randomgame.gameservice.util.ValidationKeys
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun handleValidationExceptions(ex: WebExchangeBindException): Map<String, String> {
        return ex.bindingResult.fieldErrors.associate { fieldError: FieldError ->
            fieldError.field to (fieldError.defaultMessage ?: ValidationKeys.DEFAULT_MESSAGE)
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException::class)
    suspend fun handleEntityNotFoundException(ex: EntityNotFoundException): ErrorResponse {
        return ErrorResponse(HttpStatus.CONFLICT, ex.message)
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ActionNotAllowedException::class)
    suspend fun handleActionNotAllowedException(ex: ActionNotAllowedException): ErrorResponse {
        return ErrorResponse(HttpStatus.CONFLICT, ex.message)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    suspend fun handleGlobalException(ex: Exception): ErrorResponse {
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionKeys.UNKNOWN_ERROR)
    }
}