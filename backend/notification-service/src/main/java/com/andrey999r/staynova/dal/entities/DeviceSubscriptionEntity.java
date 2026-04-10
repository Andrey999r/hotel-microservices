package com.andrey999r.staynova.dal.entities;

import com.andrey999r.staynova.common.kafka.enums.ChannelType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "device_subscription")
public class DeviceSubscriptionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "login")
  private String login;

  @Column(name = "channel_type")
  @Enumerated(EnumType.STRING)
  private ChannelType channelType;

  @Column(name = "endpoint")
  private String endpoint;

  @Column(name = "device_token")
  private String deviceToken;
}
