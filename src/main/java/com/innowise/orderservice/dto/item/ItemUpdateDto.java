package com.innowise.orderservice.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemUpdateDto(@NotBlank String name, @NotNull @Positive BigDecimal price) {}
