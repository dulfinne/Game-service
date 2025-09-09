package com.dulfinne.randomgame.gameservice.repository

import com.dulfinne.randomgame.gameservice.entity.OutboxEvent
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OutboxEventRepository : CoroutineCrudRepository<OutboxEvent<*>, String>
