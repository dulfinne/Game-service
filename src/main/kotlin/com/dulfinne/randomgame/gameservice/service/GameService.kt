package com.dulfinne.randomgame.gameservice.service

import com.dulfinne.randomgame.gameservice.dto.request.GameRequest
import com.dulfinne.randomgame.gameservice.dto.request.GuessRequest
import com.dulfinne.randomgame.gameservice.dto.request.PaginationRequest
import com.dulfinne.randomgame.gameservice.dto.response.GameResponse

interface GameService {
    suspend fun getAllGames(request: PaginationRequest): List<GameResponse>
    suspend fun getAllGamesByUsername(
        username: String,
        request: PaginationRequest
    ): List<GameResponse>

    suspend fun getGameById(username: String, gameId: String): GameResponse
    suspend fun createGame(username: String, request: GameRequest): GameResponse
    suspend fun guessNumber(username: String, gameId: String, request: GuessRequest): GameResponse
}