package com.dulfinne.randomgame.gameservice.kafka.config

import com.dulfinne.randomgame.gameservice.util.CommonConstants
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig(val kafkaProperties: KafkaProperties) {

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val props = HashMap<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        return KafkaAdmin(props)
    }

    @Bean
    fun gamePaymentTopic(): NewTopic =
        TopicBuilder.name("${CommonConstants.OUTBOX_PREFIX}${kafkaProperties.topics.gamePayments}")
                .build()
}