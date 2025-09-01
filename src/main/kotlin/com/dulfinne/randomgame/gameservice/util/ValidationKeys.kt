package com.dulfinne.randomgame.gameservice.util

object ValidationKeys {
    const val AMOUNT_NOT_NULL = "Amount must not be null"
    const val AMOUNT_MIN = "Amount must be at least 5"
    const val AMOUNT_MAX = "Amount must be at most 200"
    const val USER_GUESS_NOT_NULL = "User guess must not be null"
    const val OFFSET_NOT_NEGATIVE = "Pagination request offset must be positive or zero"
    const val LIMIT_POSITIVE = "Pagination request limit must be positive"
    const val DEFAULT_MESSAGE = "Invalid value"
}