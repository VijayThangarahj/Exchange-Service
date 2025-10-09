package com.careandshare.exchange.Service;

import com.careandshare.exchange.Dto.ItemDto;
import com.careandshare.exchange.Model.ExchangeRequest;
import com.careandshare.exchange.Model.Item;
import com.careandshare.exchange.Repository.ExchangeRequestRepository;
import com.careandshare.exchange.Repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    // Updated constructor to include ExchangeRequestRepository
    public ItemService(ItemRepository itemRepository, ExchangeRequestRepository exchangeRequestRepository) {
        this.itemRepository = itemRepository;
        this.exchangeRequestRepository = exchangeRequestRepository;
    }

    // 🔹 Create item (address + status supported)
    public Item createItem(ItemDto dto) {
        Item item = new Item(
                dto.getId(),
                dto.getTitle(),
                dto.getType(),
                dto.getCategory(),
                dto.getItemCondition(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getOwnerName(),
                dto.getOwnerEmail(),
                dto.getPhoneNumber(),
                dto.getAddress(),   // address
                null,
                null,
                dto.getStatus()     // status as plain String
        );
        return itemRepository.save(item);
    }

    // 🔹 Get all items
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // 🔹 Get item by ID
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    // 🔹 Partial update
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
//                case "price":
//                    if (value != null) item.setPrice(Double.valueOf(value.toString()));
//                    else item.setPrice(null);
//                    break;
                case "ownerName": item.setOwnerName((String) value); break;
                case "ownerEmail": item.setOwnerEmail((String) value); break;
                case "phoneNumber": item.setPhoneNumber((String) value); break;
                case "address": item.setAddress((String) value); break;
                case "status": item.setStatus((String) value); break;
            }
        });

        return itemRepository.save(item);
    }

    // 🔹 Delete item
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) throw new RuntimeException("Item not found");
        itemRepository.deleteById(id);
    }

    // 🔹 Upload image
    public void saveImage(Long id, MultipartFile file) throws IOException {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) throw new RuntimeException("Item not found");
        Item item = optionalItem.get();
        item.setImage(file.getBytes());
        item.setImageType(file.getContentType());
        itemRepository.save(item);
    }

    // ------------------ EXCHANGE FUNCTIONALITY ------------------

    // 🔹 Get all exchange items
    public List<Item> getExchangeItems() {
        return itemRepository.findByCategoryIgnoreCaseAndStatusIgnoreCase("exchange", "available");
    }

    // 🔹 Create exchange request
    public ExchangeRequest createExchangeRequest(Long requesterItemId, Long requestedItemId) {
        Item requesterItem = itemRepository.findById(requesterItemId)
                .orElseThrow(() -> new RuntimeException("Requester item not found"));
        Item requestedItem = itemRepository.findById(requestedItemId)
                .orElseThrow(() -> new RuntimeException("Requested item not found"));

        if (!"available".equalsIgnoreCase(requesterItem.getStatus()) ||
                !"available".equalsIgnoreCase(requestedItem.getStatus())) {
            throw new RuntimeException("One or both items are not available for exchange");
        }

        ExchangeRequest request = new ExchangeRequest();
        request.setRequesterItemId(requesterItemId);
        request.setRequestedItemId(requestedItemId);

        return exchangeRequestRepository.save(request);
    }

    // 🔹 Accept exchange request
    public ExchangeRequest acceptExchangeRequest(Long requestId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Exchange request not found"));

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("Exchange request is not pending");
        }

        Item requesterItem = itemRepository.findById(request.getRequesterItemId()).get();
        Item requestedItem = itemRepository.findById(request.getRequestedItemId()).get();

        requesterItem.setStatus("sold");
        requestedItem.setStatus("sold");

        itemRepository.save(requesterItem);
        itemRepository.save(requestedItem);

        request.setStatus("accepted");
        return exchangeRequestRepository.save(request);
    }

    // 🔹 Reject exchange request
    public ExchangeRequest rejectExchangeRequest(Long requestId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Exchange request not found"));

        if (!"pending".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("Exchange request is not pending");
        }

        request.setStatus("rejected");
        return exchangeRequestRepository.save(request);
    }

    // 🔹 List exchange requests (all or by status)
    public List<ExchangeRequest> listExchangeRequests(String status) {
        if (status == null) return exchangeRequestRepository.findAll();
        return exchangeRequestRepository.findByStatusIgnoreCase(status);
    }
}
