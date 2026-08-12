package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dto.item.ItemCreateDto;
import com.innowise.orderservice.dto.item.ItemResponseDto;
import com.innowise.orderservice.dto.item.ItemUpdateDto;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.service.ItemCommandService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemCommandServiceImpl implements ItemCommandService {

  private final ItemRepository itemRepository;
  private final ItemMapper itemMapper;

  @Transactional
  @Override
  public ItemResponseDto create(ItemCreateDto dto) {
    var item = itemMapper.toEntity(dto);
    return itemMapper.toDto(itemRepository.save(item));
  }

  @Transactional
  @Override
  public ItemResponseDto update(UUID id, ItemUpdateDto dto) {
    var item = itemRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.item(id));
    itemMapper.updateEntity(dto, item);
    return itemMapper.toDto(itemRepository.save(item));
  }

  @Transactional
  @Override
  public void delete(UUID id) {
    if (!itemRepository.existsById(id)) {
      throw ResourceNotFoundException.item(id);
    }
    itemRepository.deleteById(id);
  }
}
