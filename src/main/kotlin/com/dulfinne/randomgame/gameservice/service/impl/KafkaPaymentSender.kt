package com.dulfinne.randomgame.gameservice.service.impl

import com.dulfinne.randomgame.gameservice.kafka.entity.Payment
import com.dulfinne.randomgame.gameservice.kafka.service.KafkaProducerService
import com.dulfinne.randomgame.gameservice.service.PaymentSender
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["payment.sender.type"], havingValue = "kafka", matchIfMissing = true)
class KafkaPaymentSender(private val kafkaService: KafkaProducerService) : PaymentSender {
    override suspend fun send(payment: Payment) {
        kafkaService.sendGamePayment(payment)
    }
}