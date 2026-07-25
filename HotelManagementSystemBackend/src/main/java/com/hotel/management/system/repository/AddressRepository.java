package com.hotel.management.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.management.system.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    
}
