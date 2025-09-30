package com.dulfinne.randomgame.gameservice.integration

import com.dulfinne.randomgame.gameservice.client.dto.response.MoneyResponse
import com.dulfinne.randomgame.gameservice.dto.request.GameRequest
import com.dulfinne.randomgame.gameservice.dto.request.GuessRequest
import com.dulfinne.randomgame.gameservice.dto.response.GameResponse
import com.dulfinne.randomgame.gameservice.entity.GameStatus
import com.dulfinne.randomgame.gameservice.exception.ErrorResponse
import com.dulfinne.randomgame.gameservice.kafka.entity.Payment
import com.dulfinne.randomgame.gameservice.repository.GameRepository
import com.dulfinne.randomgame.gameservice.util.ApiPaths
import com.dulfinne.randomgame.gameservice.util.ExceptionKeys
import com.dulfinne.randomgame.gameservice.util.GameTestData
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.testcontainers.shaded.org.awaitility.Awaitility
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.jsonResponse
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import java.time.Duration

class GameServiceIT(
        val gameRepository: GameRepository,
        val kafkaConsumer: KafkaConsumer<String, Payment>,
) : IntegrationTestBase() {


  @BeforeEach
  fun setUp(): Unit = runBlocking {
    gameRepository.deleteAll()
  }

  @Nested
  inner class GetGame {

    @Test
    fun givenSameUserAuth_whenGetGame_thenReturnGameResponse(): Unit =
      runBlocking {
        gameRepository.save(
          GameTestData.getGame()
            .copy(userGuess = null)
        )
        val expected = GameTestData.getGameResponse()
          .copy(userGuess = null, guessedNumber = null)

        val result = buildRequest(
          GameTestData.USERNAME,
          HttpMethod.GET,
          "${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}"
        )
          .exchange()
          .expectStatus().isOk
          .expectBody(GameResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result).isEqualTo(expected)
      }

    @Test
    fun givenOtherUserAuth_whenGetGame_thenReturnErrorResponse(): Unit =
      runBlocking {
        gameRepository.save(GameTestData.getGame())

        val errorMessage = ExceptionKeys.GAME_NOT_FOUND.format(GameTestData.ID)

        val result = buildRequest(
          GameTestData.UNKNOWN_USERNAME,
          HttpMethod.GET,
          "${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}"
        )
          .exchange()
          .expectStatus().isNotFound
          .expectBody(ErrorResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result?.message).isEqualTo(errorMessage)
      }

    @Test
    fun givenUnknownGameId_whenGetGame_thenReturnErrorResponse(): Unit =
      runBlocking {
        val errorMessage = ExceptionKeys.GAME_NOT_FOUND.format(GameTestData.ID)

        val result = buildRequest(
          GameTestData.USERNAME,
          HttpMethod.GET,
          "${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}"
        )
          .exchange()
          .expectStatus().isNotFound
          .expectBody(ErrorResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result?.message).isEqualTo(errorMessage)
      }
  }

  @Nested
  inner class CreateGame {

    @Test
    fun givenValidData_whenCreateGame_thenReturnGameResponse(): Unit =
      runBlocking {
        val request = GameRequest(GameTestData.BID)
        val expected = GameTestData.getGameResponse()
          .copy(userGuess = null, guessedNumber = null)

        stubFor(
          get("${ApiPaths.USERS_BASE_URL}/${GameTestData.USERNAME}${ApiPaths.BALANCE}").willReturn(
            jsonResponse(
              MoneyResponse(GameTestData.USER_BALANCE_OK),
              HttpStatus.OK.value()
            )
          )
        )

        val result = buildRequest(
          GameTestData.USERNAME,
          HttpMethod.POST,
          ApiPaths.GAME_BASE_URL,
          request
        )
          .exchange()
          .expectStatus().isCreated
          .expectBody(GameResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result)
          .usingRecursiveComparison()
          .ignoringFields(GameTestData.ID_FIELD)
          .isEqualTo(expected)

        val game = result?.let {
          gameRepository.findById(it.id)
        }

        assertThat(game)
          .usingRecursiveComparison()
          .ignoringFields(
            GameTestData.ID_FIELD,
            GameTestData.GUESSED_NUMBER_FIELD
          )
          .isEqualTo(
            GameTestData.getGame()
              .copy(userGuess = null)
          )
      }

    @Test
    fun givenNotEnoughMoney_whenCreateGame_thenReturnGameResponse(): Unit =
      runBlocking {
        val request = GameRequest(GameTestData.BID)
        val expected = ErrorResponse(
          HttpStatus.CONFLICT,
          ExceptionKeys.NOT_ENOUGH_MONEY.format(GameTestData.BID)
        )

        stubFor(
          get("${ApiPaths.USERS_BASE_URL}/${GameTestData.USERNAME}${ApiPaths.BALANCE}").willReturn(
            jsonResponse(
              MoneyResponse(GameTestData.USER_BALANCE_LOW),
              HttpStatus.OK.value()
            )
          )
        )

        val result = buildRequest(
          GameTestData.USERNAME,
          HttpMethod.POST,
          ApiPaths.GAME_BASE_URL,
          request
        )
          .exchange()
          .expectStatus().isEqualTo(HttpStatus.CONFLICT)
          .expectBody(ErrorResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result)
          .isEqualTo(expected)

        val games = result?.let {
          gameRepository.findAll().toList()
        }

        assertThat(games).isEmpty()
      }

    @Test
    fun givenNotExistingUserWallet_whenCreateGame_thenReturnErrorResponse(): Unit =
      runBlocking {
        val request = GameRequest(GameTestData.BID)
        val response = ErrorResponse(HttpStatus.NOT_FOUND, "")

        stubFor(
          get("${ApiPaths.USERS_BASE_URL}/${GameTestData.USERNAME}${ApiPaths.BALANCE}").willReturn(
            jsonResponse(
              response,
              HttpStatus.NOT_FOUND.value()
            )
          )
        )

        val result = buildRequest(
          GameTestData.USERNAME,
          HttpMethod.POST,
          ApiPaths.GAME_BASE_URL,
          request
        )
          .exchange()
          .expectStatus().isNotFound
          .expectBody(ErrorResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result)
          .isEqualTo(response.copy(message = ExceptionKeys.CLIENT_ERROR.format("")))

        val games = result?.let {
          gameRepository.findAll().toList()
        }

        assertThat(games).isEmpty()
      }

    @Test
    fun givenNoRequestBody_whenCreateGame_thenReturnGameResponse(): Unit =
      runBlocking {
        val result = buildRequest(
          GameTestData.USERNAME,
          HttpMethod.POST,
          ApiPaths.GAME_BASE_URL
        )
          .exchange()
          .expectStatus().is5xxServerError
          .expectBody(ErrorResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result?.message).isEqualTo(ExceptionKeys.UNKNOWN_ERROR)
      }
  }

  @Nested
  inner class GuessNumber {

    @Test
    fun givenSameAuthAndGuessed_whenGuessNumber_thenReturnGameResponse(): Unit =
      runBlocking {
        gameRepository.save(
          GameTestData.getGame()
            .copy(userGuess = null)
        )

        val request = GuessRequest(GameTestData.USER_WIN_GUESS)
        val expected = GameTestData.getGameResponse()
          .copy(statusId = GameStatus.WON)

        val result = buildRequest(
          GameTestData.USERNAME,
          HttpMethod.POST,
          "${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}",
          request
        )
          .exchange()
          .expectStatus().isOk
          .expectBody(GameResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result).isEqualTo(expected)
        Awaitility.await()
          .atMost(Duration.ofSeconds(3))
          .untilAsserted {
            kafkaConsumer.poll(Duration.ofSeconds(100))
              .lastOrNull()
              ?.let {
                assertThat(it.key()).isEqualTo(GameTestData.USERNAME)
                assertThat(it.value()).isEqualTo(GameTestData.getPayment())
              }
          }
      }

    @Test
    fun givenOtherAuth_whenGuessNumber_thenReturnErrorResponse(): Unit =
      runBlocking {
        gameRepository.save(GameTestData.getGame())

        val request = GuessRequest(GameTestData.USER_WIN_GUESS)
        val errorMessage = ExceptionKeys.GAME_NOT_FOUND.format(GameTestData.ID)

        val result = buildRequest(
          GameTestData.UNKNOWN_USERNAME,
          HttpMethod.POST,
          "${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}",
          request
        )
          .exchange()
          .expectStatus().isNotFound
          .expectBody(ErrorResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result?.message).isEqualTo(errorMessage)
      }

    @Test
    fun givenUnknownGameId_whenGuessNumber_thenReturnErrorResponse(): Unit =
      runBlocking {
        val errorMessage = ExceptionKeys.GAME_NOT_FOUND.format(GameTestData.ID)
        val request = GuessRequest(GameTestData.USER_WIN_GUESS)

        val result = buildRequest(
          GameTestData.USERNAME,
          HttpMethod.POST,
          "${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}",
          request
        )
          .exchange()
          .expectStatus().isNotFound
          .expectBody(ErrorResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result?.message).isEqualTo(errorMessage)
      }

    @Test
    fun givenSameAuthAndNotGuessed_whenGuessNumber_thenReturnGameResponse(): Unit =
      runBlocking {
        gameRepository.save(
          GameTestData.getGame()
            .copy(userGuess = null)
        )

        val request = GuessRequest(GameTestData.USER_LOOSE_GUESS)
        val expected = GameTestData.getGameResponse()
          .copy(
            statusId = GameStatus.LOST,
            userGuess = GameTestData.USER_LOOSE_GUESS
          )
        val expectedPayment = GameTestData.getPayment()
          .copy(positiveFlag = false)

        val result = buildRequest(
          GameTestData.USERNAME,
          HttpMethod.POST,
          "${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}",
          request
        )
          .exchange()
          .expectStatus().isOk
          .expectBody(GameResponse::class.java)
          .returnResult()
          .responseBody

        assertThat(result).isEqualTo(expected)
        Awaitility.await()
          .atMost(Duration.ofSeconds(3))
          .untilAsserted {
            kafkaConsumer.poll(Duration.ofSeconds(100))
              .lastOrNull()
              ?.let {
                assertThat(it.key()).isEqualTo(GameTestData.USERNAME)
                assertThat(it.value()).isEqualTo(expectedPayment)
              }
          }
      }
  }
}
