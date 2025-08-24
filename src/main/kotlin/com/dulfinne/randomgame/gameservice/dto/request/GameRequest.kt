package com.dulfinne.randomgame.gameservice.dto.request

import com.dulfinne.randomgame.gameservice.util.ValidationKeys
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class GameRequest(
    @field:NotNull(message = ValidationKeys.AMOUNT_NOT_NULL)
    @field:DecimalMin(value = "5", message = ValidationKeys.AMOUNT_MIN)
    @field:DecimalMax(value = "200", message = ValidationKeys.AMOUNT_MAX)
    val bid: BigDecimal?,
)
