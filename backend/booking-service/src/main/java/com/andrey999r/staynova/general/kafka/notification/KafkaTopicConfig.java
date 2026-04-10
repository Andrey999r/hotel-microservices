package com.andrey999r.staynova.general.kafka.notification;

import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  @Bean
  public NewTopic notificationTopic() {
    return TopicBuilder.name(KafkaTopics.NOTIFICATION_TOPIC)
        .config("retention.ms", "86400000")
        .build();
  }

  @Bean
  public NewTopic paymentRequestTopic() {
    return TopicBuilder.name(KafkaTopics.PAYMENT_REQUEST_TOPIC).build();
  }

  @Bean
  public NewTopic cancelRequestTopic() {
    return TopicBuilder.name(KafkaTopics.CANCEL_REQUEST_TOPIC).build();
  }

  @Bean
  public NewTopic paymentResultTopic() {
    return TopicBuilder.name(KafkaTopics.PAYMENT_RESULT_TOPIC).build();
  }
}
