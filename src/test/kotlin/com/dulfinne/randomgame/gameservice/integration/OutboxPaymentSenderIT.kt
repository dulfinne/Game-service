package com.dulfinne.randomgame.gameservice.integration

import com.dulfinne.randomgame.gameservice.dto.request.GuessRequest
import com.dulfinne.randomgame.gameservice.dto.response.GameResponse
import com.dulfinne.randomgame.gameservice.entity.GameStatus
import com.dulfinne.randomgame.gameservice.entity.OutboxEvent
import com.dulfinne.randomgame.gameservice.repository.GameRepository
import com.dulfinne.randomgame.gameservice.repository.OutboxEventRepository
import com.dulfinne.randomgame.gameservice.util.ApiPaths
import com.dulfinne.randomgame.gameservice.util.GameTestData
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = ["payment.sender.type=outbox"])
class OutboxPaymentSenderIT(
    val gameRepository: GameRepository,
    val outboxRepository: OutboxEventRepository
) : IntegrationTestBase() {

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        gameRepository.deleteAll()
        outboxRepository.deleteAll()
    }

    @Nested
    inner class GuessNumber {

        @Test
        fun givenSameAuthAndGuessed_whenGuessNumber_thenReturnGameResponse(): Unit = runBlocking {
            gameRepository.save(GameTestData.getGame()
                    .copy(userGuess = null))

            val request = GuessRequest(GameTestData.USER_WIN_GUESS)
            val expected = GameTestData.getGameResponse()
                    .copy(statusId = GameStatus.WON)

            val expectedPayment = GameTestData.getPayment()
            val expectedOutboxEvent = OutboxEvent(id = null,
                aggregatetype = kafkaProperties.topics.gamePayments,
                aggregateid = expectedPayment.username,
                payload = expectedPayment)

            val result = buildRequest(GameTestData.USERNAME,
                HttpMethod.POST,
                "${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}",
                request)
                    .exchange()
                    .expectStatus().isOk
                    .expectBody(GameResponse::class.java)
                    .returnResult()
                    .responseBody

            assertThat(result).isEqualTo(expected)

            val paymentOutbox = result?.let {
                outboxRepository.findByGameId(it.id)
            }

            assertThat(paymentOutbox)
                    .usingRecursiveComparison()
                    .ignoringFields(GameTestData.ID_FIELD, GameTestData.CREATED_AT_FIELD)
                    .isEqualTo(expectedOutboxEvent)
        }

        @Test
        fun givenSameAuthAndNotGuessed_whenGuessNumber_thenReturnGameResponse(): Unit =
            runBlocking {
                gameRepository.save(GameTestData.getGame()
                        .copy(userGuess = null))

                val request = GuessRequest(GameTestData.USER_LOOSE_GUESS)
                val expected = GameTestData.getGameResponse()
                        .copy(statusId = GameStatus.LOST, userGuess = GameTestData.USER_LOOSE_GUESS)
                val expectedPayment = GameTestData.getPayment()
                        .copy(positiveFlag = false)
                val expectedOutboxEvent = OutboxEvent(id = null,
                    aggregatetype = kafkaProperties.topics.gamePayments,
                    aggregateid = expectedPayment.username,
                    payload = expectedPayment)

                val result = buildRequest(GameTestData.USERNAME,
                    HttpMethod.POST,
                    "${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}",
                    request)
                        .exchange()
                        .expectStatus().isOk
                        .expectBody(GameResponse::class.java)
                        .returnResult()
                        .responseBody

                assertThat(result).isEqualTo(expected)

                val paymentOutbox = result?.let {
                    outboxRepository.findByGameId(it.id)
                }

                assertThat(paymentOutbox)
                        .usingRecursiveComparison()
                        .ignoringFields(GameTestData.ID_FIELD, GameTestData.CREATED_AT_FIELD)
                        .isEqualTo(expectedOutboxEvent)
            }
    }
}
