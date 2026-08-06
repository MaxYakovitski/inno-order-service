package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.UserInfoDto;
import com.innowise.orderservice.dto.order.OrderResponseDto;
import com.innowise.orderservice.dto.order.OrderUpdateStatusDto;
import com.innowise.orderservice.dto.orderitem.OrderItemResponseDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderItem;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

  void updateEntity(OrderUpdateStatusDto dto, @MappingTarget Order entity);

  @Mapping(target = "id", source = "entity.id")
  OrderResponseDto toDto(Order entity, UserInfoDto userInfo);

  @Mapping(target = "itemId", source = "item.id")
  @Mapping(target = "itemName", source = "item.name")
  OrderItemResponseDto toDto(OrderItem entity);
}
