package com.dulfinne.randomgame.gameservice.client.config

import com.dulfinne.randomgame.gameservice.client.UserClient
import com.dulfinne.randomgame.gameservice.exception.ClientException
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.core.publisher.Mono

@Configuration
@EnableConfigurationProperties(ClientProperties::class)
class ClientConfig(val clientProperties: ClientProperties) {

  @Bean
  fun userClient(): UserClient {
    val webClient = WebClient.builder()
      .baseUrl(clientProperties.userService)
      .defaultStatusHandler(HttpStatusCode::isError) { response ->
        ClientException.fromResponse(response)
          .flatMap { Mono.error(it) }
      }
      .build()

    val factory = HttpServiceProxyFactory
      .builderFor(WebClientAdapter.create(webClient))
      .build()

    return factory.createClient(UserClient::class.java)
  }
}