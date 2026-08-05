package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.OrderCreateDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.dto.OrderUpdateStatusDto;
import java.util.UUID;

public interface OrderCommandService {

  OrderResponseDto create(UUID userId, OrderCreateDto dto);

  OrderResponseDto updateStatus(UUID id, OrderUpdateStatusDto dto);

  void delete(UUID id);
}
