package com.dulfinne.randomgame.gameservice.client.service

import com.dulfinne.randomgame.gameservice.client.dto.response.MoneyResponse

interface ClientService {
  suspend fun getUserBalance(username: String): MoneyResponse
}