package com.hotel.management.system.service;

import com.hotel.management.system.dto.GlobalChargeResponse;
import com.hotel.management.system.exception.ResourceNotFoundException;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.HotelOwner;
import com.hotel.management.system.model.Room;
import com.hotel.management.system.repository.HotelOwnerRepository;
import com.hotel.management.system.repository.HotelRepository;
import com.hotel.management.system.repository.RoomRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MonthlyChargeService {

    private final HotelOwnerRepository hotelOwnerRepository;
    private final GlobalChargeService globalChargeService;
    private final TransactionService transactionService;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public MonthlyChargeService(HotelOwnerRepository hotelOwnerRepository,
                                GlobalChargeService globalChargeService,
                                TransactionService transactionService,
                                HotelRepository hotelRepository,
                                RoomRepository roomRepository) {
        this.hotelOwnerRepository = hotelOwnerRepository;
        this.globalChargeService = globalChargeService;
        this.transactionService = transactionService;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public void applyRecurringChargesToOwner(Long ownerId) {
        HotelOwner owner = hotelOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel owner not found with id: " + ownerId));

        GlobalChargeResponse currentCharge = globalChargeService.getCurrent();
        LocalDate today = LocalDate.now();

        if (owner.getHotels() == null || owner.getHotels().isEmpty()) {
            return;
        }

        List<Hotel> hotels = new ArrayList<>(owner.getHotels());

        for (Hotel hotel : hotels) {
            if (hotel.getNextBaseFeeDueDate() != null &&
                    !hotel.getNextBaseFeeDueDate().isAfter(today)) {

                transactionService.recordMonthlyBaseFee(
                        owner,
                        currentCharge.getMonthlyBasePrice(),
                        "Recurring hotel monthly fee charged",
                        hotel
                );

                hotel.setNextBaseFeeDueDate(hotel.getNextBaseFeeDueDate().plusDays(30));
                hotelRepository.save(hotel);
            }

            if (hotel.getRooms() == null || hotel.getRooms().isEmpty()) {
                continue;
            }
            
            List<Room> rooms = new ArrayList<>(hotel.getRooms());

            for (Room room : rooms) {
                if (room.getNextRoomFeeDueDate() != null &&
                        !room.getNextRoomFeeDueDate().isAfter(today)) {

                    transactionService.recordMonthlyRoomFee(
                            owner,
                            currentCharge.getPerRoomPrice(),
                            "Recurring room monthly fee charged",
                            room
                    );

                    room.setNextRoomFeeDueDate(room.getNextRoomFeeDueDate().plusDays(30));
                    roomRepository.save(room);
                }
            }
        }
    }

    @Transactional
    public void applyRecurringChargesToAllOwners() {
        List<HotelOwner> owners = hotelOwnerRepository.findAll();
        for (HotelOwner owner : owners) {
            applyRecurringChargesToOwner(owner.getUserId());
        }
    }
    
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void runScheduledRecurringCharges() {
        applyRecurringChargesToAllOwners();
    }
    
}