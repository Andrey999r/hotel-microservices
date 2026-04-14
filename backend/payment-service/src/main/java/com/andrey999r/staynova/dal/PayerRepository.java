package com.andrey999r.staynova.dal;

import com.andrey999r.staynova.dal.entities.PayerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayerRepository extends JpaRepository<PayerEntity, Long> {
  Optional<PayerEntity> findByLogin(String login);
}
