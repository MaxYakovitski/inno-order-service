package com.innowise.orderservice.dto;

import java.util.UUID;

public record UserInfoDto(UUID id, String name, String surname, String email) {}
