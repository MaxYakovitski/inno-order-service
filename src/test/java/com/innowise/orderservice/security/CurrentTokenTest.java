package com.innowise.orderservice.security;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class CurrentTokenTest {

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void get_when_jwt_present_returns_token() {
    var jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", UUID.randomUUID().toString())
            .build();
    SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(jwt, null));
    assertThat(CurrentToken.get()).isEqualTo("token");
  }

  @Test
  void get_when_no_authentication_throws_IllegalStateException() {
    SecurityContextHolder.clearContext();
    assertThatThrownBy(CurrentToken::get).isInstanceOf(IllegalStateException.class);
  }

  @Test
  void get_when_principal_is_not_jwt_throws_IllegalStateException() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("user", "password"));
    assertThatThrownBy(CurrentToken::get).isInstanceOf(IllegalStateException.class);
  }
}
