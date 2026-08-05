package com.innowise.orderservice.dto;

import com.innowise.orderservice.entity.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
    @NotNull UUID id,
    @NotEmpty OrderStatus status,
    @NotNull @Positive BigDecimal totalPrice,
    @NotEmpty @Valid List<OrderItemDto> items)
    implements Serializable {}
