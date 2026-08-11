package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.order.OrderResponseDto;
import com.innowise.orderservice.filter.OrderFilter;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderQueryService {

  OrderResponseDto getById(UUID id);

  Page<OrderResponseDto> getAll(OrderFilter filter, Pageable pageable);
}
