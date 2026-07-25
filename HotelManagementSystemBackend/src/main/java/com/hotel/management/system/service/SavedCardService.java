package com.hotel.management.system.service;

import com.hotel.management.system.model.SavedCard;
import com.hotel.management.system.dto.AddressDto;
import com.hotel.management.system.dto.CardDto;
import com.hotel.management.system.dto.SavedCardResponse;
import com.hotel.management.system.mapper.AddressDtoMapper;
import com.hotel.management.system.mapper.SavedCardMapper;
import com.hotel.management.system.model.Address;
import com.hotel.management.system.model.Customer;
import com.hotel.management.system.repository.CustomerRepository;
import com.hotel.management.system.repository.SavedCardRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SavedCardService {

    private final CustomerRepository customerRepo;
    private final SavedCardRepository cardRepo;
    private final CardEncryptionService encryptionService;
    private final SavedCardMapper cardMapper;
    private final AddressDtoMapper addressMapper;

    public SavedCardService(
        CustomerRepository customerRepo,
        SavedCardRepository cardRepo,
        CardEncryptionService encryptionService,
        SavedCardMapper cardMapper,
        AddressDtoMapper addressMapper
    ) {
        this.customerRepo = customerRepo;
        this.cardRepo = cardRepo;
        this.encryptionService = encryptionService;
        this.cardMapper = cardMapper;
        this.addressMapper = addressMapper;
    }

    public List<SavedCardResponse> getCardsByUsername(String username) {
        return cardRepo.findByCustomerUsername(username)
                .stream()
                .map(cardMapper)
                .toList();
    }

    public SavedCardResponse saveCard(CardDto cardDto, String username) {
        // Fetch customer by username
        Customer customer = customerRepo.findByUsername(username)
            .orElseThrow(() ->
                new IllegalArgumentException(username + " not found"));
        
        String encryptedCardNumber = encryptionService.encrypt(cardDto.cardNumber());
        String lastFourDigits = cardDto.cardNumber().substring(
            cardDto.cardNumber().length() - 4);

        Address billingAddress = addressMapper.toEntity(cardDto.billingAddress());
        SavedCard card = new SavedCard(cardDto.cardholderName(), encryptedCardNumber,
            lastFourDigits, cardDto.expiryDate(), customer, billingAddress);
        card = cardRepo.save(card);
        return cardMapper.apply(card);
    }

    public void deleteCard(Long cardId, String username) {
        SavedCard card = cardRepo.findById(cardId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Card not found"));
        // Ensure the card belongs to the authenticated user
        if (!card.getCustomer().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "A card can only be deleted by the associated customer");
        }
        cardRepo.delete(card);
    }
}
