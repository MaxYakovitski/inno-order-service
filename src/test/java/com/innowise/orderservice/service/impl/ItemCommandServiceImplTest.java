package com.innowise.orderservice.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.innowise.orderservice.dto.item.ItemCreateDto;
import com.innowise.orderservice.dto.item.ItemResponseDto;
import com.innowise.orderservice.dto.item.ItemUpdateDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.exception.ResourceNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.repository.ItemRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemCommandServiceImplTest {

  @Mock private ItemRepository itemRepository;
  @Mock private ItemMapper itemMapper;

  @InjectMocks private ItemCommandServiceImpl itemCommandService;

  @Test
  void create_should_return_ItemCreateDto() {
    var dto = new ItemCreateDto("Item", BigDecimal.valueOf(10.0));
    var entity = Item.builder().name("Item").price(BigDecimal.valueOf(10.0)).build();
    var saved =
        Item.builder().id(UUID.randomUUID()).name("Item").price(BigDecimal.valueOf(10.0)).build();
    var expected = new ItemResponseDto(saved.getId(), "Item", saved.getPrice());
    when(itemMapper.toEntity(dto)).thenReturn(entity);
    when(itemRepository.save(entity)).thenReturn(saved);
    when(itemMapper.toDto(saved)).thenReturn(expected);
    var result = itemCommandService.create(dto);
    Assertions.assertThat(result).isEqualTo(expected);
  }

  @Test
  void update_when_item_exists_updates_and_returns_ItemUpdateDto() {
    var id = UUID.randomUUID();
    var dto = new ItemUpdateDto("Item", BigDecimal.valueOf(10.0));
    var entity = Item.builder().id(id).name("New item").price(BigDecimal.valueOf(5.00)).build();
    var expected = new ItemResponseDto(id, "Item", BigDecimal.valueOf(10.00));

    when(itemRepository.findById(id)).thenReturn(Optional.of(entity));
    when(itemRepository.save(entity)).thenReturn(entity);
    when(itemMapper.toDto(entity)).thenReturn(expected);
    var result = itemCommandService.update(id, dto);
    Assertions.assertThat(result).isEqualTo(expected);
    verify(itemMapper).updateEntity(dto, entity);
  }

  @Test
  void update_when_item_not_found_throws_ResourceNotFoundException() {
    var id = UUID.randomUUID();
    var dto = new ItemUpdateDto("Item", BigDecimal.valueOf(10.0));
    when(itemRepository.findById(id)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> itemCommandService.update(id, dto))
        .isInstanceOf(ResourceNotFoundException.class);
    verify(itemRepository, never()).save(any(Item.class));
  }

  @Test
  void delete_when_item_exists_by_id() {
    var id = UUID.randomUUID();
    when(itemRepository.existsById(id)).thenReturn(true);
    itemCommandService.delete(id);
    verify(itemRepository).deleteById(id);
  }

  @Test
  void delete_when_item_not_found_throws_ResourceNotFoundException() {
    var id = UUID.randomUUID();
    when(itemRepository.existsById(id)).thenReturn(false);
    assertThatThrownBy(() -> itemCommandService.delete(id))
        .isInstanceOf(ResourceNotFoundException.class);
    verify(itemRepository, never()).deleteById(any(UUID.class));
  }
}
