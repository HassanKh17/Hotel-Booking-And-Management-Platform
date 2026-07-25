package com.hotel.management.system.mapper;

import com.hotel.management.system.dto.SavedCardResponse;
import com.hotel.management.system.model.SavedCard;

import org.springframework.stereotype.Component;
import java.util.function.Function;

@Component
public class SavedCardMapper implements Function<SavedCard, SavedCardResponse> {

    @Override
    public SavedCardResponse apply(SavedCard savedCard) {
        return new SavedCardResponse(
            savedCard.getCardId(),
            savedCard.getCardholderName(),
            savedCard.getLastFourDigits(),
            savedCard.getExpiryDate()
        );
    }
}
