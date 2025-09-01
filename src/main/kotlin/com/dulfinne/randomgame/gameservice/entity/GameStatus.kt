package com.dulfinne.randomgame.gameservice.entity

import com.dulfinne.randomgame.gameservice.util.ExceptionKeys

enum class GameStatus(val id: Int) {
    PENDING(0),
    WON(1),
    LOST(2);

    companion object {
        fun fromId(id: Int): GameStatus =
            requireNotNull(entries.find { it.id == id }) {
                ExceptionKeys.GAME_STATUS_ID_INVALID.format(id)
            }
    }
}