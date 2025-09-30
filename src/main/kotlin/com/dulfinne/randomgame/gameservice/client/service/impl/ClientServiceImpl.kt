package com.dulfinne.randomgame.gameservice.client.service.impl

import com.dulfinne.randomgame.gameservice.client.UserClient
import com.dulfinne.randomgame.gameservice.client.dto.response.MoneyResponse
import com.dulfinne.randomgame.gameservice.client.service.ClientService
import org.springframework.stereotype.Service

@Service
class ClientServiceImpl(val userClient: UserClient) : ClientService {
  override suspend fun getUserBalance(username: String): MoneyResponse {
    return userClient.getUserBalance(username)
  }
}
