package com.andrey999r.staynova.dal;

import com.andrey999r.staynova.dal.entities.RecipientEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<RecipientEntity, Long> {
  boolean existsByLogin(String login);

  Optional<RecipientEntity> findByLogin(String login);
}
