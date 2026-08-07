package com.innowise.orderservice.security;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.repository.OrderRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderGuardTest {

  @Mock private OrderRepository orderRepository;
  @InjectMocks private OrderGuard orderGuard;

  UUID orderId = UUID.randomUUID();

  @Test
  void isOwner_when_user_is_owner_returns_true() {
    var userId = UUID.randomUUID();
    var order = Order.builder().userId(userId).build();
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    assertThat(orderGuard.isOwner(orderId, userId.toString())).isTrue();
  }

  @Test
  void isOwner_when_user_is_not_owner_returns_false() {
    var order = Order.builder().userId(UUID.randomUUID()).build();
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    assertThat(orderGuard.isOwner(orderId, UUID.randomUUID().toString())).isFalse();
  }

  @Test
  void isOwner_when_order_not_found_returns_false() {
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
    assertThat(orderGuard.isOwner(orderId, UUID.randomUUID().toString())).isFalse();
  }
}
