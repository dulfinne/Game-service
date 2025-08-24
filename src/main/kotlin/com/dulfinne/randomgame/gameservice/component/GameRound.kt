package com.dulfinne.randomgame.gameservice.component

import com.dulfinne.randomnumberstarter.annotation.InjectRandomInt
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class GameRound {
    @InjectRandomInt
    val guessedNumber: Int = 0
}