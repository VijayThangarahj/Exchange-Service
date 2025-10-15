package com.careandshare.exchange.Controller;

import com.careandshare.exchange.Dto.ExchangeRequestDto;
import com.careandshare.exchange.Model.ExchangeRequest;
import com.careandshare.exchange.Service.ExchangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/exchange-requests")
@CrossOrigin(origins = "*")
public class ExchangeRequestController {

    @Autowired
    private ExchangeRequestService exchangeRequestService;

    @PostMapping("/debug")
    public ResponseEntity<?> debugRequest(HttpServletRequest request) {
        System.out.println("=== DEBUG REQUEST PARAMETERS ===");
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            System.out.println(paramName + ": " + paramValue);
        }
        System.out.println("=== END DEBUG ===");
        return ResponseEntity.ok("Check console for parameters");
    }

    // Main endpoint for creating exchange requests
    @PostMapping("/create")
    public ResponseEntity<?> createExchangeRequest(
            @RequestParam String exchangerName,
            @RequestParam String exchangerEmail,
            @RequestParam String exchangerPhone,
            @RequestParam(required = false) String requestedItemTitle,  // Made optional
            @RequestParam(required = false) String itemOwnerName,       // Made optional
            @RequestParam(required = false) String itemOwnerEmail,      // Made optional
            @RequestParam String offeredItemTitle,
            @RequestParam String offeredItemDescription,
            @RequestParam(required = false) MultipartFile offeredItemImage,
            @RequestParam String exchangeMethod,
            @RequestParam String message,
            @RequestParam(required = false) String preferredLocation) {

        try {
            System.out.println("=== CREATE EXCHANGE REQUEST PARAMETERS ===");
            System.out.println("exchangerName: " + exchangerName);
            System.out.println("exchangerEmail: " + exchangerEmail);
            System.out.println("exchangerPhone: " + exchangerPhone);
            System.out.println("requestedItemTitle: " + requestedItemTitle);
            System.out.println("itemOwnerName: " + itemOwnerName);
            System.out.println("itemOwnerEmail: " + itemOwnerEmail);
            System.out.println("offeredItemTitle: " + offeredItemTitle);
            System.out.println("offeredItemDescription: " + offeredItemDescription);
            System.out.println("exchangeMethod: " + exchangeMethod);
            System.out.println("message: " + message);
            System.out.println("preferredLocation: " + preferredLocation);
            System.out.println("offeredItemImage: " + (offeredItemImage != null ? offeredItemImage.getOriginalFilename() : "null"));
            System.out.println("=== END PARAMETERS ===");

            ExchangeRequest request = new ExchangeRequest();

            // Requester Information
            request.setExchangerName(exchangerName);
            request.setExchangerEmail(exchangerEmail);
            request.setExchangerPhone(exchangerPhone);

            // Requested Item Information (with fallback defaults)
            request.setRequestedItemTitle(requestedItemTitle != null ? requestedItemTitle : "Item to Exchange");
            request.setItemOwnerName(itemOwnerName != null ? itemOwnerName : "Item Owner");
            request.setItemOwnerEmail(itemOwnerEmail != null ? itemOwnerEmail : "owner@example.com");

            // Offered Item Information
            request.setOfferedItemTitle(offeredItemTitle);
            request.setOfferedItemDescription(offeredItemDescription);

            // Exchange Details
            request.setExchangeMethod(exchangeMethod);
            request.setMessage(message);
            request.setPreferredLocation(preferredLocation != null ? preferredLocation : "To be discussed");

            // Status - Set to PENDING so admin can see it in exchange request tab
            request.setStatus("PENDING");

            // Handle image if provided
            if (offeredItemImage != null && !offeredItemImage.isEmpty()) {
                request.setOfferedItemImage(offeredItemImage.getBytes());
                request.setOfferedImageType(offeredItemImage.getContentType());
            }

            ExchangeRequest savedRequest = exchangeRequestService.createExchangeRequest(request);
            return ResponseEntity.ok(savedRequest);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error processing image: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
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
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        try {
            ExchangeRequest request = exchangeRequestService.approveRequest(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error approving request: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        try {
            ExchangeRequest request = exchangeRequestService.rejectRequest(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error rejecting request: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ExchangeRequest>> getAllExchangeRequests() {
        return ResponseEntity.ok(exchangeRequestService.getAllExchangeRequests());
    }
    @GetMapping("/dto")
    public ResponseEntity<List<ExchangeRequestDto>> getAllExchangeRequestsAsDto() {
        List<ExchangeRequest> requests = exchangeRequestService.getAllExchangeRequests();
        List<ExchangeRequestDto> dtos = requests.stream()
                .map(ExchangeRequestDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExchangeRequestById(@PathVariable Long id) {
        try {
            ExchangeRequest request = exchangeRequestService.getExchangeRequestById(id)
                    .orElseThrow(() -> new RuntimeException("Exchange request not found"));
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching request: " + e.getMessage());
        }
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<ExchangeRequest>> getExchangeRequestsByUser(@PathVariable String email) {
        return ResponseEntity.ok(exchangeRequestService.getExchangeRequestsByExchangerEmail(email));
    }
}