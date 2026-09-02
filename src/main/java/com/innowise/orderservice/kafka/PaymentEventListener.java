package com.innowise.orderservice.kafka;

import com.innowise.orderservice.entity.OrderStatus;
import com.innowise.orderservice.event.PaymentCompletedEvent;
import com.innowise.orderservice.service.OrderCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

  private final OrderCommandService orderCommandService;

  @KafkaListener(
      topics = "${payment.kafka.topic}",
      containerFactory = "paymentEventListenerContainerFactory")
  @Retryable(includes = OptimisticLockingFailureException.class, maxRetries = 3, delay = 200)
  public void onPaymentCompleted(PaymentCompletedEvent event) {
    var resolvedStatus = mapStatus(event.status());
    if (resolvedStatus == null) {
      log.error(
          "Unknown payment status '{}' for order {}, ignoring event",
          event.status(),
          event.orderId());
      return;
    }
    orderCommandService.applyPaymentResult(event.orderId(), resolvedStatus);
  }

  private OrderStatus mapStatus(String paymentStatus) {
    return switch (paymentStatus) {
      case "SUCCESS" -> OrderStatus.PAID;
      case "FAILED" -> OrderStatus.PAYMENT_FAILED;
      default -> null;
    };
  }
}
