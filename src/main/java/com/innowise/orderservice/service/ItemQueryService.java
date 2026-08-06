package com.innowise.orderservice.service;

import com.innowise.orderservice.dto.item.ItemResponseDto;
import com.innowise.orderservice.entity.Item;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemQueryService {

  Map<UUID, Item> findAllByIds(Collection<UUID> ids);

  Page<ItemResponseDto> getAll(Pageable pageable);
}
