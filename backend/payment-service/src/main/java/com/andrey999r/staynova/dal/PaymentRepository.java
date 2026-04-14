package com.andrey999r.staynova.dal;

import com.andrey999r.staynova.dal.entities.PaymentEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

  Optional<PaymentEntity> findByProviderSessionId(String providerSessionId);
}
