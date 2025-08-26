package com.dulfinne.randomgame.gameservice.controller

import com.dulfinne.randomgame.gameservice.dto.request.GameRequest
import com.dulfinne.randomgame.gameservice.dto.request.GuessRequest
import com.dulfinne.randomgame.gameservice.dto.request.PaginationRequest
import com.dulfinne.randomgame.gameservice.dto.response.GameResponse
import com.dulfinne.randomgame.gameservice.service.GameService
import com.dulfinne.randomgame.gameservice.util.ApiPaths
import com.dulfinne.randomgame.gameservice.util.HeaderConstants
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiPaths.GAME_BASE_URL)
class GameController(val gameService: GameService) {

    @GetMapping(ApiPaths.ALL)
    suspend fun getAllGames(@Valid request: PaginationRequest): List<GameResponse> =
        gameService.getAllGames(request)

    @GetMapping
    suspend fun getAllGamesByUsername(
        @RequestHeader(HeaderConstants.USERNAME_HEADER) username: String,
        @Valid request: PaginationRequest
    ): List<GameResponse> =
        gameService.getAllGamesByUsername(username, request)

    @GetMapping(ApiPaths.GAME_ID)
    suspend fun getGameById(
        @RequestHeader(HeaderConstants.USERNAME_HEADER) username: String,
        @PathVariable gameId: String
    ): GameResponse =
        gameService.getGameById(username, gameId)

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    suspend fun createGame(
        @RequestHeader(HeaderConstants.USERNAME_HEADER) username: String,
        @RequestBody @Valid request: GameRequest
    ): GameResponse =
        gameService.createGame(username, request)

    @PostMapping("${ApiPaths.GAME_ID}${ApiPaths.GUESS}")
    suspend fun guessNumber(
        @RequestHeader(HeaderConstants.USERNAME_HEADER) username: String,
        @PathVariable gameId: String,
        @RequestBody @Valid request: GuessRequest
    ): GameResponse =
        gameService.guessNumber(username, gameId, request)
}