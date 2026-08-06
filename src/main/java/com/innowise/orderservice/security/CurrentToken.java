package com.innowise.orderservice.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CurrentToken {

  public static String get() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
      throw new IllegalStateException("No JWT authentication present in SecurityContext");
    }
    return jwt.getTokenValue();
  }
}
