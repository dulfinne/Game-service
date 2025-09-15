package com.dulfinne.randomgame.gameservice.dto.request

import jakarta.validation.constraints.Positive

data class ParallelRequest(
    @field:Positive
    val firstDelay: Long,

    @field:Positive
    val secondDelay: Long,
)
