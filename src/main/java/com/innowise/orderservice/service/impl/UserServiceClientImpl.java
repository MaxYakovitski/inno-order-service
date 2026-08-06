package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dto.UserInfoDto;
import com.innowise.orderservice.service.UserServiceClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class UserServiceClientImpl implements UserServiceClient {

  private final RestClient restClient;

  @Cacheable(value = "users", key = "#userId")
  @Override
  public UserInfoDto getById(UUID userId, String token) {
    return restClient
        .get()
        .uri("/api/users/{id}", userId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve()
        .body(UserInfoDto.class);
  }
}
