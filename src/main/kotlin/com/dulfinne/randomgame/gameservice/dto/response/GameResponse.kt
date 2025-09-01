package com.dulfinne.randomgame.gameservice.dto.response

import com.dulfinne.randomgame.gameservice.entity.GameStatus
import java.math.BigDecimal

data class GameResponse(
    val id: String,
    val username: String,
    val guessedNumber: Int?,
    val userGuess: Int?,
    val bid: BigDecimal,
    val statusId: GameStatus,
)
