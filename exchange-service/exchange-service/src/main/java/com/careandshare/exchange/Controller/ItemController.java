package com.careandshare.exchange.Controller;

import com.careandshare.exchange.Dto.ItemDto;
import com.careandshare.exchange.Model.Item;
import com.careandshare.exchange.Repository.ItemRepository;

import com.careandshare.exchange.Service.ItemService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*") // allow requests from any origin for testing; lock down in production
public class ItemController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;
//    private final ModelMapper modelMapper;

    public ItemController(ItemService itemService, ItemRepository itemRepository, ModelMapper modelMapper) {
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.modelMapper = modelMapper;
    }

    // 🔹 Get all items
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    // 🔹 Get item by id
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> optionalItem = itemService.getItemById(id);
        return optionalItem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Create multiple items (your provided method)
    @PostMapping
    public ResponseEntity<List<ItemDto>> createItems(@RequestBody List<ItemDto> items) {
        List<ItemDto> savedItems = new ArrayList<>();
        for (ItemDto item : items) {
            Item saved = itemRepository.save(modelMapper.map(item, Item.class));
            savedItems.add(modelMapper.map(saved, ItemDto.class));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItems);
    }

    // 🔹 Partial update
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Item updatedItem = itemService.updateItemPartial(id, updates);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 🔹 Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable Long id) {
        try {
            itemService.deleteItem(id);
            return ResponseEntity.ok("Item deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 🔹 Upload image
    @PostMapping("/{id}/image")
    public ResponseEntity<String> uploadImage(@PathVariable Long id,
                                              @RequestParam("image") MultipartFile file) {
        try {
            itemService.saveImage(id, file);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 🔹 Get item with image as Base64
    @GetMapping("/{id}/image")
    public ResponseEntity<Map<String, Object>> getItemWithImage(@PathVariable Long id) {
        Optional<Item> optionalItem = itemService.getItemById(id);

        if (!optionalItem.isPresent()) return ResponseEntity.notFound().build();

        Item item = optionalItem.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", item.getId());
        response.put("title", item.getTitle());
        response.put("type", item.getType());
        response.put("category", item.getCategory());
        response.put("status", item.getStatus());
        response.put("ownerName", item.getOwnerName());
        response.put("ownerEmail", item.getOwnerEmail());
        response.put("phoneNumber", item.getPhoneNumber());
        response.put("address", item.getAddress());
//        response.put("price", item.getPrice());
        response.put("description", item.getDescription());

        if (item.getImage() != null) {
            response.put("imageBase64", Base64.getEncoder().encodeToString(item.getImage()));
            response.put("imageType", item.getImageType());
        } else {
            response.put("imageBase64", null);
            response.put("imageType", null);
        }

        return ResponseEntity.ok(response);
    }
}
