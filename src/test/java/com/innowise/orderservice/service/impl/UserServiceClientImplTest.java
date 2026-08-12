package com.innowise.orderservice.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

import com.innowise.orderservice.dto.UserInfoDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class UserServiceClientImplTest {

  @Mock private RestClient restClient;
  @Mock private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
  @Mock private RestClient.RequestHeadersSpec<?> requestHeadersSpec;
  @Mock private RestClient.ResponseSpec responseSpec;

  @InjectMocks private UserServiceClientImpl userServiceClient;

  @Test
  void getById_calls_user_service_and_returns_body() {
    var userId = UUID.randomUUID();
    var token = "token";
    var expected = new UserInfoDto(userId, "Maxim", "Maximov", "m@test.com");

    doReturn(requestHeadersUriSpec).when(restClient).get();
    doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri("/api/users/{id}", userId);
    doReturn(requestHeadersSpec)
        .when(requestHeadersSpec)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    doReturn(responseSpec).when(requestHeadersSpec).retrieve();
    doReturn(expected).when(responseSpec).body(UserInfoDto.class);

    var result = userServiceClient.getById(userId, token);

    assertThat(result).isEqualTo(expected);
  }
}
