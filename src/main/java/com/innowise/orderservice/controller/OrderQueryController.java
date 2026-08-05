package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.filter.OrderFilter;
import com.innowise.orderservice.service.OrderQueryService;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderQueryController {

  private final OrderQueryService orderQueryService;

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or @orderGuard.isOwner(#id, authentication.name)")
  public ResponseEntity<OrderResponseDto> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(orderQueryService.getById(id));
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<Page<OrderResponseDto>> getAll(
      @AuthenticationPrincipal Jwt jwt,
      @RequestParam(required = false) LocalDate startDate,
      @RequestParam(required = false) LocalDate endDate,
      @RequestParam(required = false) List<OrderStatus> statuses,
      @PageableDefault(size = 50) Pageable pageable) {

    OrderFilter filter =
        new OrderFilter(
            startDate != null ? startDate.atStartOfDay().toInstant(ZoneOffset.UTC) : null,
            endDate != null ? endDate.atStartOfDay().toInstant(ZoneOffset.UTC) : null,
            statuses);
    return ResponseEntity.ok(orderQueryService.getAll(jwt, filter, pageable));
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<OrderResponseDto>> getByUserId(
      @PathVariable UUID userId, Pageable pageable) {
    return ResponseEntity.ok(orderQueryService.getByUserId(userId, pageable));
  }
}
