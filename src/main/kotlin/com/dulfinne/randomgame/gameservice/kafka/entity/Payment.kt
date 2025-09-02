package com.dulfinne.randomgame.gameservice.kafka.entity

import java.math.BigDecimal

data class Payment(
    val username: String,
    val amount: BigDecimal,
    val positiveFlag: Boolean,
)
