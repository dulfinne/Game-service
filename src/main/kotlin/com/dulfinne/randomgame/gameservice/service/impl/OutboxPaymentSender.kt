package com.dulfinne.randomgame.gameservice.service.impl

import com.dulfinne.randomgame.gameservice.entity.OutboxEvent
import com.dulfinne.randomgame.gameservice.kafka.config.KafkaProperties
import com.dulfinne.randomgame.gameservice.kafka.entity.Payment
import com.dulfinne.randomgame.gameservice.repository.OutboxEventRepository
import com.dulfinne.randomgame.gameservice.service.PaymentSender
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
@ConditionalOnProperty(name = ["payment.sender.type"],
    havingValue = "outbox",
    matchIfMissing = true)
class OutboxPaymentSender(
    private val outboxRepository: OutboxEventRepository,
    private val properties: KafkaProperties
) : PaymentSender {
    override suspend fun send(payment: Payment) {
        outboxRepository.save(OutboxEvent(id = null,
            aggregatetype = properties.topics.gamePayments,
            aggregateid = payment.username,
            payload = payment))
    }
}