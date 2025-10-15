package com.careandshare.exchange.Controller;

import com.careandshare.exchange.Model.Item;
import com.careandshare.exchange.Repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        try {
            // Set default status if not provided
            if (item.getStatus() == null) {
                item.setStatus("pending");
            }

            // Set submitted date
            if (item.getSubmittedDate() == null) {
                item.setSubmittedDate(LocalDate.now().toString());
            }

            // Set submitted by if not provided
            if (item.getSubmittedBy() == null && item.getOwnerEmail() != null) {
                item.setSubmittedBy(item.getOwnerEmail());
            }

            Item savedItem = itemRepository.save(item);
            return ResponseEntity.ok(savedItem);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // New endpoint to create item with image
    @PostMapping("/with-image")
    public ResponseEntity<Item> createItemWithImage(
            @RequestParam("title") String title,
            @RequestParam("type") String type,
            @RequestParam("category") String category,
            @RequestParam("itemCondition") String itemCondition,
            @RequestParam("description") String description,
            @RequestParam("ownerName") String ownerName,
            @RequestParam("ownerEmail") String ownerEmail,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("address") String address,
            @RequestParam("location") String location,
            @RequestParam(value = "preferredCategory", required = false) String preferredCategory,
            @RequestParam(value = "shippingAvailable", defaultValue = "false") Boolean shippingAvailable,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            Item item = new Item();
            item.setTitle(title);
            item.setType(type);
            item.setCategory(category);
            item.setItemCondition(itemCondition);
            item.setDescription(description);
            item.setOwnerName(ownerName);
            item.setOwnerEmail(ownerEmail);
            item.setPhoneNumber(phoneNumber);
            item.setAddress(address);
            item.setLocation(location);
            item.setPreferredCategory(preferredCategory);
            item.setShippingAvailable(shippingAvailable);
            item.setStatus("pending");
            item.setSubmittedDate(LocalDate.now().toString());
            item.setSubmittedBy(ownerEmail);

            // Handle image upload
            if (image != null && !image.isEmpty()) {
                item.setImage(image.getBytes());
                item.setImageType(image.getContentType());
            }

            Item savedItem = itemRepository.save(item);
            return ResponseEntity.ok(savedItem);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Add these new endpoints for status filtering
    @GetMapping("/status/{status}")
    public List<Item> getItemsByStatus(@PathVariable String status) {
        return itemRepository.findByStatusIgnoreCase(status);
    }

    @GetMapping("/category/{category}/status/{status}")
    public List<Item> getItemsByCategoryAndStatus(@PathVariable String category, @PathVariable String status) {
        return itemRepository.findByCategoryIgnoreCaseAndStatusIgnoreCase(category, status);
    }

    // Add endpoints for approve/reject
    @PutMapping("/{id}/approve")
    public ResponseEntity<Item> approveItem(@PathVariable Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            item.setStatus("approved");
            Item updatedItem = itemRepository.save(item);
            return ResponseEntity.ok(updatedItem);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Item> rejectItem(@PathVariable Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            item.setStatus("rejected");
            Item updatedItem = itemRepository.save(item);
            return ResponseEntity.ok(updatedItem);
        }
        return ResponseEntity.notFound().build();
    }

    // Get items by owner email
    @GetMapping("/owner/{email}")
    public List<Item> getItemsByOwner(@PathVariable String email) {
        return itemRepository.findByOwnerEmailIgnoreCase(email);
    }

    // Update item image
    @PostMapping("/{id}/image")
    public ResponseEntity<Item> updateItemImage(@PathVariable Long id,
                                                @RequestParam("image") MultipartFile image) {
        try {
            Optional<Item> optionalItem = itemRepository.findById(id);
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();
                item.setImage(image.getBytes());
                item.setImageType(image.getContentType());
                Item updatedItem = itemRepository.save(item);
                return ResponseEntity.ok(updatedItem);
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();


        }
    }
}