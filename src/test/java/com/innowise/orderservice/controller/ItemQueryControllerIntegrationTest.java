package com.innowise.orderservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.repository.ItemRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ItemQueryControllerIntegrationTest extends AbstractIntegrationTest {

  private static final String BASE_URL = "/api/items";

  @Autowired private ItemRepository itemRepository;

  @Test
  void get_all_should_return_200_when_authenticated() throws Exception {
    itemRepository.save(Item.builder().name("Item").price(BigDecimal.valueOf(1.0)).build());

    mockMvc
        .perform(get(BASE_URL).with(asUser(UUID.randomUUID())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1));
  }
}
