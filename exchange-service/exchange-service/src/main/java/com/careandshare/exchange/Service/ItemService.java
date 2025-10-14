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

    // FIXED METHOD - Use setters instead of constructor
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

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

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

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) throw new RuntimeException("Item not found");
        itemRepository.deleteById(id);
    }

    public void saveImage(Long id, MultipartFile file) throws IOException {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) throw new RuntimeException("Item not found");
        Item item = optionalItem.get();
        item.setImage(file.getBytes());
        item.setImageType(file.getContentType());
        itemRepository.save(item);
    }

    public List<Item> getExchangeItems() {
        return itemRepository.findByCategoryIgnoreCaseAndStatusIgnoreCase("exchange", "approved");
    }

    public List<Item> getItemsByStatus(String status) {
        return itemRepository.findByStatusIgnoreCase(status);
    }

    public List<Item> getItemsByCategory(String category) {
        return itemRepository.findByCategoryIgnoreCase(category);
    }

    public List<Item> getItemsByOwnerEmail(String email) {
        return itemRepository.findByOwnerEmailIgnoreCase(email);
    }

    public List<Item> getItemsBySubmittedBy(String email) {
        return itemRepository.findBySubmittedByIgnoreCase(email);
    }

    public List<Item> searchItems(String searchTerm) {
        return itemRepository.searchItems(searchTerm);
    }

    public ExchangeRequest createExchangeRequest(Long requesterItemId, Long requestedItemId) {
        Item requesterItem = itemRepository.findById(requesterItemId)
                .orElseThrow(() -> new RuntimeException("Requester item not found"));
        Item requestedItem = itemRepository.findById(requestedItemId)
                .orElseThrow(() -> new RuntimeException("Requested item not found"));

        if (!"approved".equalsIgnoreCase(requesterItem.getStatus()) &&
                !"available".equalsIgnoreCase(requesterItem.getStatus())) {
            throw new RuntimeException("Requester item is not available for exchange");
        }

        if (!"approved".equalsIgnoreCase(requestedItem.getStatus()) &&
                !"available".equalsIgnoreCase(requestedItem.getStatus())) {
            throw new RuntimeException("Requested item is not available for exchange");
        }

        ExchangeRequest request = new ExchangeRequest();
        request.setRequestedItemId(requesterItemId);
        request.setRequestedItemId(requestedItemId);
        request.setStatus("pending");

        return exchangeRequestRepository.save(request);
    }

    public ExchangeRequest acceptExchangeRequest(Long requestId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Exchange request not found"));

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("Exchange request is not pending");
        }

        Item requesterItem = itemRepository.findById(request.getRequestedItemId()).get();
        Item requestedItem = itemRepository.findById(request.getRequestedItemId()).get();

        requesterItem.setStatus("sold");
        requestedItem.setStatus("sold");

        itemRepository.save(requesterItem);
        itemRepository.save(requestedItem);

        request.setStatus("accepted");
        return exchangeRequestRepository.save(request);
    }

    public ExchangeRequest rejectExchangeRequest(Long requestId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Exchange request not found"));

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("Exchange request is not pending");
        }

        request.setStatus("rejected");
        return exchangeRequestRepository.save(request);
    }

    public List<ExchangeRequest> listExchangeRequests(String status) {
        if (status == null || status.isEmpty()) {
            return exchangeRequestRepository.findAll();
        }
        return exchangeRequestRepository.findByStatusIgnoreCase(status);
    }
}