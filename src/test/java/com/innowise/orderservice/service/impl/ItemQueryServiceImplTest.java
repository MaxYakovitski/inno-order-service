package com.innowise.orderservice.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.innowise.orderservice.dto.item.ItemResponseDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.repository.ItemRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ItemQueryServiceImplTest {

  @Mock private ItemRepository itemRepository;
  @Mock private ItemMapper itemMapper;

  @InjectMocks private ItemQueryServiceImpl itemQueryService;

  @Test
  void findAllById_returns_mapped_items() {
    var item1 =
        Item.builder().id(UUID.randomUUID()).name("Item1").price(BigDecimal.valueOf(1.0)).build();
    var item2 =
        Item.builder().id(UUID.randomUUID()).name("Item2").price(BigDecimal.valueOf(10.0)).build();
    var ids = List.of(item1.getId(), item2.getId());
    when(itemRepository.findAllById(ids)).thenReturn(List.of(item1, item2));
    var result = itemQueryService.findAllByIds(ids);
    assertThat(result)
        .containsExactlyInAnyOrderEntriesOf(Map.of(item1.getId(), item1, item2.getId(), item2));
  }

  @Test
  void findAllById_when_some_missed_returns_only_found_items() {
    var item1 =
        Item.builder().id(UUID.randomUUID()).name("Item").price(BigDecimal.valueOf(1.0)).build();
    var missingId = UUID.randomUUID();
    var ids = List.of(item1.getId(), missingId);
    when(itemRepository.findAllById(ids)).thenReturn(List.of(item1));
    var result = itemQueryService.findAllByIds(ids);
    assertThat(result).containsOnlyKeys(item1.getId());
  }

  @Test
  void getAll_returns_page() {
    var item =
        Item.builder().id(UUID.randomUUID()).name("Item").price(BigDecimal.valueOf(1.0)).build();
    var dto = new ItemResponseDto(item.getId(), "Item", BigDecimal.valueOf(1.0));
    var pageable = PageRequest.of(0, 50);
    var page = new PageImpl<>(List.of(item), pageable, 1);
    when(itemRepository.findAll(pageable)).thenReturn(page);
    when(itemMapper.toDto(item)).thenReturn(dto);
    var result = itemQueryService.getAll(pageable);
    assertThat(result.getContent()).containsExactly(dto);
  }
}
