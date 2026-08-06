package com.innowise.orderservice.dto.order;

import com.innowise.orderservice.dto.UserInfoDto;
import com.innowise.orderservice.dto.orderitem.OrderItemResponseDto;
import com.innowise.orderservice.entity.OrderStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
    UUID id,
    OrderStatus status,
    BigDecimal totalPrice,
    List<OrderItemResponseDto> items,
    UserInfoDto userInfo)
    implements Serializable {}
