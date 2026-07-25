package com.hotel.management.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.management.system.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    
}
