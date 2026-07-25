package com.hotel.management.system.controller;

import com.hotel.management.system.dto.RoomDto;
import com.hotel.management.system.dto.UnavailableDateRangeDto;
import com.hotel.management.system.mapper.RoomDtoMapper;
import com.hotel.management.system.model.OccupancyStatus;
import com.hotel.management.system.model.OccupancyType;
import com.hotel.management.system.model.Room;
import com.hotel.management.system.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final RoomDtoMapper roomDtoMapper;

    public RoomController(RoomService roomService, RoomDtoMapper roomDtoMapper) {
        this.roomService = roomService;
        this.roomDtoMapper = roomDtoMapper;
    }

    @PostMapping("/{hotelId}")
    public ResponseEntity<RoomDto> addRoom(@PathVariable Long hotelId,
                                           @RequestBody RoomDto roomDto) {

        Room room = new Room(
                OccupancyType.valueOf(roomDto.occupancyType().toUpperCase()),
                OccupancyStatus.valueOf(roomDto.occupancyStatus().toUpperCase()),
                //roomDto.numberOfRooms(),
                roomDto.pricePerNight(),
                roomDto.capacity()
        );

        return ResponseEntity.ok(
                roomDtoMapper.apply(roomService.addRoom(hotelId, room))
        );
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomDto>> getRooms(@PathVariable Long hotelId) {

        List<Room> rooms = roomService.getRoomsByHotel(hotelId);

        if (rooms == null || rooms.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(
                rooms.stream().map(roomDtoMapper).toList()
        );
    }
    
    @GetMapping("/{roomId}/unavailable-dates")
    public ResponseEntity<List<UnavailableDateRangeDto>> getUnavailableDates(
            @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(roomService.getUnavailableDates(roomId));
    }
}