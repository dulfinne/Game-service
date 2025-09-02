package com.dulfinne.randomgame.gameservice.integration

import com.dulfinne.randomgame.gameservice.kafka.config.KafkaProperties
import com.dulfinne.randomgame.gameservice.kafka.entity.Payment
import com.dulfinne.randomgame.gameservice.util.HeaderConstants
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.util.UUID
import java.util.Properties

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestBase {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @Autowired
    protected lateinit var kafkaProperties: KafkaProperties

    companion object {
        @Container
        val container = MongoDBContainer(DockerImageName.parse("mongo:8.0.4"))

        @Container
        val kafkaContainer = KafkaContainer(DockerImageName.parse("apache/kafka:3.9.1"))

        @JvmStatic
        @DynamicPropertySource
        fun testProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri", container::getConnectionString)
            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers)
        }
    }

    protected fun buildRequest(
        username: String,
        method: HttpMethod,
        uri: String,
        body: Any? = null
    ): WebTestClient.RequestHeadersSpec<*> {
        val request = webTestClient.method(method)
                .uri(uri)
                .header(HeaderConstants.USERNAME_HEADER, username)
                .contentType(MediaType.APPLICATION_JSON)

        return body?.let { request.bodyValue(it) } ?: request
    }

    protected fun createPaymentConsumer(): KafkaConsumer<String, Payment> {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaContainer.bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = "test-group-payment-${UUID.randomUUID()}"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java.name
        props["spring.json.value.default.type"] = Payment::class.java.name
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        val consumer = KafkaConsumer<String, Payment>(props)
        consumer.subscribe(listOf(kafkaProperties.topics.gamePayments))
        return consumer
    }
}