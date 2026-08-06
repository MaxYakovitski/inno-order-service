package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.UserInfoDto;
import java.util.UUID;

public interface UserServiceClient {

  UserInfoDto getById(UUID userId, String token);
}
