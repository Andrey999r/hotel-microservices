package com.andrey999r.staynova.dal;

import com.andrey999r.staynova.common.kafka.enums.ChannelType;
import com.andrey999r.staynova.dal.entities.DeviceSubscriptionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceSubscriptionRepository
    extends JpaRepository<DeviceSubscriptionEntity, Long> {
  List<DeviceSubscriptionEntity> findAllByLogin(String login);

  Optional<DeviceSubscriptionEntity> findByLoginAndChannelType(
      String login, ChannelType channelType);
}
