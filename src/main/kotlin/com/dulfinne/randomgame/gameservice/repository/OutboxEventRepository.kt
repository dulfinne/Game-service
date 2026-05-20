package com.dulfinne.randomgame.gameservice.repository

import com.dulfinne.randomgame.gameservice.entity.OutboxEvent
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OutboxEventRepository : CoroutineCrudRepository<OutboxEvent<*>, String> {
    @Query("{ 'payload.gameId': ?0 }")
    suspend fun findByGameId(gameId: String): OutboxEvent<*>?
}
