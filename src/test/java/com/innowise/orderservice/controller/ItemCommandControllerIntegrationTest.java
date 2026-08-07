package com.innowise.orderservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.innowise.orderservice.dto.item.ItemCreateDto;
import com.innowise.orderservice.dto.item.ItemUpdateDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.repository.ItemRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class ItemCommandControllerIntegrationTest extends AbstractIntegrationTest {

  private static final String BASE_URL = "/api/items";

  @Autowired private ItemRepository itemRepository;

  @Test
  void create_should_return_201_when_admin() throws Exception {
    var dto = new ItemCreateDto("Item", BigDecimal.valueOf(1.0));

    mockMvc
        .perform(
            post(BASE_URL)
                .with(asAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Item"));
  }

  @Test
  void create_should_return_403_when_user() throws Exception {
    var dto = new ItemCreateDto("Item", BigDecimal.valueOf(1.0));

    mockMvc
        .perform(
            post(BASE_URL)
                .with(asUser(UUID.randomUUID()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_should_return_200_when_admin() throws Exception {
    var item =
        itemRepository.save(Item.builder().name("Old").price(BigDecimal.valueOf(1.0)).build());
    var dto = new ItemUpdateDto("New", BigDecimal.valueOf(1.0));

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", item.getId())
                .with(asAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New"));
  }

  @Test
  void delete_should_return_204_when_admin() throws Exception {
    var item =
        itemRepository.save(Item.builder().name("Item").price(BigDecimal.valueOf(1.0)).build());

    mockMvc
        .perform(delete(BASE_URL + "/{id}", item.getId()).with(asAdmin()))
        .andExpect(status().isNoContent());
  }
}
