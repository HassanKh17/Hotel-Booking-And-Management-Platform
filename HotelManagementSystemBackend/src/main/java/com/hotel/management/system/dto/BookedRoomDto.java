package com.hotel.management.system.dto;

import java.time.LocalDate;

public record BookedRoomDto(
	    Long bookingItemId,
	    Long roomId,
	    String occupancyType,
	    LocalDate checkIn,
	    LocalDate checkOut
	) {}