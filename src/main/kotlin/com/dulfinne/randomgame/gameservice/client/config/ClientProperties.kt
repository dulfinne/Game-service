package com.dulfinne.randomgame.gameservice.client.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.client")
class ClientProperties {
    lateinit var userService: String
}
