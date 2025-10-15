package com.careandshare.exchange.Service;

import com.careandshare.exchange.Dto.ItemDto;
import com.careandshare.exchange.Model.ExchangeRequest;
import com.careandshare.exchange.Model.Item;
import com.careandshare.exchange.Repository.ExchangeRequestRepository;
import com.careandshare.exchange.Repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    public ItemService(ItemRepository itemRepository, ExchangeRequestRepository exchangeRequestRepository) {
        this.itemRepository = itemRepository;
        this.exchangeRequestRepository = exchangeRequestRepository;
    }

    // Create new item
    public Item createItem(ItemDto dto) {
        Item item = new Item();
        item.setTitle(dto.getTitle());
        item.setType(dto.getType());
        item.setCategory(dto.getCategory());
        item.setItemCondition(dto.getItemCondition());
        item.setDescription(dto.getDescription());
        item.setOwnerName(dto.getOwnerName());
        item.setOwnerEmail(dto.getOwnerEmail());
        item.setPhoneNumber(dto.getPhoneNumber());
        item.setAddress(dto.getAddress());
        item.setStatus(dto.getStatus() != null ? dto.getStatus() : "pending");
        item.setLocation(dto.getLocation());
        item.setPreferredCategory(dto.getPreferredCategory());
        item.setShippingAvailable(dto.getShippingAvailable());
        item.setSubmittedDate(LocalDate.now().toString());

        return itemRepository.save(item);
    }

    // Get all items
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Get item by ID
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    // Partial update item
    public Item updateItemPartial(Long id, Map<String, Object> updates) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "title": item.setTitle((String) value); break;
                case "type": item.setType((String) value); break;
                case "category": item.setCategory((String) value); break;
                case "itemCondition": item.setItemCondition((String) value); break;
                case "description": item.setDescription((String) value); break;
                case "ownerName": item.setOwnerName((String) value); break;
                case "ownerEmail": item.setOwnerEmail((String) value); break;
                case "phoneNumber": item.setPhoneNumber((String) value); break;
                case "address": item.setAddress((String) value); break;
                case "status": item.setStatus((String) value); break;
                case "location": item.setLocation((String) value); break;
                case "preferredCategory": item.setPreferredCategory((String) value); break;
                case "shippingAvailable": item.setShippingAvailable((Boolean) value); break;
            }
        });

        return itemRepository.save(item);
    }

    // Delete item
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) throw new RuntimeException("Item not found");
        itemRepository.deleteById(id);
    }

    // Save item image
    public void saveImage(Long id, MultipartFile file) throws IOException {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) throw new RuntimeException("Item not found");
        Item item = optionalItem.get();
        item.setImage(file.getBytes());
        item.setImageType(file.getContentType());
        itemRepository.save(item);
    }

    // Get exchange items
    public List<Item> getExchangeItems() {
        return itemRepository.findByCategoryIgnoreCaseAndStatusIgnoreCase("exchange", "approved");
    }

    // Get items by status
    public List<Item> getItemsByStatus(String status) {
        return itemRepository.findByStatusIgnoreCase(status);
    }

    // Get items by category
    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategoryIgnoreCase(category);
    }

    // Get items by owner email
    public List<Item> getItemsByOwnerEmail(String email) {
        return itemRepository.findByOwnerEmailIgnoreCase(email);
    }

    // Get items by submitted by
    public List<Item> getItemsBySubmittedBy(String email) {
        return itemRepository.findBySubmittedByIgnoreCase(email);
    }

    // Search items
    public List<Item> searchItems(String searchTerm) {
        return itemRepository.searchItems(searchTerm);
    }

    // List exchange requests
    public List<ExchangeRequest> listExchangeRequests(String status) {
        if (status != null && !status.isEmpty()) {
            return exchangeRequestRepository.findByStatusIgnoreCase(status);
        } else {
            return exchangeRequestRepository.findAll();
        }
    }

    // Create exchange request
    public ExchangeRequest createExchangeRequest(Long offeredItemId, Long requestedItemId) {
        Item offeredItem = itemRepository.findById(offeredItemId)
                .orElseThrow(() -> new RuntimeException("Offered item not found"));
        Item requestedItem = itemRepository.findById(requestedItemId)
                .orElseThrow(() -> new RuntimeException("Requested item not found"));

        if (!"approved".equalsIgnoreCase(offeredItem.getStatus()) &&
                !"available".equalsIgnoreCase(offeredItem.getStatus())) {
            throw new RuntimeException("Offered item is not available for exchange");
        }

        if (!"approved".equalsIgnoreCase(requestedItem.getStatus()) &&
                !"available".equalsIgnoreCase(requestedItem.getStatus())) {
            throw new RuntimeException("Requested item is not available for exchange");
        }

        ExchangeRequest request = new ExchangeRequest();

        // Set offered item information
        request.setOfferedItemTitle(offeredItem.getTitle());
        request.setOfferedItemDescription(offeredItem.getDescription());

        // Set requested item information
        request.setRequestedItemTitle(requestedItem.getTitle());
        request.setItemOwnerName(requestedItem.getOwnerName());
        request.setItemOwnerEmail(requestedItem.getOwnerEmail());

        // Set requester information from the offered item owner
        request.setExchangerName(offeredItem.getOwnerName());
        request.setExchangerEmail(offeredItem.getOwnerEmail());
        request.setExchangerPhone(offeredItem.getPhoneNumber());

        // Set exchange details
        request.setExchangeMethod("meet_in_person");
        request.setPreferredLocation(offeredItem.getLocation());
        request.setMessage("Exchange request for " + requestedItem.getTitle());

        // Set status
        request.setStatus("PENDING");

        return exchangeRequestRepository.save(request);
    }

    // Accept exchange request
    public ExchangeRequest acceptExchangeRequest(Long requestId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Exchange request not found"));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("Exchange request is not pending");
        }

        // Since we don't have the original item ID, find items by title and owner
        String offeredItemTitle = request.getOfferedItemTitle();
        String exchangerEmail = request.getExchangerEmail();

        if (offeredItemTitle != null && exchangerEmail != null) {
            List<Item> offeredItems = itemRepository.findByTitleAndOwnerEmail(offeredItemTitle, exchangerEmail);
            if (!offeredItems.isEmpty()) {
                Item offeredItem = offeredItems.get(0);
                offeredItem.setStatus("sold");
                itemRepository.save(offeredItem);
            }
        }

        // Update requested item status
        String requestedItemTitle = request.getRequestedItemTitle();
        String itemOwnerEmail = request.getItemOwnerEmail();

        if (requestedItemTitle != null && itemOwnerEmail != null) {
            List<Item> requestedItems = itemRepository.findByTitleAndOwnerEmail(requestedItemTitle, itemOwnerEmail);
            if (!requestedItems.isEmpty()) {
                Item requestedItem = requestedItems.get(0);
                requestedItem.setStatus("sold");
                itemRepository.save(requestedItem);
            }
        }

        request.setStatus("APPROVED");
        request.setReviewedDate(java.time.LocalDateTime.now());

        return exchangeRequestRepository.save(request);
    }

    // Reject exchange request
    public ExchangeRequest rejectExchangeRequest(Long requestId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Exchange request not found"));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("Exchange request is not pending");
        }

        request.setStatus("REJECTED");
        request.setReviewedDate(java.time.LocalDateTime.now());

        return exchangeRequestRepository.save(request);
    }

    // Additional method to get exchange requests by user email
    public List<ExchangeRequest> getExchangeRequestsByUserEmail(String email) {
        return exchangeRequestRepository.findByExchangerEmail(email);
    }

    // Additional method to update item status
    public Item updateItemStatus(Long id, String status) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setStatus(status);
        return itemRepository.save(item);
    }

    // Get available items for exchange
    public List<Item> getAvailableItems() {
        return itemRepository.findByStatusIgnoreCase("available");
    }

    // Get items by multiple criteria
    public List<Item> getItemsByCategoryAndStatus(String category, String status) {
        return itemRepository.findByCategoryIgnoreCaseAndStatusIgnoreCase(category, status);
    }

    // Get exchange request by ID
    public Optional<ExchangeRequest> getExchangeRequestById(Long id) {
        return exchangeRequestRepository.findById(id);
    }
}