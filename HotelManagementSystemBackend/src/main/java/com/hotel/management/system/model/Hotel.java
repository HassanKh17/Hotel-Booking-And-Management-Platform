package com.hotel.management.system.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;

    private String name;
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer starRating;
    private String photoUrl;

    @DecimalMin(value = "0.0", message = "Special offer cannot be negative")
    @DecimalMax(value = "10.0", message = "Special offer cannot exceed 10%")
    private BigDecimal specialOfferPercentage;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private HotelOwner hotelOwner;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "hotel_facility",
    joinColumns = @JoinColumn(name = "hotel_id"),
    inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private List<Facility> facilities = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
                name = "hotel_amenity",
                joinColumns = @JoinColumn(name = "hotel_id"),
                inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private List<Amenity> amenities = new ArrayList<>();

    @Column(name = "next_base_fee_due_date")
    private LocalDate nextBaseFeeDueDate;


    public Hotel() {}

    public Hotel(String name, String description, BigDecimal specialOfferPercentage) {
        this.name = name;
        this.description = description;
        this.specialOfferPercentage = specialOfferPercentage;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStarRating() {
        return starRating;
    }

    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public BigDecimal getSpecialOfferPercentage() {
        return specialOfferPercentage;
    }

    public void setSpecialOfferPercentage(BigDecimal specialOfferPercentage) {
        this.specialOfferPercentage = specialOfferPercentage;
    }

    public HotelOwner getHotelOwner() {
        return hotelOwner;
    }

    public void setHotelOwner(HotelOwner hotelOwner) {
        this.hotelOwner = hotelOwner;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

	public LocalDate getNextBaseFeeDueDate() {
		return nextBaseFeeDueDate;
	}

	public void setNextBaseFeeDueDate(LocalDate nextBaseFeeDueDate) {
		this.nextBaseFeeDueDate = nextBaseFeeDueDate;
	}
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hotelId == null) ? 0 : hotelId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Hotel other = (Hotel) obj;
        if (hotelId == null)
            return false;
        return hotelId.equals(other.hotelId);
    }
}
