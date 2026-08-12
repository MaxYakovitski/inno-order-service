package com.innowise.orderservice.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {}

  public static ResourceNotFoundException order(UUID orderId) {
    return new ResourceNotFoundException("Order with id " + orderId + " not found");
  }

  public static ResourceNotFoundException item(UUID itemId) {
    return new ResourceNotFoundException("Item with id " + itemId + " not found");
  }
}
