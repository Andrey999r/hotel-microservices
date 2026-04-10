package com.andrey999r.staynova.infrastructure.adapters.out.kafka.mappers;

import com.andrey999r.staynova.common.kafka.enums.NotificationEventType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationEventTypeMapper {

  NotificationEventType toKafkaType(
      com.andrey999r.staynova.domain.models.NotificationEventType domainEventType);
}
