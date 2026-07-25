package com.hotel.management.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.management.system.model.HotelOwner;

import java.util.Optional;

@Repository
public interface HotelOwnerRepository extends JpaRepository<HotelOwner, Long> {
	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

    Optional<HotelOwner> findByUsername(String username);
}
