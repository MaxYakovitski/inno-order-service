package com.innowise.orderservice.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.innowise.orderservice.assembler.OrderAssembler;
import com.innowise.orderservice.dto.order.OrderResponseDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.filter.OrderFilter;
import com.innowise.orderservice.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceImplTest {

  @Mock private OrderRepository orderRepository;
  @Mock private OrderAssembler orderAssembler;

  @InjectMocks private OrderQueryServiceImpl orderQueryService;

  @Test
  void getById_should_return_assembled_response() {
    UUID orderId = UUID.randomUUID();
    Order order = Order.builder().id(orderId).build();
    OrderResponseDto expected = sampleResponse(orderId);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderAssembler.assemble(order)).thenReturn(expected);

    assertThat(orderQueryService.getById(orderId)).isEqualTo(expected);
  }

  @Test
  void getById_should_throw_when_not_found() {
    UUID orderId = UUID.randomUUID();
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> orderQueryService.getById(orderId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void getAll_should_return_page() {
    UUID userId = UUID.randomUUID();
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", userId.toString())
            .claim("role", "USER")
            .build();
    OrderFilter filter = new OrderFilter(null, null, null);
    Pageable pageable = PageRequest.of(0, 50);
    Order order = Order.builder().id(UUID.randomUUID()).userId(userId).build();
    Page<Order> page = new PageImpl<>(List.of(order));
    when(orderRepository.findAll(ArgumentMatchers.<Specification<Order>>any(), eq(pageable)))
        .thenReturn(page);
    when(orderAssembler.assemble(order)).thenReturn(sampleResponse(order.getId()));
    Page<OrderResponseDto> result = orderQueryService.getAll(jwt, filter, pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void getByUserId_should_return_page() {
    UUID userId = UUID.randomUUID();
    Pageable pageable = PageRequest.of(0, 50);
    Order order = Order.builder().id(UUID.randomUUID()).userId(userId).build();
    Page<Order> page = new PageImpl<>(List.of(order));
    when(orderRepository.findAll(ArgumentMatchers.<Specification<Order>>any(), eq(pageable)))
        .thenReturn(page);
    when(orderAssembler.assemble(order)).thenReturn(sampleResponse(order.getId()));
    Page<OrderResponseDto> result = orderQueryService.getByUserId(userId, pageable);
    assertThat(result.getContent()).hasSize(1);
  }

  private OrderResponseDto sampleResponse(UUID id) {
    return new OrderResponseDto(
        id, com.innowise.orderservice.entity.OrderStatus.CREATED, BigDecimal.TEN, List.of(), null);
  }
}
