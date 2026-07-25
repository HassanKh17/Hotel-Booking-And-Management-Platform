package com.hotel.management.system.service;

import com.hotel.management.system.dto.GlobalChargeResponse;
import com.hotel.management.system.exception.HotelNotFound;
import com.hotel.management.system.exception.HotelOwnerNotFound;
import com.hotel.management.system.repository.*;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.HotelOwner;
import com.hotel.management.system.model.Room;
import com.hotel.management.system.repository.HotelOwnerRepository;
import com.hotel.management.system.repository.HotelRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelOwnerRepository hotelOwnerRepository;
    private final AmenityRepository amenityRepository;
    private final FacilityRepository facilityRepository;
    private final GlobalChargeService globalChargeService;
    private final TransactionService transactionService;

    public HotelService(HotelRepository hotelRepository, HotelOwnerRepository hotelOwnerRepository, AmenityRepository amenityRepository, FacilityRepository facilityRepository, GlobalChargeService globalChargeService, TransactionService transactionService) {
        this.hotelRepository = hotelRepository;
        this.hotelOwnerRepository = hotelOwnerRepository;
        this.amenityRepository = amenityRepository;
        this.facilityRepository = facilityRepository;
        this.globalChargeService = globalChargeService;
        this.transactionService = transactionService;
    }

    // CREATE
    @Transactional
    public Hotel createHotel(String username, Hotel hotel, List<Long> amenityIds, List<Long> facilityIds, MultipartFile image) {
        validateSpecialOffer(hotel.getSpecialOfferPercentage());
        HotelOwner hotelOwner = hotelOwnerRepository.findByUsername(username).orElseThrow(() -> new HotelOwnerNotFound("Owner not found"));

        hotel.setHotelOwner(hotelOwner);

        setAmenitiesAndFacilities(hotel, amenityIds, facilityIds);
        GlobalChargeResponse currentCharge = globalChargeService.getCurrent();
        LocalDate nextDueDate = LocalDate.now().plusDays(30);

        hotel.setHotelOwner(hotelOwner);

        if (hotel.getRooms() != null) {

            List<Room> expandedRooms = new ArrayList<>();

            for (Room room : hotel.getRooms()) {

                int count = room.getNumberOfRooms(); // from frontend

                for (int i = 0; i < count; i++) {

                    Room newRoom = new Room(); // create separate room each time

                    newRoom.setOccupancyType(room.getOccupancyType());
                    newRoom.setOccupancyStatus(room.getOccupancyStatus());
                    newRoom.setCapacity(room.getCapacity());
                    newRoom.setPricePerNight(room.getPricePerNight());

                    newRoom.setHotel(hotel); // VERY IMPORTANT

                    expandedRooms.add(newRoom);
                }
            }
            hotel.setRooms(expandedRooms); // replace grouped with expanded list
        }

        handleImageUpload(hotel, image);

        Hotel savedHotel = hotelRepository.save(hotel);


        transactionService.recordMonthlyBaseFee(hotelOwner, currentCharge.getMonthlyBasePrice(), "Initial hotel fee charged on hotel creation", hotel);

        savedHotel.setNextBaseFeeDueDate(nextDueDate);

        if (savedHotel.getRooms() != null) {
            for (Room room : savedHotel.getRooms()) {
                transactionService.recordMonthlyRoomFee(hotelOwner, currentCharge.getPerRoomPrice(), "Initial room fee charged on room creation", room);

                room.setNextRoomFeeDueDate(nextDueDate);
            }
        }

        return hotelRepository.save(savedHotel);
    }

    // UPDATE
    public Hotel updateHotel(Long hotelId, String username, Hotel updatedHotel, List<Long> amenityIds, List<Long> facilityIds, MultipartFile image) {
        validateSpecialOffer(updatedHotel.getSpecialOfferPercentage());
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFound("Hotel not found"));

        if (!hotel.getHotelOwner().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorised to update this hotel");
        }

        hotel.setName(updatedHotel.getName());
        hotel.setDescription(updatedHotel.getDescription());
        hotel.setSpecialOfferPercentage(updatedHotel.getSpecialOfferPercentage());
        hotel.setAddress(updatedHotel.getAddress());

        if (updatedHotel.getRooms() != null) {
            hotel.getRooms().clear();

            for (Room room : updatedHotel.getRooms()) {
                room.setRoomId(null);
                room.setHotel(hotel); // VERY IMPORTANT
                hotel.getRooms().add(room);
            }
        }

        setAmenitiesAndFacilities(hotel, amenityIds, facilityIds);
        handleImageUpload(hotel, image);

        return hotelRepository.save(hotel);
    }

    // DELETE
    @Transactional
    public void deleteHotel(Long hotelId, String username) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFound("Hotel not found"));

        if (!hotel.getHotelOwner().getUsername().equals(username)) {
            throw new RuntimeException("You are not the owner of this hotel");
        }
        transactionService.deleteByHotel(hotelId);
        hotelRepository.deleteById(hotelId);
    }

    // GET MY HOTELS (by username)
    public List<Hotel> getHotelsByOwnerUsername(String username) {
        return hotelRepository.findByHotelOwnerUsername(username);
    }

    // GET BY ID
    public Hotel getHotelById(Long hotelId) {
        return hotelRepository.findById(hotelId).orElseThrow(() -> new HotelNotFound("Hotel not found"));
    }

    // GET ALL
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAllByOrderByNameAsc();
    }

    // method to set amenities and facilities
    private void setAmenitiesAndFacilities(Hotel hotel, List<Long> amenityIds, List<Long> facilityIds) {
        // Always resets to avoid stale data
        if (amenityIds != null) {
            hotel.setAmenities(amenityRepository.findAllById(amenityIds));
        } else {
            hotel.setAmenities(List.of());
        }

        if (facilityIds != null) {
            hotel.setFacilities(facilityRepository.findAllById(facilityIds));
        } else {
            hotel.setFacilities(List.of());
        }
    }

    public List<Hotel> getHotelsByCity(String city) {
        return hotelRepository.findByAddressCityIgnoreCaseOrderByNameAsc(city);
    }

    // Image upload handling
    private void handleImageUpload(Hotel hotel, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                String uploadDir = System.getProperty("user.dir") + "/uploads/";

                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filePath = uploadDir + fileName;
                image.transferTo(new File(filePath));

                hotel.setPhotoUrl("/uploads/" + fileName);

            } catch (Exception e) {
                throw new RuntimeException("Image upload failed", e);
            }
        }
    }

    private void validateSpecialOffer(BigDecimal specialOfferPercentage) {
        if (specialOfferPercentage != null) {
            if (specialOfferPercentage.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Special offer cannot be negative");
            }
            if (specialOfferPercentage.compareTo(BigDecimal.TEN) > 0) {
                throw new RuntimeException("Special offer cannot exceed 10%");
            }
        }
    }
}