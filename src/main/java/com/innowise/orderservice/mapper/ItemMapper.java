package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.item.ItemCreateDto;
import com.innowise.orderservice.dto.item.ItemResponseDto;
import com.innowise.orderservice.dto.item.ItemUpdateDto;
import com.innowise.orderservice.entity.Item;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

  Item toEntity(ItemCreateDto dto);

  void updateEntity(ItemUpdateDto dto, @MappingTarget Item entity);

  ItemResponseDto toDto(Item entity);
}
