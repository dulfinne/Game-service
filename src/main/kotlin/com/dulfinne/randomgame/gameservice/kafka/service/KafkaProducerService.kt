package com.dulfinne.randomgame.gameservice.kafka.service

import com.dulfinne.randomgame.gameservice.kafka.config.KafkaProperties
import com.dulfinne.randomgame.gameservice.kafka.entity.Payment
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    val kafkaTemplate: KafkaTemplate<String, Any>,
    val kafkaProperties: KafkaProperties
) {
    fun sendGamePayment(request: Payment) =
        kafkaTemplate.executeInTransaction { template ->
            template.send(kafkaProperties.topics.gamePayments, request.username, request)
        }
}