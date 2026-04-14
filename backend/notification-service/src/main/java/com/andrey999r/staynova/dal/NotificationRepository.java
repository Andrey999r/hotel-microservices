package com.andrey999r.staynova.dal;

import com.andrey999r.staynova.dal.entities.NotificationEntity;
import com.andrey999r.staynova.dal.entities.RecipientEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
  List<NotificationEntity> findByRecipientOrderByCreatedAtDesc(RecipientEntity recipientEntity);
}
