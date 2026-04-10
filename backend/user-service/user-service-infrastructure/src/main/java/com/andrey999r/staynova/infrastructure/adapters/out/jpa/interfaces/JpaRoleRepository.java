package com.andrey999r.staynova.infrastructure.adapters.out.jpa.interfaces;

import com.andrey999r.staynova.infrastructure.adapters.out.jpa.entities.RoleEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, UUID> {
  Optional<RoleEntity> findByRoleName(String roleName);
}
