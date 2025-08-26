package com.dulfinne.randomgame.gameservice.exception

class EntityNotFoundException(
    override val message: String
) : RuntimeException(message)
