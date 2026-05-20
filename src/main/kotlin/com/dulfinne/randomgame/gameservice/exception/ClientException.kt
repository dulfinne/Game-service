package com.dulfinne.randomgame.gameservice.exception

import com.dulfinne.randomgame.gameservice.util.ExceptionKeys
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono

class ClientException(
        val errorCode: Int,
        override val message: String
) : RuntimeException(message) {

  companion object {
    fun fromResponse(response: ClientResponse): Mono<ClientException> {
      val code = response.statusCode().value()
      return response.bodyToMono(ErrorResponse::class.java)
        .map { body ->
          ClientException(
            code,
            ExceptionKeys.CLIENT_ERROR.format(body.message)
          )
        }
    }
  }
}