package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.filter.OrderFilter;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

public interface OrderQueryService {

  OrderResponseDto getById(UUID id);

  Page<OrderResponseDto> getAll(Jwt jwt, OrderFilter filter, Pageable pageable);

  Page<OrderResponseDto> getByUserId(UUID userId, Pageable pageable);
}
