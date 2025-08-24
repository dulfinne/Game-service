package com.dulfinne.randomgame.gameservice.dto.request

import com.dulfinne.randomgame.gameservice.util.ValidationKeys
import jakarta.validation.constraints.NotNull

data class GuessRequest(
    @field:NotNull(message = ValidationKeys.USER_GUESS_NOT_NULL)
    val userGuess: Int?,
)
