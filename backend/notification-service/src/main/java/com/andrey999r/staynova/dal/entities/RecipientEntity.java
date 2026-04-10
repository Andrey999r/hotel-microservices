package com.andrey999r.staynova.dal.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "recipient")
public class RecipientEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "login")
  private String login;

  @OneToMany(mappedBy = "recipient")
  private List<com.andrey999r.staynova.dal.entities.NotificationEntity> notificationEntities = new ArrayList<>();

  @Column(name = "email")
  private String email;

  @Column(name = "email_notifications_enabled")
  @Builder.Default
  private boolean emailNotificationsEnabled = false;
}
