package com.dulfinne.randomgame.gameservice.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("outbox_event")
data class OutboxEvent<T>(
    @Id
    val id: String?,

    val aggregatetype: String,
    val aggregateid: String,
    val payload: T,

    @Indexed(expireAfter = "5m")
    val createdAt: Instant = Instant.now()
)
