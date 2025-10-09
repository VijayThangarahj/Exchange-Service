package com.careandshare.exchange.Dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ItemDto {

    @NotNull
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String type;

    /**
     * Category should be one of: resell, donate, exchange (case-insensitive)
     */
    @NotNull
    @Pattern(regexp = "^(?i)(resell|donate|exchange)$", message = "Category must be exchange, donate or resell")
    private String category;

    @NotNull
    private String itemCondition;

    private String description;
    private Double price;
    private String ownerName;
    private String ownerEmail;

    @NotNull
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    private String address;

    /**
     * Status should be one of: pending, available, sold (case-insensitive)
     */
    @Pattern(regexp = "^(?i)(pending|available|sold)$", message = "Status must be pending, available or sold")
    private String status;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getItemCondition() { return itemCondition; }
    public void setItemCondition(String itemCondition) { this.itemCondition = itemCondition; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
