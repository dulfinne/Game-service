package com.dulfinne.randomgame.gameservice.service.impl

import com.dulfinne.randomgame.gameservice.component.GameRound
import com.dulfinne.randomgame.gameservice.dto.request.GameRequest
import com.dulfinne.randomgame.gameservice.dto.request.GuessRequest
import com.dulfinne.randomgame.gameservice.dto.request.PaginationRequest
import com.dulfinne.randomgame.gameservice.dto.response.GameResponse
import com.dulfinne.randomgame.gameservice.entity.Game
import com.dulfinne.randomgame.gameservice.entity.GameStatus
import com.dulfinne.randomgame.gameservice.exception.ActionNotAllowedException
import com.dulfinne.randomgame.gameservice.exception.EntityNotFoundException
import com.dulfinne.randomgame.gameservice.kafka.entity.Payment
import com.dulfinne.randomgame.gameservice.mapper.toGame
import com.dulfinne.randomgame.gameservice.mapper.toResponse
import com.dulfinne.randomgame.gameservice.repository.GameRepository
import com.dulfinne.randomgame.gameservice.repository.GameSortingRepository
import com.dulfinne.randomgame.gameservice.service.GameService
import com.dulfinne.randomgame.gameservice.service.PaymentSender
import com.dulfinne.randomgame.gameservice.util.ExceptionKeys
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.stereotype.Service
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@Service
class GameServiceImpl(
    val gameRepository: GameRepository,
    val gameSortingRepository: GameSortingRepository,
    val paymentSender: PaymentSender
) : GameService {

    @Lookup
    fun getGameRound(): GameRound = throw UnsupportedOperationException()

    @Transactional(readOnly = true)
    override suspend fun getAllGames(request: PaginationRequest): List<GameResponse> =
        gameSortingRepository.findAllBy(PageRequest.of(request.offset, request.limit))
                .collectList()
                .awaitSingle()
                .map { it.toResponse() }

    @Transactional(readOnly = true)
    override suspend fun getAllGamesByUsername(
        username: String,
        request: PaginationRequest
    ): List<GameResponse> =
        gameSortingRepository.findAllByUsername(username,
            PageRequest.of(request.offset, request.limit))
                .collectList()
                .awaitSingle()
                .map { it.toResponse() }

    @Transactional(readOnly = true)
    override suspend fun getGameById(username: String, gameId: String): GameResponse {
        val game = getGameIfExists(gameId)
        validateUser(username, game)
        return game.toResponse()
    }

    @Transactional
    override suspend fun createGame(username: String, request: GameRequest): GameResponse {
        val secretNumber = getGameRound().guessedNumber
        val game = request.toGame(username, secretNumber)
        val savedGame = gameRepository.save(game)
        return savedGame.toResponse()
    }

    @Transactional
    override suspend fun guessNumber(
        username: String,
        gameId: String,
        request: GuessRequest
    ): GameResponse {
        val game = getGameIfExists(gameId)
        validateUser(username, game)
        checkCanGuess(game)

        val userGuess = request.userGuess

        val hasWon = userGuess == game.guessedNumber
        val status = if (hasWon) GameStatus.WON else GameStatus.LOST

        game.apply {
            this.userGuess = userGuess
            statusId = status.id
        }

        val savedGame = gameRepository.save(game)
        paymentSender.send(Payment(
            amount = game.bid,
            username = game.username,
            positiveFlag = hasWon))

        return savedGame.toResponse()
    }

    private suspend fun getGameIfExists(gameId: String): Game {
        return gameRepository.findById(gameId)
            ?: throw EntityNotFoundException(ExceptionKeys.GAME_NOT_FOUND.format(gameId))
    }

    private fun validateUser(username: String, game: Game) {
        if (username != game.username) {
            throw EntityNotFoundException(ExceptionKeys.GAME_NOT_FOUND.format(game.id))
        }
    }

    private fun checkCanGuess(game: Game) {
        if (game.statusId != GameStatus.PENDING.id) {
            throw ActionNotAllowedException(ExceptionKeys.GAME_IS_FINISHED.format(game.id))
        }
    }
}
