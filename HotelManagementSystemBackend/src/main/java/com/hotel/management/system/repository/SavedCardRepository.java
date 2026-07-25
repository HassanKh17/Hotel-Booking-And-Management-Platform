package com.hotel.management.system.repository;

import com.hotel.management.system.model.SavedCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavedCardRepository extends JpaRepository<SavedCard, Long> {
    List<SavedCard> findByCustomerUsername(String username);
}
