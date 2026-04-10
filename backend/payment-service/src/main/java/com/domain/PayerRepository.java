package com.domain;

import com.domain.entities.PayerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayerRepository extends JpaRepository<PayerEntity, Long> {
  Optional<PayerEntity> findByLogin(String login);
}
