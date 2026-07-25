package com.hotel.management.system.service;

import com.hotel.management.system.model.Customer;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.HotelOwner;
import com.hotel.management.system.model.Review;
import com.hotel.management.system.repository.CustomerRepository;
import com.hotel.management.system.repository.HotelRepository;
import com.hotel.management.system.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private CustomerRepository customerRepository;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(reviewRepository, hotelRepository, customerRepository);
    }

    @Test
    void replyToReview_savesReply_whenOwnerOwnsHotel() {
        Review review = buildReview("owner");
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        Review updated = reviewService.replyToReview(1L, 1L, "Thanks for staying with us.", "owner");

        assertThat(updated.getReply()).isEqualTo("Thanks for staying with us.");
        verify(reviewRepository).save(review);
    }

    @Test
    void replyToReview_throwsForbidden_whenOwnerDoesNotOwnHotel() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(buildReview("owner")));

        assertThatThrownBy(() -> reviewService.replyToReview(1L, 1L, "Nope", "other_owner"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Only the hotel owner");
    }

    private Review buildReview(String ownerUsername) {
        HotelOwner owner = new HotelOwner(ownerUsername, ownerUsername + "@test.com", "password", BigDecimal.ZERO);
        Hotel hotel = new Hotel();
        hotel.setHotelId(1L);
        hotel.setName("Test Hotel");
        hotel.setHotelOwner(owner);

        Review review = new Review();
        review.setId(1L);
        review.setHotel(hotel);
        review.setCustomer(new Customer("guest", "guest@test.com", "password"));
        review.setStarRating(5);
        review.setMessage("Great stay");

        return review;
    }
}
