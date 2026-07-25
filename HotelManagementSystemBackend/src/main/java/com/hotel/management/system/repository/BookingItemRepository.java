package com.hotel.management.system.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.management.system.model.BookingItem;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {
	@Query("""
			    SELECT CASE WHEN COUNT(bi) > 0 THEN true ELSE false END
			    FROM BookingItem bi
			    WHERE bi.room.roomId = :roomId
			      AND bi.checkIn < :checkOut
			      AND bi.checkOut > :checkIn
			""")
	boolean existsOverlappingBooking(@Param("roomId") Long roomId, @Param("checkIn") LocalDate checkIn,
			@Param("checkOut") LocalDate checkOut);

	@Query("""
			    SELECT COUNT(DISTINCT bi.room.roomId)
			    FROM BookingItem bi
			    WHERE bi.room.hotel.hotelId = :hotelId
			      AND bi.checkIn <= :today
			      AND bi.checkOut > :today
			""")
	long countOccupiedRoomsToday(Long hotelId, LocalDate today);

	@Query("""
			    SELECT COUNT(bi)
			    FROM BookingItem bi
			    WHERE bi.room.hotel.hotelId = :hotelId
			      AND bi.checkIn > :today
			""")
	long countFutureBookings(Long hotelId, LocalDate today);

	@Query("""
			    SELECT bi
			    FROM BookingItem bi
			    WHERE bi.room.hotel.hotelId = :hotelId
			      AND bi.checkIn <= :today
			      AND bi.checkOut > :today
			    ORDER BY bi.checkIn ASC, bi.checkOut ASC
			""")
	List<BookingItem> findCurrentBookedRoomsByHotel(@Param("hotelId") Long hotelId, @Param("today") LocalDate today);

	@Query("""
			    SELECT bi
			    FROM BookingItem bi
			    WHERE bi.room.hotel.hotelId = :hotelId
			      AND bi.checkIn > :today
			    ORDER BY bi.checkIn ASC, bi.checkOut ASC
			""")
	List<BookingItem> findUpcomingBookedRoomsByHotel(@Param("hotelId") Long hotelId, @Param("today") LocalDate today);

	@Query("""
			    SELECT bi
			    FROM BookingItem bi
			    WHERE bi.room.roomId = :roomId
			      AND bi.checkOut > :today
			    ORDER BY bi.checkIn ASC
			""")
	List<BookingItem> findFutureOrActiveBookingsByRoomId(@Param("roomId") Long roomId, @Param("today") LocalDate today);

	@Modifying
	@Query("""
			    delete from BookingItem bi
			    where bi.room.hotel.hotelOwner.userId = :ownerId
			""")
	void deleteByOwnerId(@Param("ownerId") Long ownerId);
}
