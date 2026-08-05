package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.service.ItemQueryService;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemQueryServiceImpl implements ItemQueryService {

  private final ItemRepository itemRepository;

  @Override
  public Map<UUID, Item> findAllByIds(Collection<UUID> ids) {
    return itemRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(Item::getId, Function.identity()));
  }
}
