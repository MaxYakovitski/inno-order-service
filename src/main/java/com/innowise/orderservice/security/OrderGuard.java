package com.innowise.orderservice.security;

import com.innowise.orderservice.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("orderGuard")
@RequiredArgsConstructor
public class OrderGuard {

  private final OrderRepository orderRepository;

  public boolean isOwner(UUID cardId, String userId) {
    return orderRepository
        .findById(cardId)
        .map(o -> o.getUserId().toString().equals(userId))
        .orElse(false);
  }
}
