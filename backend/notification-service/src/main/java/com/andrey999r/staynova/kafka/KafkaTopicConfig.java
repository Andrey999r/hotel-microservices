package com.andrey999r.staynova.kafka;

import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  private static final String SEVEN_DAYS = "86400000";

  @Bean
  public NewTopic notificationTopic() {
    return TopicBuilder.name(KafkaTopics.NOTIFICATION_TOPIC)
        .config("retention.ms", SEVEN_DAYS)
        .build();
  }

  @Bean
  public NewTopic emailNotificationTopic() {
    return TopicBuilder.name(KafkaTopics.EMAIL_TOPIC)
        .config("retention.ms", SEVEN_DAYS)
        .build();
  }
}
