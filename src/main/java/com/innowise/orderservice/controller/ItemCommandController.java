package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.item.ItemCreateDto;
import com.innowise.orderservice.dto.item.ItemResponseDto;
import com.innowise.orderservice.dto.item.ItemUpdateDto;
import com.innowise.orderservice.service.ItemCommandService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemCommandController {

  private final ItemCommandService itemCommandService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ItemResponseDto> create(@Valid @RequestBody ItemCreateDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(itemCommandService.create(dto));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ItemResponseDto> update(
      @PathVariable UUID id, @Valid @RequestBody ItemUpdateDto dto) {
    return ResponseEntity.status(HttpStatus.OK).body(itemCommandService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    itemCommandService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
