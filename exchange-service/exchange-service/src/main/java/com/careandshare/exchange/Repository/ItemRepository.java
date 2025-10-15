package com.careandshare.exchange.Repository;

import com.careandshare.exchange.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategoryIgnoreCaseAndStatusIgnoreCase(String category, String status);
    List<Item> findByStatusIgnoreCase(String status);
    List<Item> findByCategoryIgnoreCase(String category);
    List<Item> findByOwnerEmailIgnoreCase(String email);
    List<Item> findBySubmittedByIgnoreCase(String email);

    // Add this method for finding requested item by title and owner
    @Query("SELECT i FROM Item i WHERE LOWER(i.title) = LOWER(:title) AND LOWER(i.ownerEmail) = LOWER(:ownerEmail)")
    List<Item> findByTitleAndOwnerEmail(@Param("title") String title, @Param("ownerEmail") String ownerEmail);

    @Query("SELECT i FROM Item i WHERE " +
            "LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(i.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Item> searchItems(@Param("searchTerm") String searchTerm);
}