package com.careandshare.exchange.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Requester Information
    @Column(name = "exchanger_name", nullable = false)
    private String exchangerName;

    @Column(name = "exchanger_email", nullable = false)
    private String exchangerEmail;

    @Column(name = "exchanger_phone", nullable = false)
    private String exchangerPhone;

    // Item Being Requested
    @Column(name = "requested_item_id", nullable = false)
    private Long requestedItemId;

    @Column(name = "requested_item_title")
    private String requestedItemTitle;

    @Column(name = "item_owner_name")
    private String itemOwnerName;

    @Column(name = "item_owner_email")
    private String itemOwnerEmail;

    // Item Being Offered
    @Column(name = "offered_item_id")
    private Long offeredItemId;

    @Column(name = "offered_item_title")
    private String offeredItemTitle;

    @Column(name = "offered_item_description", length = 1000)
    private String offeredItemDescription;

    @Lob
    @Column(name = "offered_item_image", columnDefinition = "LONGBLOB")
    private byte[] offeredItemImage;

    @Column(name = "offered_image_type")
    private String offeredImageType;

    // Exchange Details
    @Column(name = "exchange_method", nullable = false)
    private String exchangeMethod; // "meet_in_person" or "shipping"

    @Column(name = "message", length = 2000)
    private String message;

    @Column(name = "preferred_location")
    private String preferredLocation;

    // Status and Timestamps
    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, COMPLETED

    @Column(name = "submitted_date")
    private LocalDateTime submittedDate;

    @Column(name = "reviewed_date")
    private LocalDateTime reviewedDate;

    @Column(name = "admin_notes", length = 1000)
    private String adminNotes;

    @PrePersist
    protected void onCreate() {
        submittedDate = LocalDateTime.now();
    }
}