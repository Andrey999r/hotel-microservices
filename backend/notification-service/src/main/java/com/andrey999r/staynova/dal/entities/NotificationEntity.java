package com.andrey999r.staynova.dal.entities;

import com.andrey999r.staynova.common.kafka.enums.NotificationEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "notification")
public class NotificationEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "message")
  private String message;

  @Column(name = "created_at")
  private Instant createdAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type")
  private NotificationEventType eventType;

  @ManyToOne
  @JoinColumn(name = "recipient_id")
  private RecipientEntity recipient;
}
