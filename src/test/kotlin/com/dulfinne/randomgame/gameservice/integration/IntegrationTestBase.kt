package com.dulfinne.randomgame.gameservice.integration

import com.dulfinne.randomgame.gameservice.util.HeaderConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestBase {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    companion object {
        @Container
        val container = MongoDBContainer(DockerImageName.parse("mongo:8.0.4"))

        @DynamicPropertySource
        fun mongoProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl)
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
}