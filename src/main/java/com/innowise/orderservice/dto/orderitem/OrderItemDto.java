package com.innowise.orderservice.dto.orderitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record OrderItemDto(@NotNull UUID itemId, @NotNull @Positive Integer quantity) {}
