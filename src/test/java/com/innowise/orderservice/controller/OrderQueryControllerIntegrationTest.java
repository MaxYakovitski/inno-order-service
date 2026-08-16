package com.innowise.orderservice.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowise.orderservice.dto.order.OrderCreateDto;
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

class OrderQueryControllerIntegrationTest extends AbstractIntegrationTest {

  private static final String BASE_URL = "/api/v1/orders";

  @Autowired private ItemRepository itemRepository;
  @Autowired private OrderRepository orderRepository;

  @InjectWireMock("user-service")
  private WireMockServer userServiceMock;

  private Item item;
  private UUID userId;
  private UUID otherUserId;

  @BeforeEach
  void setUp() {
    item = itemRepository.save(Item.builder().name("Item").price(BigDecimal.valueOf(1.0)).build());
    userId = UUID.randomUUID();
    otherUserId = UUID.randomUUID();

    userServiceMock.stubFor(
        WireMock.get(urlPathMatching("/api/v1/users/.*"))
            .willReturn(
                okJson(
                    """
                                        {"id":"%s","name":"Maxim","surname":"Maximov","email":"m@test.com"}
                                        """
                        .formatted(userId))));
  }

  @Test
  void getById_second_call_hits_cache_not_UserService() throws Exception {
    var dto = new OrderCreateDto(List.of(new OrderItemDto(item.getId(), 1)));
    var response =
        mockMvc
            .perform(
                post(BASE_URL)
                    .with(asUser(userId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
            .andReturn()
            .getResponse()
            .getContentAsString();

    var orderId = objectMapper.readTree(response).get("id").asString();

    mockMvc
        .perform(get(BASE_URL + "/{id}", orderId).with(asUser(userId)))
        .andExpect(status().isOk());

    mockMvc
        .perform(get(BASE_URL + "/{id}", orderId).with(asUser(userId)))
        .andExpect(status().isOk());

    userServiceMock.verify(1, getRequestedFor(urlPathMatching("/api/v1/users/" + userId)));
  }

  @Test
  void getById_when_admin_returns_order_of_any_user() throws Exception {
    var order = orderRepository.save(order(userId));

    mockMvc
        .perform(get(BASE_URL + "/{id}", order.getId()).with(asAdmin()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(order.getId().toString()));
  }

  @Test
  void getById_when_not_user_and_not_admin_returns_forbidden() throws Exception {
    var order = orderRepository.save(order(userId));

    mockMvc
        .perform(get(BASE_URL + "/{id}", order.getId()).with(asUser(UUID.randomUUID())))
        .andExpect(status().isForbidden());
  }

  @Test
  void getById_when_not_exists_returns_not_found() throws Exception {
    mockMvc
        .perform(get(BASE_URL + "/{id}", UUID.randomUUID()).with(asAdmin()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("not_found"));
  }

  @Test
  void getAll_when_user_returns_only_own_orders() throws Exception {
    orderRepository.save(order(userId));
    orderRepository.save(order(otherUserId));

    mockMvc
        .perform(get(BASE_URL + "/my").with(asUser(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1));
  }

  @Test
  void getAll_when_admin_returns_all_orders() throws Exception {
    orderRepository.save(order(userId));
    orderRepository.save(order(otherUserId));

    mockMvc
        .perform(get(BASE_URL).with(asAdmin()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2));
  }

  @Test
  void getAll_when_admin_filters_by_user_returns_orders_of_given_user() throws Exception {
    orderRepository.save(order(userId));

    mockMvc
        .perform(get(BASE_URL).param("userId", userId.toString()).with(asAdmin()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1));
  }

  @Test
  void getAll_when_user_returns_forbidden() throws Exception {
    mockMvc.perform(get(BASE_URL).with(asUser(userId))).andExpect(status().isForbidden());
  }

  private Order order(UUID ownerId) {
    return Order.builder()
        .userId(ownerId)
        .status(OrderStatus.CREATED)
        .totalPrice(BigDecimal.ONE)
        .build();
  }
}
