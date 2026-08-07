package com.innowise.orderservice.specification;

import com.innowise.orderservice.entity.Order;
import com.innowise.orderservice.entity.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSpecification {

  private static final String CREATED_AT = "createdAt";

  public static Specification<Order> createdBetween(Instant from, Instant to) {
    if (from == null && to == null) {
      return Specification.unrestricted();
    }
    return (root, query, cb) -> {
      if (from != null && to != null) {
        cb.between(root.get(CREATED_AT), from, to);
      }
      if (from != null) {
        return cb.greaterThanOrEqualTo(root.get(CREATED_AT), from);
      }
      return cb.lessThan(root.get(CREATED_AT), to);
    };
  }

  public static Specification<Order> hasStatus(List<OrderStatus> statuses) {
    if (statuses == null || statuses.isEmpty()) {
      return Specification.unrestricted();
    }
    return (root, _, _) -> root.get("statuses").in(statuses);
  }

  public static Specification<Order> hasUserId(UUID userId) {
    if (userId == null) {
      return Specification.unrestricted();
    }
    return ((root, _, cb) -> cb.equal(root.get("userId"), userId));
  }
}
