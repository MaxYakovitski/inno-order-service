package com.innowise.orderservice.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.innowise.orderservice.assembler.OrderAssembler;
import com.innowise.orderservice.dto.UserInfoDto;
import com.innowise.orderservice.dto.order.OrderCreateDto;
import com.innowise.orderservice.dto.order.OrderResponseDto;
import com.innowise.orderservice.dto.order.OrderUpdateStatusDto;
import com.innowise.orderservice.dto.orderitem.OrderItemDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.ItemQueryService;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceImplTest {

  @Mock private OrderRepository orderRepository;
  @Mock private ItemQueryService itemQueryService;
  @Mock private OrderMapper orderMapper;
  @Mock private OrderAssembler orderAssembler;

  @InjectMocks private OrderCommandServiceImpl orderCommandService;

  UUID userId = UUID.randomUUID();
  UUID itemId = UUID.randomUUID();
  UUID orderId = UUID.randomUUID();

  @Test
  void create_should_build_order_and_return_assembled_response() {
    OrderCreateDto dto = new OrderCreateDto(List.of(new OrderItemDto(itemId, 3)));

    Item item = Item.builder().id(itemId).name("Item").price(BigDecimal.valueOf(10.0)).build();
    when(itemQueryService.findAllByIds(Set.of(itemId))).thenReturn(Map.of(itemId, item));
    when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

    OrderResponseDto expected =
        new OrderResponseDto(
            UUID.randomUUID(),
            OrderStatus.CREATED,
            BigDecimal.valueOf(30.00),
            List.of(),
            new UserInfoDto(userId, "Maxim", "Maximov", "m@test.com"));
    when(orderAssembler.assemble(any(Order.class))).thenReturn(expected);

    OrderResponseDto result = orderCommandService.create(userId, dto);

    assertThat(result).isEqualTo(expected);

    ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository).save(captor.capture());
    Order saved = captor.getValue();
    assertThat(saved.getUserId()).isEqualTo(userId);
    assertThat(saved.getTotalPrice()).isEqualByComparingTo("30.00");
    assertThat(saved.getItems()).hasSize(1);
    assertThat(saved.getItems().getFirst().getQuantity()).isEqualTo(3);
    assertThat(saved.getItems().getFirst().getPriceAtOrder()).isEqualByComparingTo("10.00");
  }

  @Test
  void create_should_throw_when_item_not_found() {
    OrderCreateDto dto = new OrderCreateDto(List.of(new OrderItemDto(itemId, 1)));
    when(itemQueryService.findAllByIds(Set.of(itemId))).thenReturn(Map.of());
    assertThatThrownBy(() -> orderCommandService.create(userId, dto))
        .isInstanceOf(ResourceNotFoundException.class);
    verify(orderRepository, never()).save(any());
  }

  @Test
  void update_status_should_update_and_return_assembled_response() {
    Order order = Order.builder().id(orderId).userId(UUID.randomUUID()).build();
    OrderUpdateStatusDto dto = new OrderUpdateStatusDto(OrderStatus.PAID);
    OrderResponseDto expected =
        new OrderResponseDto(orderId, OrderStatus.PAID, BigDecimal.ZERO, List.of(), null);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderRepository.save(order)).thenReturn(order);
    when(orderAssembler.assemble(order)).thenReturn(expected);
    OrderResponseDto result = orderCommandService.updateStatus(orderId, dto);
    verify(orderMapper).updateEntity(dto, order);
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void update_status_should_throw_when_order_not_found() {
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
    OrderUpdateStatusDto dto = new OrderUpdateStatusDto(OrderStatus.PAID);
    assertThatThrownBy(() -> orderCommandService.updateStatus(orderId, dto))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void delete_should_delete_existing_order() {
    Order order = Order.builder().id(orderId).build();
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    orderCommandService.delete(orderId);
    verify(orderRepository).delete(order);
  }

  @Test
  void delete_should_throw_when_order_not_found() {
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> orderCommandService.delete(orderId))
        .isInstanceOf(ResourceNotFoundException.class);
    verify(orderRepository, never()).delete(any(Order.class));
  }
}
