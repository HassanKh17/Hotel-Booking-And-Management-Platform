package com.hotel.management.system.controller;

import com.hotel.management.system.dto.CardDto;
import com.hotel.management.system.dto.SavedCardResponse;
import com.hotel.management.system.service.SavedCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/cards")
public class SavedCardController {

    private final SavedCardService cardService;

    public SavedCardController(SavedCardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<List<SavedCardResponse>> getSavedCards(
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cardService.getCardsByUsername(username));
    }

    @PostMapping
    public ResponseEntity<SavedCardResponse> saveCard(@RequestBody CardDto card,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cardService.saveCard(card, username));
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id,
            Authentication authentication) {
        cardService.deleteCard(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
