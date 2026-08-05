package com.innowise.orderservice.dto;

import com.innowise.orderservice.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderUpdateStatusDto(@NotNull OrderStatus status) {}
