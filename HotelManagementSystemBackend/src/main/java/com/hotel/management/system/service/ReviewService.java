package com.hotel.management.system.service;

import com.hotel.management.system.exception.HotelNotFound;
import com.hotel.management.system.model.Customer;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.Review;
import com.hotel.management.system.repository.CustomerRepository;
import com.hotel.management.system.repository.HotelRepository;
import com.hotel.management.system.repository.ReviewRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;

    public ReviewService(ReviewRepository reviewRepository,
            HotelRepository hotelRepository,
            CustomerRepository customerRepository) {
        this.reviewRepository = reviewRepository;
        this.hotelRepository = hotelRepository;
        this.customerRepository = customerRepository;
    }

    public Review addReview(Long hotelId, Long customerId, Review review) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFound("Hotel not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        boolean hasBooked = reviewRepository.hasCustomerBookedHotel(customerId, hotelId);
        if (!hasBooked) {
            throw new RuntimeException("You can only review hotels you have previously booked");
        }

        boolean alreadyReviewed = reviewRepository.existsByCustomerUserIdAndHotelHotelId(customerId, hotelId);
        if (alreadyReviewed) {
            throw new RuntimeException("You have already reviewed this hotel");
        }

        review.setHotel(hotel);
        review.setCustomer(customer);
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelHotelId(hotelId);
    }

    public Double getAverageRating(Long hotelId) {
        return reviewRepository.findAverageRatingByHotelId(hotelId);
    }

    public Review updateReview(Long reviewId, Review updatedReview) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setStarRating(updatedReview.getStarRating());
        review.setMessage(updatedReview.getMessage());
        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public Review replyToReview(Long hotelId, Long reviewId, String reply, String ownerUsername) {
        if (ownerUsername == null || ownerUsername.isBlank()) {
            throw new AccessDeniedException("Only the hotel owner can reply to this review");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Hotel hotel = review.getHotel();

        if (hotel == null || !hotel.getHotelId().equals(hotelId)) {
            throw new RuntimeException("Review not found for this hotel");
        }

        if (hotel.getHotelOwner() == null || !ownerUsername.equals(hotel.getHotelOwner().getUsername())) {
            throw new AccessDeniedException("Only the hotel owner can reply to this review");
        }

        review.setReply(reply);
        return reviewRepository.save(review);
    }

}
