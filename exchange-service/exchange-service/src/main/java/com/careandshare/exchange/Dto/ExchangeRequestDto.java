package com.careandshare.exchange.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequestDto {

    private Long id;
    private String exchangerName;
    private String exchangerEmail;
    private String exchangerPhone;
    private String requestedItemTitle;
    private String itemOwnerName;
    private String itemOwnerEmail;
    private String offeredItemTitle;
    private String offeredItemDescription;
    private String offeredItemImage;  // Base64 encoded image
    private String offeredImageType;
    private String exchangeMethod;
    private String message;
    private String preferredLocation;
    private String status;
    private LocalDateTime submittedDate;
    private LocalDateTime reviewedDate;

    // Constructor to convert ExchangeRequest entity to DTO
    public ExchangeRequestDto(com.careandshare.exchange.Model.ExchangeRequest request) {
        this.id = request.getId();
        this.exchangerName = request.getExchangerName();
        this.exchangerEmail = request.getExchangerEmail();
        this.exchangerPhone = request.getExchangerPhone();
        this.requestedItemTitle = request.getRequestedItemTitle();
        this.itemOwnerName = request.getItemOwnerName();
        this.itemOwnerEmail = request.getItemOwnerEmail();
        this.offeredItemTitle = request.getOfferedItemTitle();
        this.offeredItemDescription = request.getOfferedItemDescription();
        this.offeredImageType = request.getOfferedImageType();
        this.exchangeMethod = request.getExchangeMethod();
        this.message = request.getMessage();
        this.preferredLocation = request.getPreferredLocation();
        this.status = request.getStatus();
        this.submittedDate = request.getSubmittedDate();
        this.reviewedDate = request.getReviewedDate();

        // Convert byte array to Base64 string with proper null check
        if (request.getOfferedItemImage() != null && request.getOfferedItemImage().length > 0) {
            this.offeredItemImage = Base64.getEncoder().encodeToString(request.getOfferedItemImage());
        } else {
            this.offeredItemImage = null; // or set to empty string if preferred
        }
    }
}