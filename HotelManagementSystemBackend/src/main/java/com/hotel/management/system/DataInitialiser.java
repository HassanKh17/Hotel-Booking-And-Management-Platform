package com.hotel.management.system;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hotel.management.system.dto.GlobalChargeResponse;
import com.hotel.management.system.exception.IllegalUsernameException;
import com.hotel.management.system.model.Address;
import com.hotel.management.system.model.Admin;
import com.hotel.management.system.model.Booking;
import com.hotel.management.system.model.BookingItem;
import com.hotel.management.system.model.Customer;
import com.hotel.management.system.model.GlobalCharge;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.HotelOwner;
import com.hotel.management.system.model.OccupancyStatus;
import com.hotel.management.system.model.OccupancyType;
import com.hotel.management.system.model.Room;
import com.hotel.management.system.repository.BookingRepository;
import com.hotel.management.system.repository.CustomerRepository;
import com.hotel.management.system.repository.GlobalChargeRepository;
import com.hotel.management.system.repository.HotelOwnerRepository;
import com.hotel.management.system.repository.HotelRepository;
import com.hotel.management.system.repository.RoomRepository;
import com.hotel.management.system.repository.UserRepository;
import com.hotel.management.system.service.GlobalChargeService;
import com.hotel.management.system.service.MonthlyChargeService;
import com.hotel.management.system.service.RoomService;
import com.hotel.management.system.service.TransactionService;

/**
 * Enter a default users and hotels into the database on application startup.
 */
@Component
public class DataInitialiser implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HotelOwnerRepository hotelOwnerRepository;
    private final HotelRepository hotelRepository;
    private final RoomService roomService;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final MonthlyChargeService monthlyChargeService;
    private final GlobalChargeRepository globalChargeRepository;
    private final GlobalChargeService globalChargeService;
    private final TransactionService transactionService;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@hms.com";
    private static final BigDecimal OPENING_BALANCE = new BigDecimal("5000.00");

    DataInitialiser(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            HotelOwnerRepository hotelOwnerRepository,
            HotelRepository hotelRepository,
            RoomService roomService,
            CustomerRepository customerRepository,
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            MonthlyChargeService monthlyChargeService,
            GlobalChargeRepository globalChargeRepository,
            GlobalChargeService globalChargeService,
            TransactionService transactionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.hotelOwnerRepository = hotelOwnerRepository;
        this.hotelRepository = hotelRepository;
        this.roomService = roomService;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.monthlyChargeService = monthlyChargeService;
        this.globalChargeRepository = globalChargeRepository;
        this.globalChargeService = globalChargeService;
        this.transactionService = transactionService;
    }

    @Override
    public void run(String... args) {
        seedGlobalCharges();

        if (userRepository.findByUsername(ADMIN_USERNAME).isPresent()) {
            throw new IllegalUsernameException(
                    IllegalUsernameException.USERNAME_EXISTS);
        }

        GlobalChargeResponse currentCharge = globalChargeService.getCurrent();

        Admin admin = new Admin(
                ADMIN_USERNAME,
                ADMIN_EMAIL,
                passwordEncoder.encode("connecting steep and flat,")
        );
        userRepository.save(admin);

        // Seed owner 1
        HotelOwner owner1 = new HotelOwner();
        owner1.setUsername("owner_alice");
        owner1.setEmail("alice@hms.com");
        owner1.setPassword(passwordEncoder.encode("password12312312"));
        owner1.setBalance(OPENING_BALANCE);
        owner1 = hotelOwnerRepository.save(owner1);

        Hotel hotel1 = new Hotel();
        hotel1.setName("The Grand");
        hotel1.setDescription("A luxury city-centre hotel.");
        hotel1.setStarRating(5);
        hotel1.setHotelOwner(owner1);
        hotel1.setAddress(new Address("1 Grand Avenue", "London", "EC1A 1BB"));
        hotel1.setPhotoUrl("https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800");
        hotelRepository.save(hotel1);

        roomService.addRoom(hotel1.getHotelId(),
                new Room(OccupancyType.SINGLE, OccupancyStatus.AVAILABLE, new BigDecimal("120.00"), 1));
        roomService.addRoom(hotel1.getHotelId(),
                new Room(OccupancyType.DOUBLE, OccupancyStatus.AVAILABLE, new BigDecimal("200.00"), 2));

        triggerInitialCharges(owner1, hotel1, currentCharge);

        Hotel hotel2 = new Hotel();
        hotel2.setName("The Cosy Inn");
        hotel2.setDescription("A charming boutique hotel.");
        hotel2.setStarRating(3);
        hotel2.setHotelOwner(owner1);
        hotel2.setAddress(new Address("14 Narrow Lane", "Bristol", "BS1 4DJ"));
        hotel2.setPhotoUrl("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800");
        hotelRepository.save(hotel2);

        roomService.addRoom(hotel2.getHotelId(),
                new Room(OccupancyType.SINGLE, OccupancyStatus.AVAILABLE, new BigDecimal("75.00"), 1));

        triggerInitialCharges(owner1, hotel2, currentCharge);

        Hotel hotel4 = new Hotel();
        hotel4.setName("The City Lodge");
        hotel4.setDescription("A modern hotel in the heart of Manchester.");
        hotel4.setStarRating(3);
        hotel4.setHotelOwner(owner1);
        hotel4.setAddress(new Address("5 Market Street", "Manchester", "M1 1PW"));
        hotel4.setPhotoUrl("https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800");
        hotelRepository.save(hotel4);

        roomService.addRoom(hotel4.getHotelId(),
                new Room(OccupancyType.SINGLE, OccupancyStatus.AVAILABLE, new BigDecimal("85.00"), 1));
        roomService.addRoom(hotel4.getHotelId(),
                new Room(OccupancyType.DOUBLE, OccupancyStatus.AVAILABLE, new BigDecimal("140.00"), 2));

        triggerInitialCharges(owner1, hotel4, currentCharge);

        // Extra hotel for recurring charge testing
        Hotel hotel6 = new Hotel();
        hotel6.setName("Old Billing Test Hotel");
        hotel6.setDescription("Seeded hotel for recurring monthly charge testing.");
        hotel6.setStarRating(4);
        hotel6.setHotelOwner(owner1);
        hotel6.setAddress(new Address("20 Billing Street", "Leeds", "LS1 2AB"));
        hotel6.setPhotoUrl("https://images.unsplash.com/photo-1445019980597-93fa8acb246c?w=800");
        hotelRepository.save(hotel6);

        roomService.addRoom(hotel6.getHotelId(),
                new Room(OccupancyType.SINGLE, OccupancyStatus.AVAILABLE, new BigDecimal("99.00"), 1));

        triggerInitialCharges(owner1, hotel6, currentCharge);

        // Backdate this one so recurring payment is due now/overdue
        hotel6.setNextBaseFeeDueDate(LocalDate.now().minusMonths(1));
        hotelRepository.save(hotel6);

        roomRepository.findByHotelHotelId(hotel6.getHotelId()).forEach(room -> {
            room.setNextRoomFeeDueDate(LocalDate.now().minusMonths(1));
            roomRepository.save(room);
        });

        // Seed owner 2
        HotelOwner owner2 = new HotelOwner();
        owner2.setUsername("owner_bob");
        owner2.setEmail("bob@hms.com");
        owner2.setPassword(passwordEncoder.encode("password12345678"));
        owner2.setBalance(OPENING_BALANCE);
        owner2 = hotelOwnerRepository.save(owner2);

        Hotel hotel3 = new Hotel();
        hotel3.setName("Seaside Retreat");
        hotel3.setDescription("A relaxing hotel by the coast.");
        hotel3.setStarRating(4);
        hotel3.setHotelOwner(owner2);
        hotel3.setAddress(new Address("7 Shoreline Road", "Brighton", "BN1 1AA"));
        hotel3.setPhotoUrl("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800");
        hotelRepository.save(hotel3);

        roomService.addRoom(hotel3.getHotelId(),
                new Room(OccupancyType.SINGLE, OccupancyStatus.AVAILABLE, new BigDecimal("95.00"), 1));
        roomService.addRoom(hotel3.getHotelId(),
                new Room(OccupancyType.DOUBLE, OccupancyStatus.AVAILABLE, new BigDecimal("160.00"), 2));

        triggerInitialCharges(owner2, hotel3, currentCharge);

        Hotel hotel5 = new Hotel();
        hotel5.setName("Harbour View");
        hotel5.setDescription("A stunning hotel overlooking the harbour.");
        hotel5.setStarRating(4);
        hotel5.setHotelOwner(owner2);
        hotel5.setAddress(new Address("12 Harbour Lane", "Brighton", "BN2 1TB"));
        hotel5.setPhotoUrl("https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?w=800");
        hotelRepository.save(hotel5);

        roomService.addRoom(hotel5.getHotelId(),
                new Room(OccupancyType.SINGLE, OccupancyStatus.AVAILABLE, new BigDecimal("110.00"), 1));
        roomService.addRoom(hotel5.getHotelId(),
                new Room(OccupancyType.DOUBLE, OccupancyStatus.AVAILABLE, new BigDecimal("180.00"), 2));

        triggerInitialCharges(owner2, hotel5, currentCharge);

        // Seed demo customers with past bookings
        Customer customer1 = new Customer(
                "demo_customer",
                "customer@hms.com",
                passwordEncoder.encode("password12345678")
        );
        customerRepository.save(customer1);

        Room grandSingleRoom = roomRepository.findByHotelHotelId(hotel1.getHotelId()).get(0);
        Booking booking1 = new Booking(
                customer1,
                grandSingleRoom.getPricePerNight(),
                LocalDate.of(2025, 1, 1)
        );
        BookingItem item1 = new BookingItem(
                grandSingleRoom,
                grandSingleRoom.getPricePerNight(),
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 5)
        );
        booking1.addBookingItem(item1);
        bookingRepository.save(booking1);

        Customer customer2 = new Customer(
                "demo_customer2",
                "customer2@hms.com",
                passwordEncoder.encode("password12345678")
        );
        customerRepository.save(customer2);

        Room seasideSingleRoom = roomRepository.findByHotelHotelId(hotel3.getHotelId()).get(0);
        Booking booking2 = new Booking(
                customer2,
                seasideSingleRoom.getPricePerNight(),
                LocalDate.of(2025, 1, 10)
        );
        BookingItem item2 = new BookingItem(
                seasideSingleRoom,
                seasideSingleRoom.getPricePerNight(),
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 14)
        );
        booking2.addBookingItem(item2);
        bookingRepository.save(booking2);

        // Optional: trigger recurring charges once on startup too
        monthlyChargeService.applyRecurringChargesToAllOwners();
    }

    private void triggerInitialCharges(HotelOwner owner, Hotel hotel, GlobalChargeResponse currentCharge) {
        LocalDate nextDueDate = LocalDate.now().plusDays(30);

        transactionService.recordMonthlyBaseFee(
                owner,
                currentCharge.getMonthlyBasePrice(),
                "Initial hotel fee charged on hotel creation",
                hotel
        );

        hotel.setNextBaseFeeDueDate(nextDueDate);

        roomRepository.findByHotelHotelId(hotel.getHotelId()).forEach(room -> {
            transactionService.recordMonthlyRoomFee(
                    owner,
                    currentCharge.getPerRoomPrice(),
                    "Initial room fee charged on room creation",
                    room
            );

            room.setNextRoomFeeDueDate(nextDueDate);
        });
    }

    private void seedGlobalCharges() {
        if (globalChargeRepository
                .findTopByEffectiveFromLessThanEqualOrderByEffectiveFromDesc(LocalDate.now())
                .isPresent()) {
            return;
        }

        GlobalCharge defaultCharge = new GlobalCharge();
        defaultCharge.setBaseMonthlyFee(new BigDecimal("100.00"));
        defaultCharge.setPerRoomFee(new BigDecimal("10.00"));
        defaultCharge.setTransactionFeePct(new BigDecimal("5.00"));
        defaultCharge.setEffectiveFrom(LocalDate.now());

        globalChargeRepository.save(defaultCharge);
    }
}