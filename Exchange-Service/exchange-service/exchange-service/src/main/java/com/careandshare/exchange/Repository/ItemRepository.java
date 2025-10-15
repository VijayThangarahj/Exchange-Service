package com.careandshare.exchange.Repository;

import com.careandshare.exchange.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByCategoryIgnoreCaseAndStatusIgnoreCase(String category, String status);

    List<Item> findByOwnerNameIgnoreCase(String ownerName);

    List<Item> findByOwnerEmailIgnoreCase(String ownerEmail);

    List<Item> findByStatusIgnoreCase(String status);

    List<Item> findByCategoryIgnoreCase(String category);

    List<Item> findBySubmittedByIgnoreCase(String submittedBy);

    List<Item> findBySubmittedByIgnoreCaseAndStatusIgnoreCase(String submittedBy, String status);

    @Query("SELECT i FROM Item i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Item> searchItems(@Param("searchTerm") String searchTerm);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND LOWER(i.status) = LOWER(:status)")
    List<Item> searchItemsByStatus(@Param("searchTerm") String searchTerm, @Param("status") String status);

    Optional<Item> findById(Long id);

    long count();
}