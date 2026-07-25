package com.hotel.management.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Enumerated(EnumType.STRING)
    private OccupancyType occupancyType; // Single, Double

    @Enumerated(EnumType.STRING)
    private OccupancyStatus occupancyStatus;
    @Transient
    private int numberOfRooms;

    private BigDecimal pricePerNight;

    private int capacity;

	@Column(name = "next_room_fee_due_date")
    private LocalDate nextRoomFeeDueDate;

    @ManyToOne
    @JoinColumn(name = "HOTEL_ID")
    @JsonIgnore
    private Hotel hotel;

    public Room() {}
    public Room(OccupancyType occupancyType, OccupancyStatus occupancyStatus, BigDecimal pricePerNight, int capacity) {
        this.occupancyType = occupancyType;
        this.occupancyStatus = occupancyStatus;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;

    }

    public Long getRoomId() {
        return roomId;
    }

    public OccupancyStatus getOccupancyStatus() {
        return occupancyStatus;
    }

    public void setOccupancyStatus(OccupancyStatus occupancyStatus) {
        this.occupancyStatus = occupancyStatus;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public OccupancyType getOccupancyType() {
        return occupancyType;
    }

    public void setOccupancyType(OccupancyType occupancyType) {
        this.occupancyType = occupancyType;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }
    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    
    public LocalDate getNextRoomFeeDueDate() {
		return nextRoomFeeDueDate;
	}

	public void setNextRoomFeeDueDate(LocalDate nextRoomFeeDueDate) {
		this.nextRoomFeeDueDate = nextRoomFeeDueDate;
	}
}
