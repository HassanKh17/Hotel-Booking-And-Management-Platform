package com.hotel.management.system.repository;

import com.hotel.management.system.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findByOwnerUserIdOrderByCreatedAtDesc(Long ownerId);

	@Modifying
	@Query("""
			    delete from Transaction t
			    where t.hotel.hotelId in (
			        select h.hotelId
			        from Hotel h
			        where h.hotelOwner.userId = :ownerId
			    )
			""")
	void deleteByOwnerHotelIds(@Param("ownerId") Long ownerId);

	@Modifying
	@Query("""
			    delete from Transaction t
			    where t.owner.userId = :ownerId
			""")
	void deleteByOwnerId(@Param("ownerId") Long ownerId);


	// Correct method — deletes all transactions referencing a specific hotel
	@Modifying
	@Query("DELETE FROM Transaction t WHERE t.hotel.hotelId = :hotelId")
	void deleteAllByHotelId(@Param("hotelId") Long hotelId);
}