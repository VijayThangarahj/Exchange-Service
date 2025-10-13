package com.careandshare.exchange.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String type;

    @NotNull
    private String category; // exchange, donate, resell

    @NotNull
    private String itemCondition; // excellent, good, fair

    @Column(length = 1000)
    private String description;

    private String ownerName;
    private String ownerEmail;

    @NotNull(message = "Phone number is required")
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    private String address;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    private String imageType;

    private String status; // pending, approved, rejected

    private String location;

    private String preferredCategory;

    private Boolean shippingAvailable;

    @Column(name = "submitted_date")
    private String submittedDate;

    private String submittedBy; // Email of the user who submitted

    public Item() {}

    public Item(Long id, String title, String type, String category, String itemCondition,
                String description, String ownerName, String ownerEmail,
                String phoneNumber, String address, byte[] image, String imageType,
                String status, String location, String preferredCategory, Boolean shippingAvailable,
                String submittedDate, String submittedBy) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.category = category;
        this.itemCondition = itemCondition;
        this.description = description;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.image = image;
        this.imageType = imageType;
        this.status = status;
        this.location = location;
        this.preferredCategory = preferredCategory;
        this.shippingAvailable = shippingAvailable;
        this.submittedDate = submittedDate;
        this.submittedBy = submittedBy;
    }

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

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPreferredCategory() { return preferredCategory; }
    public void setPreferredCategory(String preferredCategory) { this.preferredCategory = preferredCategory; }

    public Boolean getShippingAvailable() { return shippingAvailable; }
    public void setShippingAvailable(Boolean shippingAvailable) { this.shippingAvailable = shippingAvailable; }

    public String getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(String submittedDate) { this.submittedDate = submittedDate; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }
}