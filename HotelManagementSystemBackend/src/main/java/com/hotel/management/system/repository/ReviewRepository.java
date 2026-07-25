package com.hotel.management.system.repository;

import com.hotel.management.system.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByHotelHotelId(Long hotelId);

    @Query("SELECT AVG(r.starRating) FROM Review r WHERE r.hotel.hotelId = :hotelId")
    Double findAverageRatingByHotelId(Long hotelId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b JOIN b.items i WHERE b.customer.userId = :customerId AND i.room.hotel.hotelId = :hotelId AND i.checkOut < CURRENT_DATE")
    boolean hasCustomerBookedHotel(@Param("customerId") Long customerId, @Param("hotelId") Long hotelId);

    boolean existsByCustomerUserIdAndHotelHotelId(Long customerId, Long hotelId);

}
