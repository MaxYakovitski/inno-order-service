package com.innowise.orderservice.dto.order;

import com.innowise.orderservice.dto.orderitem.OrderItemDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderCreateDto(@NotEmpty @Valid List<OrderItemDto> items) {}
