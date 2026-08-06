package com.innowise.orderservice.controller;

import com.innowise.orderservice.dto.item.ItemResponseDto;
import com.innowise.orderservice.service.ItemQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/items")
@RequiredArgsConstructor
public class ItemQueryController {

  private final ItemQueryService itemQueryService;

  public ResponseEntity<Page<ItemResponseDto>> getAll(Pageable pageable) {
    return ResponseEntity.ok(itemQueryService.getAll(pageable));
  }
}
