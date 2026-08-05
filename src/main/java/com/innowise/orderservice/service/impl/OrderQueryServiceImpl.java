package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.filter.OrderFilter;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderQueryService;
import com.innowise.orderservice.specification.OrderSpecification;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;

  @Transactional(readOnly = true)
  @Override
  public OrderResponseDto getById(UUID id) {
    var order = orderRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.order(id));
    return orderMapper.toDto(order);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<OrderResponseDto> getAll(Jwt jwt, OrderFilter filter, Pageable pageable) {
    var isAdmin = "ADMIN".equals(jwt.getClaimAsString("role"));
    var userId = isAdmin ? null : UUID.fromString(jwt.getSubject());

    var specification =
        Specification.allOf(
            OrderSpecification.createdBetween(filter.from(), filter.to()),
            OrderSpecification.hasStatus(filter.statuses()),
            OrderSpecification.hasUserId(userId));
    var orders = orderRepository.findAll(specification, pageable);
    return orders.map(orderMapper::toDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<OrderResponseDto> getByUserId(UUID userId, Pageable pageable) {
    var specification = Specification.allOf(OrderSpecification.hasUserId(userId));
    var orders = orderRepository.findAll(specification, pageable);
    return orders.map(orderMapper::toDto);
  }
}
