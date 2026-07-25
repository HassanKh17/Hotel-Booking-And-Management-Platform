package com.hotel.management.system.repository;

import com.hotel.management.system.model.Room;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Custom query to get rooms by hotel
    List<Room> findByHotelHotelId(Long hotelId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.roomId = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);
}
