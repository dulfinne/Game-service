package com.dulfinne.randomgame.gameservice.kafka.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.kafka")
class KafkaProperties {

    lateinit var bootstrapServers: String
    lateinit var producerId: String
    var retriesNum: Int = 0
    lateinit var topics: Topics

    class Topics {
        lateinit var gamePayments: String
    }
}