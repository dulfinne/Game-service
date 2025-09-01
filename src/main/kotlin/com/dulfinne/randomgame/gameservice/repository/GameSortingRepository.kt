package com.dulfinne.randomgame.gameservice.repository

import com.dulfinne.randomgame.gameservice.entity.Game
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux

interface GameSortingRepository : ReactiveSortingRepository<Game, String> {
    fun findAllBy(pageable: Pageable): Flux<Game>
    fun findAllByUsername(username: String, pageable: Pageable): Flux<Game>
}