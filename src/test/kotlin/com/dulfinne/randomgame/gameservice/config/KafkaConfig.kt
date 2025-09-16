package com.dulfinne.randomgame.gameservice.config

import com.dulfinne.randomgame.gameservice.integration.IntegrationTestBase.Companion.kafkaContainer
import com.dulfinne.randomgame.gameservice.kafka.config.KafkaProperties
import com.dulfinne.randomgame.gameservice.kafka.entity.Payment
import com.dulfinne.randomgame.gameservice.util.CommonConstants
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import java.util.Properties

@Configuration
class KafkaConfig(val kafkaProperties: KafkaProperties) {

    @Bean
    fun createPaymentConsumer(): KafkaConsumer<String, Payment> {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaContainer.bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = "test-group"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java.name
        props["spring.json.value.default.type"] = Payment::class.java.name
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        val consumer = KafkaConsumer<String, Payment>(props)
        consumer.subscribe(listOf("${CommonConstants.OUTBOX_PREFIX}${kafkaProperties.topics.gamePayments}"))
        return consumer
    }
}
