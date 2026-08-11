package com.innowise.orderservice.dto.order;

import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.filter.OrderFilter;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public record OrderFilterDto(LocalDate startDate, LocalDate endDate, List<OrderStatus> statuses) {

  @AssertTrue(message = "startDate must be before endDate")
  public boolean isRangeValid() {
    return startDate == null || endDate == null || !startDate.isAfter(endDate);
  }

  public OrderFilter toFilter(UUID userId) {
    return new OrderFilter(
        startDate == null ? null : startDate.atStartOfDay().toInstant(ZoneOffset.UTC),
        endDate == null ? null : endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC),
        statuses,
        userId);
  }
}
