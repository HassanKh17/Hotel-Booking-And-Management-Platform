package com.hotel.management.system.repository;

import com.hotel.management.system.model.Booking;
import com.hotel.management.system.model.Customer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"items", "items.room", "items.room.hotel"})
    List<Booking> findByCustomerOrderByCreatedAtDesc(Customer customer);
}