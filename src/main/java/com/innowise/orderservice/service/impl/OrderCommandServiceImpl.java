package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dto.OrderCreateDto;
import com.innowise.orderservice.dto.OrderItemDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.dto.OrderUpdateStatusDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderItem;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.ItemQueryService;
import com.innowise.orderservice.service.OrderCommandService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCommandServiceImpl implements OrderCommandService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final ItemQueryService itemQueryService;

  @Transactional
  @Override
  public OrderResponseDto create(UUID userId, OrderCreateDto dto) {
    var qtyByItemId =
        dto.items().stream()
            .collect(
                Collectors.groupingBy(
                    OrderItemDto::itemId, Collectors.summingInt(OrderItemDto::quantity)));
    var itemIds = qtyByItemId.keySet();
    var itemsById = itemQueryService.findAllByIds(itemIds);

    if (itemIds.size() != itemsById.size()) {
      throw new ResourceNotFoundException("Some items were not found");
    }

    var order = Order.builder().userId(userId).build();

    for (Map.Entry<UUID, Integer> entry : qtyByItemId.entrySet()) {
      var item = itemsById.get(entry.getKey());
      OrderItem orderItem = OrderItem.builder().item(item).quantity(entry.getValue()).build();
      order.addItem(orderItem);
    }
    order.setTotalPrice(calculate(order.getItems()));
    return orderMapper.toDto(orderRepository.save(order));
  }

  private @NotNull @Positive BigDecimal calculate(List<OrderItem> items) {
    return items.stream()
        .map(i -> i.getPriceAtOrder().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Transactional
  @Override
  public OrderResponseDto updateStatus(UUID id, OrderUpdateStatusDto dto) {
    var order = orderRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.order(id));
    orderMapper.updateEntity(dto, order);
    return orderMapper.toDto(order);
  }

  @Transactional
  @Override
  public void delete(UUID id) {
    var order = orderRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.order(id));
    orderRepository.delete(order);
  }
}
