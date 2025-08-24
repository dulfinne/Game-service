package com.dulfinne.randomgame.gameservice.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document("game")
data class Game(
    @Id
    val id: String?,

    @Indexed
    val username: String,
    val guessedNumber: Int,
    var userGuess: Int?,
    val bid: BigDecimal,
    var statusId: Int
)

