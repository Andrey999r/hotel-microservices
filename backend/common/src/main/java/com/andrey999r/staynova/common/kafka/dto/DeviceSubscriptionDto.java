package com.andrey999r.staynova.common.kafka.dto;

import com.andrey999r.staynova.common.kafka.enums.ChannelType;

public record DeviceSubscriptionDto(String login, String deviceToken, ChannelType channelType, String endpoint, boolean enabled) {
}
