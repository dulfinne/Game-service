package com.dulfinne.randomgame.gameservice.mapper

import com.dulfinne.randomgame.gameservice.dto.request.GameRequest
import com.dulfinne.randomgame.gameservice.dto.response.GameResponse
import com.dulfinne.randomgame.gameservice.entity.Game
import com.dulfinne.randomgame.gameservice.entity.GameStatus

fun Game.toResponse() = GameResponse(
    id = id!!,
    username = username,
    guessedNumber = this.guessedNumber.takeIf { statusId != GameStatus.PENDING.id },
    userGuess = userGuess,
    bid = bid,
    statusId = GameStatus.fromId(statusId)
)

fun GameRequest.toGame(username: String, guessedNumber: Int) = Game(
    id = null,
    username = username,
    guessedNumber = guessedNumber,
    userGuess = null,
    bid = bid,
    statusId = GameStatus.PENDING.id
)