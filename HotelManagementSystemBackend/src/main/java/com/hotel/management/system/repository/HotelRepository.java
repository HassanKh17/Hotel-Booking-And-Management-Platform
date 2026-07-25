package com.hotel.management.system.repository;

import com.hotel.management.system.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByHotelOwnerUserId(Long ownerId);

    List<Hotel> findByHotelOwnerUsername(String username);

    List<Hotel> findAllByOrderByNameAsc();

    List<Hotel> findByAddressCityIgnoreCaseOrderByNameAsc(String name);

}
