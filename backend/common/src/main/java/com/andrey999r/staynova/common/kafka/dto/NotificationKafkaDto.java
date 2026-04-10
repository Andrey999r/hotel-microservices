package com.andrey999r.staynova.common.kafka.dto;


import com.andrey999r.staynova.common.kafka.enums.NotificationEventType;

public record NotificationKafkaDto(String login, String message, NotificationEventType eventType) {
}
