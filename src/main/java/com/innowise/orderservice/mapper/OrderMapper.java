package com.innowise.orderservice.mapper;

import com.innowise.orderservice.dto.OrderResponseDto;
import com.innowise.orderservice.dto.OrderUpdateStatusDto;
import com.innowise.orderservice.entity.Order;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

  @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
  void updateEntity(OrderUpdateStatusDto dto, @MappingTarget Order entity);

  OrderResponseDto toDto(Order entity);
}
