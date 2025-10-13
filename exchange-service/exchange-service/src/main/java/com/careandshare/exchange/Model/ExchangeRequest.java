package com.careandshare.exchange.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "exchange_requests")
public class ExchangeRequest {

    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requesterItemId;  // Item offered by requester
    private Long requestedItemId;  // Item requested from another user

    private String status; // pending, accepted, rejected

    private LocalDateTime createdAt;

    public ExchangeRequest() {
        this.createdAt = LocalDateTime.now();
        this.status = "pending";
    }

}
