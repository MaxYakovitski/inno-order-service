package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.order.OrderCreateDto;
import com.innowise.orderservice.dto.order.OrderResponseDto;
import com.innowise.orderservice.dto.order.OrderUpdateStatusDto;
import com.innowise.orderservice.entity.OrderStatus;
import java.util.UUID;

public interface OrderCommandService {

  OrderResponseDto create(UUID userId, OrderCreateDto dto);

  OrderResponseDto updateStatus(UUID id, OrderUpdateStatusDto dto);

  void delete(UUID id);

  void applyPaymentResult(UUID orderId, OrderStatus resolvedStatus);
}
