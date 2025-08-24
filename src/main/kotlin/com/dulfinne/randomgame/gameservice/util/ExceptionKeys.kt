package com.dulfinne.randomgame.gameservice.util

object ExceptionKeys {
    const val GAME_NOT_FOUND = "Game not found: gameId = %s"
    const val GAME_IS_FINISHED = "Cannot make a guess: game #%s is already finished"
    const val GAME_STATUS_ID_INVALID = "Invalid id for GameStatus: %d"
    const val UNKNOWN_ERROR = "An unknown error has occurred..."
}