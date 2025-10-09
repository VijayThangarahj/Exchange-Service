package com.careandshare.exchange.Repository;


import com.careandshare.exchange.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // 🔹 Find items by category and status (case-insensitive)
    List<Item> findByCategoryIgnoreCaseAndStatusIgnoreCase(String category, String status);

    // 🔹 Optionally, find items by owner's name or email
    List<Item> findByOwnerNameIgnoreCase(String ownerName);
    List<Item> findByOwnerEmailIgnoreCase(String ownerEmail);

    // ❌ Removed: findByOwnerId(Long ownerId) — invalid, because no ownerId field exists
}
