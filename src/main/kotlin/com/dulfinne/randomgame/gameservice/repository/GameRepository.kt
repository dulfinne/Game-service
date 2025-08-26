package com.dulfinne.randomgame.gameservice.repository

import com.dulfinne.randomgame.gameservice.entity.Game
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface GameRepository : CoroutineCrudRepository<Game, String>