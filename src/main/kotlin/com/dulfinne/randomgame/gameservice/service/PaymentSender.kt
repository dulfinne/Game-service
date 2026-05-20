package com.dulfinne.randomgame.gameservice.service

import com.dulfinne.randomgame.gameservice.kafka.entity.Payment

interface PaymentSender {
    suspend fun send(payment: Payment)
}