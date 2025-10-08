package com.careandshare.exchange.Controller;


import com.careandshare.exchange.Dto.ExchangeRequestDTO;
import com.careandshare.exchange.Dto.ExchangeResponseDTO;
import com.careandshare.exchange.Service.ExchangeService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    /**
     * Create a new exchange request between two users
     */
    @PostMapping("/request")
    public ResponseEntity<ExchangeResponseDTO> requestExchange(@Valid @RequestBody ExchangeRequestDTO dto) {
        ExchangeResponseDTO res = exchangeService.requestExchange(dto);
        return ResponseEntity.ok(res);
    }

    /**
     * Accept an exchange request (only receiver can accept)
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<ExchangeResponseDTO> acceptExchange(
            @PathVariable Long id,
            @RequestParam Long actingUserId) {
        ExchangeResponseDTO res = exchangeService.acceptExchange(id, actingUserId);
        return ResponseEntity.ok(res);
    }

    /**
     * Reject an exchange request (only receiver can reject)
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<ExchangeResponseDTO> rejectExchange(
            @PathVariable Long id,
            @RequestParam Long actingUserId) {
        ExchangeResponseDTO res = exchangeService.rejectExchange(id, actingUserId);
        return ResponseEntity.ok(res);
    }

    /**
     * Get all exchanges for a given user (both sent and received)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExchangeResponseDTO>> getUserExchanges(@PathVariable Long userId) {
        List<ExchangeResponseDTO> exchanges = exchangeService.getExchangesForUser(userId);
        return ResponseEntity.ok(exchanges);
    }

    /**
     * Get details of a single exchange
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExchangeResponseDTO> getExchangeById(@PathVariable Long id) {
        ExchangeResponseDTO exchange = exchangeService.getExchange(id);
        return ResponseEntity.ok(exchange);
    }
}
