package com.innowise.orderservice.config;

import com.innowise.orderservice.event.PaymentCompletedEvent;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.resilience.annotation.EnableResilientMethods;

@Configuration
@EnableKafka
@EnableResilientMethods
public class KafkaConsumerConfig {

  @Bean
  public ConsumerFactory<String, PaymentCompletedEvent> paymentEventConsumerFactory(
      @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
      @Value("${payment.kafka.consumer-group-id}") String groupId) {

    JacksonJsonDeserializer<PaymentCompletedEvent> deserializer =
        new JacksonJsonDeserializer<>(PaymentCompletedEvent.class);

    Map<String, Object> props =
        Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG, groupId,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent>
      paymentEventListenerContainerFactory(
          ConsumerFactory<String, PaymentCompletedEvent> paymentEventConsumerFactory) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent>();
    factory.setConsumerFactory(paymentEventConsumerFactory);
    return factory;
  }
}
