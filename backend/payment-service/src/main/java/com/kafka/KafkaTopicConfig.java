package com.kafka;

import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  private static final String SEVEN_DAYS = "86400000";

  @Bean
  public NewTopic paymentRequestTopic() {
    return TopicBuilder.name(KafkaTopics.PAYMENT_REQUEST_TOPIC)
        .config("retention.ms", SEVEN_DAYS)
        .build();
  }

  @Bean
  public NewTopic cancelRequestTopic() {
    return TopicBuilder.name(KafkaTopics.CANCEL_REQUEST_TOPIC)
        .config("retention.ms", SEVEN_DAYS)
        .build();
  }

  @Bean
  public NewTopic paymentResultTopic() {
    return TopicBuilder.name(KafkaTopics.PAYMENT_RESULT_TOPIC)
        .config("retention.ms", SEVEN_DAYS)
        .build();
  }
}
