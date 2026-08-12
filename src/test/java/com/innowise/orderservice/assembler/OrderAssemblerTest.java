package com.innowise.orderservice.assembler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import com.innowise.orderservice.dto.UserInfoDto;
import com.innowise.orderservice.dto.order.OrderResponseDto;
import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.service.UserServiceClient;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class OrderAssemblerTest {

  @Mock private UserServiceClient userServiceClient;

  @Mock private OrderMapper orderMapper;

  @InjectMocks private OrderAssembler orderAssembler;

  private static final String TOKEN = "token";

  @BeforeEach
  void setUp() {
    var jwt =
        Jwt.withTokenValue(TOKEN)
            .header("alg", "none")
            .claim("sub", UUID.randomUUID().toString())
            .build();
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(jwt, null));
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void assemble_fetches_user_info_and_convert_to_dto() {
    var userId = UUID.randomUUID();
    var order = Order.builder().userId(userId).build();
    var userInfo = new UserInfoDto(userId, "Maxim", "Maximov", "m@test.com");
    var expected = new OrderResponseDto(null, null, null, null, userInfo);

    when(userServiceClient.getById(userId, TOKEN)).thenReturn(userInfo);
    when(orderMapper.toDto(order, userInfo)).thenReturn(expected);

    var result = orderAssembler.assemble(order);

    assertThat(result).isEqualTo(expected);
    verify(orderMapper, times(1)).toDto(order, userInfo);
  }
}
