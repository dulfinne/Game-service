package com.dulfinne.randomgame.gameservice.client

import com.dulfinne.randomgame.gameservice.client.dto.response.MoneyResponse
import com.dulfinne.randomgame.gameservice.util.ApiPaths
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange

@HttpExchange(ApiPaths.USERS_BASE_URL)
interface UserClient {

    @GetExchange("${ApiPaths.USERNAME}${ApiPaths.BALANCE}")
    suspend fun getUserBalance(@PathVariable username: String): MoneyResponse
}
