package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.item.ItemCreateDto;
import com.innowise.orderservice.dto.item.ItemResponseDto;
import com.innowise.orderservice.dto.item.ItemUpdateDto;
import java.util.UUID;

public interface ItemCommandService {

  ItemResponseDto create(ItemCreateDto dto);

  ItemResponseDto update(UUID id, ItemUpdateDto dto);

  void delete(UUID id);
}
