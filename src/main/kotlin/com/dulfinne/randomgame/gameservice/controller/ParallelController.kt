package com.dulfinne.randomgame.gameservice.controller

import com.dulfinne.randomgame.gameservice.dto.request.ParallelRequest
import com.dulfinne.randomgame.gameservice.util.ApiPaths
import jakarta.validation.Valid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.time.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping(ApiPaths.PARALLEL_BASE_URL)
class ParallelController(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    @GetMapping
    suspend fun getParallelNonBlocking(@Valid request: ParallelRequest): String = coroutineScope {
        val task1 = async {
            delay(Duration.ofMillis(request.firstDelay))
            "Result 1"
        }

        val task2 = async {
            delay(Duration.ofMillis(request.secondDelay))
            "Result 2"
        }

        "Results: ${task1.await()}, ${task2.await()}"
    }

    @GetMapping(ApiPaths.BLOCKING)
    suspend fun getParallelBlocking(@Valid request: ParallelRequest): String = coroutineScope {
        val task1 = async(ioDispatcher) {
            Thread.sleep(request.firstDelay)
            "Result 1"
        }

        val task2 = async(ioDispatcher) {
            Thread.sleep(request.secondDelay)
            "Result 2"
        }

        "Results: ${task1.await()}, ${task2.await()}"
    }
}
