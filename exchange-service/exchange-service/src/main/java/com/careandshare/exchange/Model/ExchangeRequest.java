package com.careandshare.exchange.Model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_requests")
public class ExchangeRequest {

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

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRequesterItemId() { return requesterItemId; }
    public void setRequesterItemId(Long requesterItemId) { this.requesterItemId = requesterItemId; }

    public Long getRequestedItemId() { return requestedItemId; }
    public void setRequestedItemId(Long requestedItemId) { this.requestedItemId = requestedItemId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
