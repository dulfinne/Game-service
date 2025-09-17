package com.dulfinne.randomgame.gameservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager

@Configuration
class MongoConfig {

    @Bean
    fun transactionManager(dbFactory: ReactiveMongoDatabaseFactory) =
        ReactiveMongoTransactionManager(dbFactory)
}