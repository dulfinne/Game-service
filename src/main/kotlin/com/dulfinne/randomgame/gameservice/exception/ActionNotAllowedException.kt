package com.dulfinne.randomgame.gameservice.exception

class ActionNotAllowedException(
    override val message: String
) : RuntimeException(message)