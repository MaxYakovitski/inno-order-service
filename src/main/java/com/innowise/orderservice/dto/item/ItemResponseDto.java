package com.innowise.orderservice.dto.item;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public record ItemResponseDto(UUID id, String name, BigDecimal price) implements Serializable {}
