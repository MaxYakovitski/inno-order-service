package com.innowise.orderservice.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowise.orderservice.dto.order.OrderCreateDto;
import com.innowise.orderservice.dto.order.OrderUpdateStatusDto;
import com.innowise.orderservice.dto.orderitem.OrderItemDto;
import com.innowise.orderservice.entity.Item;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.wiremock.spring.InjectWireMock;

class OrderCommandControllerIntegrationTest extends AbstractIntegrationTest {

  private static final String BASE_URL = "/api/orders";

  @Autowired private ItemRepository itemRepository;
  @Autowired private OrderRepository orderRepository;

  @InjectWireMock("user-service")
  private WireMockServer userServiceMock;

  private Item item;
  private UUID userId;

  @BeforeEach
  void setUp() {
    item = itemRepository.save(Item.builder().name("Item").price(BigDecimal.valueOf(1.0)).build());
    userId = UUID.randomUUID();

    userServiceMock.stubFor(
        WireMock.get(urlPathMatching("/api/users/" + userId))
            .willReturn(
                okJson(
                    """
                                        {"id":"%s","name":"Maxim","surname":"Maximov","email":"m@test.com"}
                                        """
                        .formatted(userId))));
  }

  @Test
  void create_should_persist_order_and_return_201() throws Exception {
    var dto = new OrderCreateDto(List.of(new OrderItemDto(item.getId(), 2)));

    mockMvc
        .perform(
            post(BASE_URL)
                .with(asUser(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.totalPrice").value(2.0))
        .andExpect(jsonPath("$.userInfo.email").value("m@test.com"));

    assertThat(orderRepository.findAll()).hasSize(1);
  }

  @Test
  void create_should_return_400_when_items_empty() throws Exception {
    var dto = new OrderCreateDto(List.of());

    mockMvc
        .perform(
            post(BASE_URL)
                .with(asUser(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("validation_failed"));
  }

  @Test
  void update_should_change_status_and_return_200() throws Exception {
    var order =
        orderRepository.save(
            Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO)
                .build());
    var dto = new OrderUpdateStatusDto(OrderStatus.PAID);

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", order.getId())
                .with(asUser(userId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PAID"));
  }

  @Test
  void update_should_return_403_when_not_user_and_not_admin() throws Exception {
    var order =
        orderRepository.save(
            Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO)
                .build());
    var dto = new OrderUpdateStatusDto(OrderStatus.PAID);

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", order.getId())
                .with(asUser(UUID.randomUUID()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  void update_should_return_404_when_not_exists() throws Exception {
    var dto = new OrderUpdateStatusDto(OrderStatus.PAID);

    mockMvc
        .perform(
            put(BASE_URL + "/{id}", UUID.randomUUID())
                .with(asAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("not_found"));
  }

  @Test
  void delete_should_soft_delete_when_user_and_return_204() throws Exception {
    var order =
        orderRepository.save(
            Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO)
                .build());

    mockMvc
        .perform(delete(BASE_URL + "/{id}", order.getId()).with(asUser(userId)))
        .andExpect(status().isNoContent());

    assertThat(orderRepository.findById(order.getId())).isEmpty();
  }

  @Test
  void delete_should_return_403_when_not_user_and_not_admin() throws Exception {
    var order =
        orderRepository.save(
            Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.ZERO)
                .build());

    mockMvc
        .perform(delete(BASE_URL + "/{id}", order.getId()).with(asUser(UUID.randomUUID())))
        .andExpect(status().isForbidden());

    assertThat(orderRepository.findById(order.getId())).isPresent();
  }

  @Test
  void delete_should_return_404_when_not_exists() throws Exception {
    mockMvc
        .perform(delete(BASE_URL + "/{id}", UUID.randomUUID()).with(asAdmin()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("not_found"));
  }
}
