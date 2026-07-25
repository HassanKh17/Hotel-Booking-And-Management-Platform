package com.hotel.management.system.controller;

import com.hotel.management.system.dto.ReviewDto;
import com.hotel.management.system.model.Review;
import com.hotel.management.system.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.hotel.management.system.repository.ReviewRepository;

import java.util.List;

@RestController
@RequestMapping("/hotels/{hotelId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Long hotelId) {
        List<ReviewDto> reviews = reviewService.getReviewsByHotel(hotelId)
                .stream()
                .map(r -> {
                    ReviewDto dto = new ReviewDto();
                    dto.setId(r.getId());
                    dto.setStarRating(r.getStarRating());
                    dto.setMessage(r.getMessage());
                    dto.setCustomerUsername(r.getCustomer().getUsername());
                    dto.setReply(r.getReply());
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getAverageRating(hotelId));
    }

    @PostMapping("/{customerId}")
    public ResponseEntity<ReviewDto> addReview(@PathVariable Long hotelId,
            @PathVariable Long customerId,
            @RequestBody Review review) {
        Review saved = reviewService.addReview(hotelId, customerId, review);
        ReviewDto dto = new ReviewDto();
        dto.setId(saved.getId());
        dto.setStarRating(saved.getStarRating());
        dto.setMessage(saved.getMessage());
        dto.setCustomerUsername(saved.getCustomer().getUsername());
        dto.setReply(saved.getReply());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{reviewId}/edit")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long reviewId,
            @RequestBody Review review) {
        Review updated = reviewService.updateReview(reviewId, review);
        ReviewDto dto = new ReviewDto();
        dto.setId(updated.getId());
        dto.setStarRating(updated.getStarRating());
        dto.setMessage(updated.getMessage());
        dto.setCustomerUsername(updated.getCustomer().getUsername());
        dto.setReply(updated.getReply());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{reviewId}/reply")
    public ResponseEntity<ReviewDto> replyToReview(@PathVariable Long hotelId,
            @PathVariable Long reviewId,
            Authentication authentication,
            @RequestBody java.util.Map<String, String> body) {
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }
        String username = authentication == null ? null : authentication.getName();
        Review updated = reviewService.replyToReview(hotelId, reviewId, body.get("reply"), username);
        ReviewDto dto = new ReviewDto();
        dto.setId(updated.getId());
        dto.setStarRating(updated.getStarRating());
        dto.setMessage(updated.getMessage());
        dto.setCustomerUsername(updated.getCustomer().getUsername());
        dto.setReply(updated.getReply());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/can-review/{customerId}")
    public ResponseEntity<Boolean> canReview(@PathVariable Long hotelId,
            @PathVariable Long customerId) {
        return ResponseEntity.ok(
                reviewRepository.hasCustomerBookedHotel(customerId, hotelId) &&
                !reviewRepository.existsByCustomerUserIdAndHotelHotelId(customerId, hotelId)
        );
    }

}
