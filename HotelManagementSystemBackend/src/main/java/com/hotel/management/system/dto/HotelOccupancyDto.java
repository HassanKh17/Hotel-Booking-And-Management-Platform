package com.hotel.management.system.dto;

import java.util.List;

public record HotelOccupancyDto(
	    Long hotelId,
	    String hotelName,
	    long totalRooms,
	    long occupiedToday,
	    long availableToday,
	    double occupancyPercentage,
	    long futureBookings,
	    List<BookedRoomDto> currentBookedRooms,
	    List<BookedRoomDto> upcomingBookedRooms
	) {}
