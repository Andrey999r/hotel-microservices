package com.andrey999r.staynova.infrastructure.adapters.out.jpa.interfaces;

import com.andrey999r.staynova.infrastructure.adapters.out.jpa.entities.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByLogin(String login);

  @Query(
      """
    select u from UserEntity u
    join u.roles r
    where r.roleName = :roleName
    """)
  List<UserEntity> findAllByRoleName(@Param("roleName") String roleName, Pageable pageable);
}
