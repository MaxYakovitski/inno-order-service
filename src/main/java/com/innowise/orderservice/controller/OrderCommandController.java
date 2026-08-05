package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.OrderCreateDto;
import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.dto.OrderUpdateStatusDto;
import com.innowise.orderservice.service.OrderCommandService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderCommandController {

  private final OrderCommandService orderCommandService;

  @PostMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<OrderResponseDto> create(
      @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody OrderCreateDto dto) {
    var userId = UUID.fromString(jwt.getSubject());
    var order = orderCommandService.create(userId, dto);
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
