package com.hotel.management.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

/**
 * Encrypt and decrypt card numbers using Spring Security's Encryptors.
 * In a real application, you would never store CVV or even the full card number
 */
@Service
public class CardEncryptionService {

    private final TextEncryptor encryptor;

    public CardEncryptionService(
        @Value("${api.card.encryption.password}") String password,
        @Value("${api.card.encryption.salt}") String salt
    ) {
        this.encryptor = Encryptors.delux(password, salt);
    }

    public String encrypt(String rawCardNumber) {
        return encryptor.encrypt(rawCardNumber);
    }

    public String decrypt(String encryptedCardNumber) {
        return encryptor.decrypt(encryptedCardNumber);
    }
}
