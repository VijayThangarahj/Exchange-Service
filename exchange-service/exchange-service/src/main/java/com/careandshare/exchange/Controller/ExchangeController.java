package com.careandshare.exchange.Controller;


import com.careandshare.exchange.Model.ExchangeRequest;
import com.careandshare.exchange.Model.Item;

import com.careandshare.exchange.Service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@CrossOrigin(origins = "*")
public class ExchangeController {

    private final ItemService itemService;

    public ExchangeController(ItemService itemService) {
        this.itemService = itemService;
    }

    // 🔹 Get all exchange items
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getExchangeItems() {
        return ResponseEntity.ok(itemService.getExchangeItems());
    }

    // 🔹 Create exchange request
    @PostMapping("/request")
    public ResponseEntity<ExchangeRequest> createExchangeRequest(
            @RequestParam Long requesterItemId,
            @RequestParam Long requestedItemId) {
        ExchangeRequest request = itemService.createExchangeRequest(requesterItemId, requestedItemId);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    // 🔹 Accept exchange request
    @PostMapping("/request/{id}/accept")
    public ResponseEntity<ExchangeRequest> acceptExchangeRequest(@PathVariable Long id) {
        ExchangeRequest request = itemService.acceptExchangeRequest(id);
        return ResponseEntity.ok(request);
    }

    // 🔹 Reject exchange request
    @PostMapping("/request/{id}/reject")
    public ResponseEntity<ExchangeRequest> rejectExchangeRequest(@PathVariable Long id) {
        ExchangeRequest request = itemService.rejectExchangeRequest(id);
        return ResponseEntity.ok(request);
    }

    // 🔹 List exchange requests (optional filter by status)
    @GetMapping("/requests")
    public ResponseEntity<List<ExchangeRequest>> listExchangeRequests(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(itemService.listExchangeRequests(status));
    }
}
