package com.innowise.orderservice.filter;

import com.innowise.orderservice.entity.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderFilter(Instant from, Instant to, List<OrderStatus> statuses, UUID userId) {}
