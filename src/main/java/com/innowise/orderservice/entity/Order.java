package com.innowise.orderservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ? AND version = ?")
@SQLRestriction("deleted = false")
public class Order extends AbstractAuditingEntity {

  @Id @UuidGenerator private UUID id;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private OrderStatus status = OrderStatus.CREATED;

  @NotNull
  @Positive
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal totalPrice;

  @Builder.Default
  @Column(nullable = false)
  private boolean deleted = false;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();

  public void addItem(OrderItem item) {
    items.add(item);
    item.setOrder(this);
  }
}
