package com.andrey999r.staynova.infrastructure.adapters.out.jpa.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Exclude
  @Column(name = "id", unique = true, nullable = false)
  private UUID id;

  @Column(name = "login", nullable = false, unique = true)
  private String login;

  @Column(name = "password", nullable = false)
  private String password;

  @ManyToMany(fetch = FetchType.EAGER)
  @ToString.Exclude
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles = new HashSet<>();

  @Column(name = "picture_path", nullable = false)
  private String picturePath;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "email_subscription_status", nullable = false)
  private boolean emailSubscriptionEnabled = false;

  @Column(name = "creation_date", nullable = false)
  private Instant creationDate;

  @PrePersist
  public void prePersist() {
    if (creationDate == null) {
      creationDate = Instant.now();
    }
  }
}
