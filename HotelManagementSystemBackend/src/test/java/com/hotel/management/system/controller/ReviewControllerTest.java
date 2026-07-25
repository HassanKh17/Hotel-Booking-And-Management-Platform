package com.hotel.management.system.controller;

import tools.jackson.databind.ObjectMapper;
import com.hotel.management.system.model.Customer;
import com.hotel.management.system.model.Hotel;
import com.hotel.management.system.model.HotelOwner;
import com.hotel.management.system.model.Review;
import com.hotel.management.system.repository.ReviewRepository;
import com.hotel.management.system.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private ReviewRepository reviewRepository;

    private Review buildReview(Long id, int stars, String message, String username) {
        Customer customer = new Customer(username, username + "@test.com", "password");
        HotelOwner owner = new HotelOwner("owner", "owner@test.com", "password", BigDecimal.ZERO);
        Hotel hotel = new Hotel();
        hotel.setHotelId(1L);
        hotel.setName("Test Hotel");
        hotel.setHotelOwner(owner);
        Review review = new Review();
        review.setId(id);
        review.setStarRating(stars);
        review.setMessage(message);
        review.setCustomer(customer);
        review.setHotel(hotel);
        return review;
    }

    @Test
    void getReviews_returnsOkWithList() throws Exception {
        Review review = buildReview(1L, 5, "Great hotel!", "john_doe");
        when(reviewService.getReviewsByHotel(1L)).thenReturn(List.of(review));

        mockMvc.perform(get("/hotels/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].starRating").value(5))
                .andExpect(jsonPath("$[0].message").value("Great hotel!"))
                .andExpect(jsonPath("$[0].customerUsername").value("john_doe"));
    }

    @Test
    void getReviews_returnsEmptyList_whenNoReviews() throws Exception {
        when(reviewService.getReviewsByHotel(1L)).thenReturn(List.of());

        mockMvc.perform(get("/hotels/1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAverageRating_returnsOk() throws Exception {
        when(reviewService.getAverageRating(1L)).thenReturn(4.5);

        mockMvc.perform(get("/hotels/1/reviews/average"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }

    @Test
    void getAverageRating_returnsNull_whenNoReviews() throws Exception {
        when(reviewService.getAverageRating(1L)).thenReturn(null);

        mockMvc.perform(get("/hotels/1/reviews/average"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void addReview_returnsOkWithSavedReview() throws Exception {
        Review review = buildReview(1L, 4, "Nice stay", "jane_doe");
        when(reviewService.addReview(eq(1L), eq(2L), any(Review.class))).thenReturn(review);

        mockMvc.perform(post("/hotels/1/reviews/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.starRating").value(4))
                .andExpect(jsonPath("$.message").value("Nice stay"))
                .andExpect(jsonPath("$.customerUsername").value("jane_doe"));
    }

    @Test
    void updateReview_returnsOkWithUpdatedReview() throws Exception {
        Review updated = buildReview(1L, 3, "Updated message", "john_doe");
        when(reviewService.updateReview(eq(1L), any(Review.class))).thenReturn(updated);

        mockMvc.perform(put("/hotels/1/reviews/1/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.starRating").value(3))
                .andExpect(jsonPath("$.message").value("Updated message"));
    }

    @Test
    void deleteReview_returnsNoContent() throws Exception {
        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(delete("/hotels/1/reviews/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "owner")
    void replyToReview_returnsOkWithReply() throws Exception {
        Review replied = buildReview(1L, 5, "Great hotel!", "john_doe");
        replied.setReply("Thank you for your kind review!");
        when(reviewService.replyToReview(anyLong(), anyLong(), eq("Thank you for your kind review!"), anyString()))
                .thenReturn(replied);

        mockMvc.perform(put("/hotels/1/reviews/1/reply")
                .with(user("owner"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("reply", "Thank you for your kind review!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Thank you for your kind review!"));
    }

    @Test
    @WithMockUser(username = "other_owner")
    void replyToReview_returnsForbidden_whenOwnerDoesNotOwnHotel() throws Exception {
        when(reviewService.replyToReview(anyLong(), anyLong(), eq("Nope"), anyString()))
                .thenThrow(new AccessDeniedException("Only the hotel owner can reply to this review"));

        mockMvc.perform(put("/hotels/1/reviews/1/reply")
                .with(user("other_owner"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("reply", "Nope"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void canReview_returnsTrue_whenCustomerHasBookingAndNoReview() throws Exception {
        when(reviewRepository.hasCustomerBookedHotel(2L, 1L)).thenReturn(true);
        when(reviewRepository.existsByCustomerUserIdAndHotelHotelId(2L, 1L)).thenReturn(false);

        mockMvc.perform(get("/hotels/1/reviews/can-review/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void canReview_returnsFalse_whenNoBooking() throws Exception {
        when(reviewRepository.hasCustomerBookedHotel(2L, 1L)).thenReturn(false);
        when(reviewRepository.existsByCustomerUserIdAndHotelHotelId(2L, 1L)).thenReturn(false);

        mockMvc.perform(get("/hotels/1/reviews/can-review/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void canReview_returnsFalse_whenAlreadyReviewed() throws Exception {
        when(reviewRepository.hasCustomerBookedHotel(2L, 1L)).thenReturn(true);
        when(reviewRepository.existsByCustomerUserIdAndHotelHotelId(2L, 1L)).thenReturn(true);

        mockMvc.perform(get("/hotels/1/reviews/can-review/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
