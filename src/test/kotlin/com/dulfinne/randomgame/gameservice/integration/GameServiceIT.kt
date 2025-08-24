package com.dulfinne.randomgame.gameservice.integration

import com.dulfinne.randomgame.gameservice.dto.request.GameRequest
import com.dulfinne.randomgame.gameservice.dto.request.GuessRequest
import com.dulfinne.randomgame.gameservice.dto.response.GameResponse
import com.dulfinne.randomgame.gameservice.entity.GameStatus
import com.dulfinne.randomgame.gameservice.repository.GameRepository
import com.dulfinne.randomgame.gameservice.util.ApiPaths
import com.dulfinne.randomgame.gameservice.util.ExceptionKeys
import com.dulfinne.randomgame.gameservice.util.GameTestData
import io.restassured.http.ContentType
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GameServiceIT(val gameRepository: GameRepository) : IntegrationTestBase() {

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        gameRepository.deleteAll()
                .awaitSingleOrNull()
    }

    @Nested
    inner class GetGame {

        @Test
        fun givenSameUserAuth_whenGetGame_thenReturnGameResponse(): Unit = runBlocking {
            gameRepository.save(GameTestData.getGame()
                    .copy(userGuess = null))
                    .awaitSingle()
            val expected = GameTestData.getGameResponse()
                    .copy(userGuess = null, guessedNumber = null)

            val result = withAuth(GameTestData.USERNAME)
                    .`when`()
                    .get("${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .`as`(GameResponse::class.java)

            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun givenOtherUserAuth_whenGetGame_thenReturnErrorResponse(): Unit = runBlocking {
            gameRepository.save(GameTestData.getGame())
                    .awaitSingle()

            val errorMessage = ExceptionKeys.GAME_NOT_FOUND.format(GameTestData.ID)

            withAuth(GameTestData.UNKNOWN_USERNAME)
                    .`when`()
                    .get("${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString(errorMessage))
                    .extract()
        }

        @Test
        fun givenUnknownGameId_whenGetGame_thenReturnErrorResponse(): Unit = runBlocking {
            val errorMessage = ExceptionKeys.GAME_NOT_FOUND.format(GameTestData.ID)

            withAuth(GameTestData.USERNAME)
                    .`when`()
                    .get("${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString(errorMessage))
                    .extract()
        }
    }

    @Nested
    inner class CreateGame {

        @Test
        fun givenValidData_whenCreateGame_thenReturnGameResponse(): Unit = runBlocking {
            val request = GameRequest(GameTestData.BID)
            val expected = GameTestData.getGameResponse()
                    .copy(userGuess = null, guessedNumber = null)

            val result = withAuth(GameTestData.USERNAME)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .`when`()
                    .post(ApiPaths.GAME_BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract()
                    .`as`(GameResponse::class.java)

            assertThat(result)
                    .usingRecursiveComparison()
                    .ignoringFields(GameTestData.ID_FIELD)
                    .isEqualTo(expected)

            val game = gameRepository.findById(result.id)
                    .awaitSingle()

            assertThat(game)
                    .usingRecursiveComparison()
                    .ignoringFields(GameTestData.ID_FIELD, GameTestData.GUESSED_NUMBER_FIELD)
                    .isEqualTo(GameTestData.getGame()
                            .copy(userGuess = null))
        }
    }

    @Nested
    inner class GuessNumber {

        @Test
        fun givenSameAuthAndGuessed_whenGuessNumber_thenReturnGameResponse(): Unit = runBlocking {
            gameRepository.save(GameTestData.getGame()
                    .copy(userGuess = null))
                    .awaitSingle()

            val request = GuessRequest(GameTestData.USER_WIN_GUESS)

            val expected = GameTestData.getGameResponse()
                    .copy(statusId = GameStatus.WON)

            val result = withAuth(GameTestData.USERNAME)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .`when`()
                    .post("${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .extract()
                    .`as`(GameResponse::class.java)

            assertThat(result).isEqualTo(expected)
        }

        @Test
        fun givenOtherAuth_whenGuessNumber_thenReturnErrorResponse(): Unit = runBlocking {
            gameRepository.save(GameTestData.getGame())
                    .awaitSingle()

            val request = GuessRequest(GameTestData.USER_WIN_GUESS)
            val errorMessage = ExceptionKeys.GAME_NOT_FOUND.format(GameTestData.ID)

            withAuth(GameTestData.UNKNOWN_USERNAME)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .`when`()
                    .post("${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString(errorMessage))
                    .extract()
        }

        @Test
        fun givenUnknownGameId_whenGuessNumber_thenReturnErrorResponse(): Unit = runBlocking {
            val errorMessage = ExceptionKeys.GAME_NOT_FOUND.format(GameTestData.ID)
            val request = GuessRequest(GameTestData.USER_WIN_GUESS)

            withAuth(GameTestData.USERNAME)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .`when`()
                    .post("${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString(errorMessage))
                    .extract()
        }

        @Test
        fun givenSameAuthAndNotGuessed_whenGuessNumber_thenReturnGameResponse(): Unit =
            runBlocking {
                gameRepository.save(GameTestData.getGame()
                        .copy(userGuess = null))
                        .awaitSingle()

                val request = GuessRequest(GameTestData.USER_LOOSE_GUESS)

                val expected = GameTestData.getGameResponse()
                        .copy(statusId = GameStatus.LOST, userGuess = GameTestData.USER_LOOSE_GUESS)

                val result = withAuth(GameTestData.USERNAME)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .`when`()
                        .post("${ApiPaths.GAME_BASE_URL}/${GameTestData.ID}${ApiPaths.GUESS}")
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .`as`(GameResponse::class.java)

                assertThat(result).isEqualTo(expected)
            }
    }
}