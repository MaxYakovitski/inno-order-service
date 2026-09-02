package com.innowise.orderservice.event;

import java.util.UUID;

public record PaymentCompletedEvent(UUID orderId, String status) {}
