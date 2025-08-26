package com.dulfinne.randomgame.gameservice.dto.request

import com.dulfinne.randomgame.gameservice.util.ValidationKeys
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero

data class PaginationRequest(
    @field:PositiveOrZero(message = ValidationKeys.OFFSET_NOT_NEGATIVE)
    var offset: Int = 0,

    @field:Positive(message = ValidationKeys.LIMIT_POSITIVE)
    var limit: Int = 10,
)
