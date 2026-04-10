package com.andrey999r.staynova.infrastructure.adapters.out.jpa.entities;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class RoleEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Exclude
  @Column(name = "id", nullable = false, unique = true)
  private UUID id;

  @Column(name = "role_name", nullable = false, unique = true)
  private String roleName;
}
