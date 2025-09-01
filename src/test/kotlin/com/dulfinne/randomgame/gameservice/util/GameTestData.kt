package com.dulfinne.randomgame.gameservice.util

import com.dulfinne.randomgame.gameservice.dto.response.GameResponse
import com.dulfinne.randomgame.gameservice.entity.Game
import com.dulfinne.randomgame.gameservice.entity.GameStatus
import java.math.BigDecimal

object GameTestData {

    const val ID = "id1"
    const val USERNAME = "alex"
    const val GUESSED_NUMBER = 13
    const val USER_WIN_GUESS = GUESSED_NUMBER
    const val USER_LOOSE_GUESS = GUESSED_NUMBER + 1

    val BID = BigDecimal.valueOf(20)

    const val UNKNOWN_USERNAME = "unknown"

    const val ID_FIELD = "id"
    const val GUESSED_NUMBER_FIELD = "guessedNumber"

    fun getGame() = Game(ID, USERNAME, GUESSED_NUMBER, USER_WIN_GUESS, BID, GameStatus.PENDING.id)

    fun getGameResponse() =
        GameResponse(ID, USERNAME, GUESSED_NUMBER, USER_WIN_GUESS, BID, GameStatus.PENDING)
}