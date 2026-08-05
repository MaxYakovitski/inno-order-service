package com.innowise.orderservice.filter;

import com.innowise.orderservice.entity.OrderStatus;
import java.time.Instant;
import java.util.List;

public record OrderFilter(Instant from, Instant to, List<OrderStatus> statuses) {}
