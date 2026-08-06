package com.innowise.orderservice.assembler;

import com.innowise.orderservice.dto.order.OrderResponseDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.security.CurrentToken;
import com.innowise.orderservice.service.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderAssembler {

  private final UserServiceClient userServiceClient;
  private final OrderMapper orderMapper;

  public OrderResponseDto assemble(Order order) {
    var userInfo = userServiceClient.getById(order.getUserId(), CurrentToken.get());
    orderMapper.toDto(order, userInfo);
    return orderMapper.toDto(order, userInfo);
  }
}
