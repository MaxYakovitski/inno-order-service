package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.order.OrderCreateDto;
import com.innowise.orderservice.dto.order.OrderResponseDto;
import com.innowise.orderservice.dto.order.OrderUpdateStatusDto;
import com.innowise.orderservice.security.CurrentUserId;
import com.innowise.orderservice.service.OrderCommandService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderCommandController {

  private final OrderCommandService orderCommandService;

  @PostMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<OrderResponseDto> create(
      @CurrentUserId String userId, @Valid @RequestBody OrderCreateDto dto) {
    var order = orderCommandService.create(UUID.fromString(userId), dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(order);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @orderGuard.isOwner(#id, authentication.name)")
  public ResponseEntity<OrderResponseDto> update(
      @PathVariable UUID id, @Valid @RequestBody OrderUpdateStatusDto dto) {
    return ResponseEntity.ok(orderCommandService.updateStatus(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @orderGuard.isOwner(#id, authentication.name)")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    orderCommandService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
