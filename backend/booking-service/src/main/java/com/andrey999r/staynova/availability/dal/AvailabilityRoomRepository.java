package com.andrey999r.staynova.availability.dal;

import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRoomRepository extends JpaRepository<RoomEntity, Long> {}
