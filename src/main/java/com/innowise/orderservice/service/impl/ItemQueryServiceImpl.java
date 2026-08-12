package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.dto.item.ItemResponseDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.service.ItemQueryService;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemQueryServiceImpl implements ItemQueryService {

  private final ItemRepository itemRepository;
  private final ItemMapper itemMapper;

  @Transactional(readOnly = true)
  @Override
  public Map<UUID, Item> findAllByIds(Collection<UUID> ids) {
    return itemRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(Item::getId, Function.identity()));
  }

  @Transactional(readOnly = true)
  @Override
  public Page<ItemResponseDto> getAll(Pageable pageable) {
    return itemRepository.findAll(pageable).map(itemMapper::toDto);
  }
}
