package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.order.OrderFilterDto;
import com.innowise.orderservice.dto.order.OrderResponseDto;
import com.innowise.orderservice.security.CurrentUserId;
import com.innowise.orderservice.service.OrderQueryService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderQueryController {

  private final OrderQueryService orderQueryService;

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @orderGuard.isOwner(#id, authentication.name)")
  public ResponseEntity<OrderResponseDto> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(orderQueryService.getById(id));
  }

  @GetMapping("/my")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Page<OrderResponseDto>> getMy(
      @CurrentUserId String userId,
      @Valid OrderFilterDto filter,
      @PageableDefault(size = 50) Pageable pageable) {
    return ResponseEntity.ok(
        orderQueryService.getAll(filter.toFilter(UUID.fromString(userId)), pageable));
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<OrderResponseDto>> getAll(
      @RequestParam(required = false) UUID userId,
      @Valid OrderFilterDto filter,
      @PageableDefault(size = 50) Pageable pageable) {
    return ResponseEntity.ok(orderQueryService.getAll(filter.toFilter(userId), pageable));
  }
}
