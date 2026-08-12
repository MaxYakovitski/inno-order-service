package com.innowise.orderservice.dto.orderitem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponseDto(
    UUID itemId, String itemName, Integer quantity, BigDecimal priceAtOrder) {}
