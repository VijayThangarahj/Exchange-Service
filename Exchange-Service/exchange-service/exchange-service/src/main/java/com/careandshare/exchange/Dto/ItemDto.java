package com.careandshare.exchange.Dto;
// ItemDto.java

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ItemDto {

    // Getters and Setters
    private Long id;

    @NotNull
    private String title;

    private String type;

    @NotNull
    @Pattern(regexp = "^(?i)(resell|donate|exchange)$", message = "Category must be exchange, donate or resell")
    private String category;

    @NotNull
    private String itemCondition;

    private String description;
    private String ownerName;
    private String ownerEmail;

    @NotNull
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    private String address;
    private String location;
    private String preferredCategory;
    private Boolean shippingAvailable;

    @Pattern(regexp = "^(?i)(pending|available|sold)$", message = "Status must be pending, available or sold")
    private String status;

    public void setId(Long id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setType(String type) { this.type = type; }

    public void setCategory(String category) { this.category = category; }

    public void setItemCondition(String itemCondition) { this.itemCondition = itemCondition; }

    public void setDescription(String description) { this.description = description; }

    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public void setAddress(String address) { this.address = address; }

    public void setStatus(String status) { this.status = status; }

    public void setLocation(String location) { this.location = location; }

    public void setPreferredCategory(String preferredCategory) { this.preferredCategory = preferredCategory; }

    public void setShippingAvailable(Boolean shippingAvailable) { this.shippingAvailable = shippingAvailable; }
}