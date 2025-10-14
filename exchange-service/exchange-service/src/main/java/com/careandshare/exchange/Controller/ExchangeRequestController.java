package com.careandshare.exchange.Controller;


import com.careandshare.exchange.Model.ExchangeRequest;
import com.careandshare.exchange.Service.ExchangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/exchange-requests")
@CrossOrigin(origins = "*")
public class ExchangeRequestController {

    @Autowired
    private ExchangeRequestService exchangeRequestService;

    @PostMapping("/create")
    public ResponseEntity<?> createExchangeRequest(
            @RequestParam String exchangerName,
            @RequestParam String exchangerEmail,
            @RequestParam String exchangerPhone,
            @RequestParam Long requestedItemId,
            @RequestParam String offeredItemTitle,
            @RequestParam String offeredItemDescription,
            @RequestParam(required = false) MultipartFile offeredItemImage,
            @RequestParam String exchangeMethod,
            @RequestParam String message,
            @RequestParam(required = false) String preferredLocation) {

        try {
            ExchangeRequest request = exchangeRequestService.createExchangeRequest(
                    exchangerName, exchangerEmail, exchangerPhone, requestedItemId,
                    offeredItemTitle, offeredItemDescription, offeredItemImage,
                    exchangeMethod, message, preferredLocation);

            return ResponseEntity.ok(request);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating exchange request: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ExchangeRequest>> getPendingRequests() {
        return ResponseEntity.ok(exchangeRequestService.getPendingRequests());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ExchangeRequest>> getApprovedRequests() {
        return ResponseEntity.ok(exchangeRequestService.getApprovedRequests());
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<ExchangeRequest>> getRejectedRequests() {
        return ResponseEntity.ok(exchangeRequestService.getRejectedRequests());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String adminNotes) {
        try {
            ExchangeRequest request = exchangeRequestService.approveRequest(id, adminNotes);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving request: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long id,
            @RequestParam(required = false) String adminNotes) {
        try {
            ExchangeRequest request = exchangeRequestService.rejectRequest(id, adminNotes);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error rejecting request: " + e.getMessage());
        }
    }
}