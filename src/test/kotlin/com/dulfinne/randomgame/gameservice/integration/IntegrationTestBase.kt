package com.dulfinne.randomgame.gameservice.integration

import com.dulfinne.randomgame.gameservice.util.HeaderConstants
import io.restassured.RestAssured.given
import io.restassured.specification.RequestSpecification
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestBase {

    @LocalServerPort
    private var port: Int = 0

    companion object {
        @Container
        val container = MongoDBContainer(DockerImageName.parse("mongo:8.0.4"))

        @DynamicPropertySource
        fun mongoProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.uri", container::getConnectionString)
        }
    }

    protected fun withAuth(username: String): RequestSpecification {
        return given()
                .header(HeaderConstants.USERNAME_HEADER, username)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .port(port)
    }
}