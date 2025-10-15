package com.careandshare.exchange.Service;



import com.careandshare.exchange.Model.ExchangeRequest;
import com.careandshare.exchange.Model.Item;
import com.careandshare.exchange.Repository.ExchangeRequestRepository;
import com.careandshare.exchange.Repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final ItemRepository itemRepository;
    private final EmailService emailService; // Added EmailService

    @Autowired
    public ExchangeRequestService(
            ExchangeRequestRepository exchangeRequestRepository,
            ItemRepository itemRepository,
            EmailService emailService) { // Added EmailService parameter
        this.exchangeRequestRepository = exchangeRequestRepository;
        this.itemRepository = itemRepository;
        this.emailService = emailService;
    }

    public ExchangeRequest createExchangeRequest(
            String exchangerName,
            String exchangerEmail,
            String exchangerPhone,
            Long requestedItemId,
            String offeredItemTitle,
            String offeredItemDescription,
            MultipartFile offeredItemImage,
            String exchangeMethod,
            String message,
            String preferredLocation) throws IOException {

        // Get the requested item details
        Optional<Item> requestedItemOpt = itemRepository.findById(requestedItemId);
        if (requestedItemOpt.isEmpty()) {
            throw new RuntimeException("Requested item not found");
        }

        Item requestedItem = requestedItemOpt.get();

        // Create exchange request
        ExchangeRequest exchangeRequest = new ExchangeRequest();
        exchangeRequest.setExchangerName(exchangerName);
        exchangeRequest.setExchangerEmail(exchangerEmail);
        exchangeRequest.setExchangerPhone(exchangerPhone);
        exchangeRequest.setRequestedItemId(requestedItemId);
        exchangeRequest.setRequestedItemTitle(requestedItem.getTitle());
        exchangeRequest.setItemOwnerName(requestedItem.getOwnerName());
        exchangeRequest.setItemOwnerEmail(requestedItem.getOwnerEmail());
        exchangeRequest.setOfferedItemTitle(offeredItemTitle);
        exchangeRequest.setOfferedItemDescription(offeredItemDescription);
        exchangeRequest.setExchangeMethod(exchangeMethod);
        exchangeRequest.setMessage(message);
        exchangeRequest.setPreferredLocation(preferredLocation);
        exchangeRequest.setStatus("PENDING");

        // Handle image upload
        if (offeredItemImage != null && !offeredItemImage.isEmpty()) {
            exchangeRequest.setOfferedItemImage(offeredItemImage.getBytes());
            exchangeRequest.setOfferedImageType(offeredItemImage.getContentType());
        }

        ExchangeRequest savedRequest = exchangeRequestRepository.save(exchangeRequest);

        // Send notification email to item owner
        try {
            sendNewExchangeRequestEmail(savedRequest);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
            // Don't throw exception - email failure shouldn't block the request
        }

        System.out.println("New exchange request created: " + savedRequest.getId());
        System.out.println("Requested Item: " + requestedItem.getTitle());
        System.out.println("Requester: " + exchangerName);

        return savedRequest;
    }

    public ExchangeRequest approveRequest(Long requestId, String adminNotes) {
        Optional<ExchangeRequest> requestOpt = exchangeRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Exchange request not found");
        }

        ExchangeRequest request = requestOpt.get();
        request.setStatus("APPROVED");
        request.setReviewedDate(LocalDateTime.now());
        request.setAdminNotes(adminNotes);

        ExchangeRequest updatedRequest = exchangeRequestRepository.save(request);

        // Send approval emails to both parties
        try {
            sendApprovalEmails(updatedRequest);
        } catch (Exception e) {
            System.err.println("Failed to send approval emails: " + e.getMessage());
            // Don't throw exception - email failure shouldn't block the approval
        }

        System.out.println("Exchange request approved: " + updatedRequest.getId());
        System.out.println("Item owner notified: " + updatedRequest.getItemOwnerEmail());
        System.out.println("Exchanger notified: " + updatedRequest.getExchangerEmail());

        return updatedRequest;
    }

    public ExchangeRequest rejectRequest(Long requestId, String adminNotes) {
        Optional<ExchangeRequest> requestOpt = exchangeRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Exchange request not found");
        }

        ExchangeRequest request = requestOpt.get();
        request.setStatus("REJECTED");
        request.setReviewedDate(LocalDateTime.now());
        request.setAdminNotes(adminNotes);

        // Send rejection email
        try {
            sendRejectionEmail(request);
        } catch (Exception e) {
            System.err.println("Failed to send rejection email: " + e.getMessage());
        }

        return exchangeRequestRepository.save(request);
    }

    // Email sending methods
    private void sendNewExchangeRequestEmail(ExchangeRequest request) {
        String subject = "New Exchange Request - CareAndShare";
        String ownerContent = String.format(
                "Dear %s,\n\n" +
                        "You have received a new exchange request for your item '%s'.\n\n" +
                        "Requester Details:\n" +
                        "- Name: %s\n" +
                        "- Email: %s\n" +
                        "- Phone: %s\n\n" +
                        "Offered Item: %s\n" +
                        "Description: %s\n\n" +
                        "Message from requester:\n%s\n\n" +
                        "Please log in to your account to review this request.\n\n" +
                        "Best regards,\nCareAndShare Team",
                request.getItemOwnerName(),
                request.getRequestedItemTitle(),
                request.getExchangerName(),
                request.getExchangerEmail(),
                request.getExchangerPhone(),
                request.getOfferedItemTitle(),
                request.getOfferedItemDescription(),
                request.getMessage()
        );

        emailService.sendSimpleEmail(
                request.getItemOwnerEmail(),
                subject,
                ownerContent
        );
    }

    private void sendApprovalEmails(ExchangeRequest request) {
        // Email to item owner
        String ownerSubject = "Exchange Request Approved - CareAndShare";
        String ownerContent = String.format(
                "Dear %s,\n\n" +
                        "Your exchange request for item '%s' has been approved!\n\n" +
                        "Exchanger Details:\n" +
                        "- Name: %s\n" +
                        "- Email: %s\n" +
                        "- Phone: %s\n\n" +
                        "You can now contact the exchanger to arrange the exchange details.\n\n" +
                        "Best regards,\nCareAndShare Team",
                request.getItemOwnerName(),
                request.getRequestedItemTitle(),
                request.getExchangerName(),
                request.getExchangerEmail(),
                request.getExchangerPhone()
        );

        // Email to exchanger
        String exchangerSubject = "Exchange Request Approved - CareAndShare";
        String exchangerContent = String.format(
                "Dear %s,\n\n" +
                        "Your exchange request for item '%s' has been approved!\n\n" +
                        "Item Owner Details:\n" +
                        "- Name: %s\n" +
                        "- Email: %s\n\n" +
                        "You can now contact the item owner to arrange the exchange details.\n\n" +
                        "Best regards,\nCareAndShare Team",
                request.getExchangerName(),
                request.getRequestedItemTitle(),
                request.getItemOwnerName(),
                request.getItemOwnerEmail()
        );

        // Send emails
        emailService.sendSimpleEmail(request.getItemOwnerEmail(), ownerSubject, ownerContent);
        emailService.sendSimpleEmail(request.getExchangerEmail(), exchangerSubject, exchangerContent);
    }

    private void sendRejectionEmail(ExchangeRequest request) {
        String subject = "Exchange Request Status Update - CareAndShare";
        String content = String.format(
                "Dear %s,\n\n" +
                        "Your exchange request for item '%s' has been reviewed.\n\n" +
                        "Status: REJECTED\n" +
                        "Admin Notes: %s\n\n" +
                        "If you have any questions, please contact our support team.\n\n" +
                        "Best regards,\nCareAndShare Team",
                request.getExchangerName(),
                request.getRequestedItemTitle(),
                request.getAdminNotes() != null ? request.getAdminNotes() : "No additional notes provided"
        );

        emailService.sendSimpleEmail(request.getExchangerEmail(), subject, content);
    }

    // Existing methods remain the same...
    public List<ExchangeRequest> getPendingRequests() {
        return exchangeRequestRepository.findByStatusOrderBySubmittedDateDesc("PENDING");
    }

    public List<ExchangeRequest> getApprovedRequests() {
        return exchangeRequestRepository.findByStatusOrderBySubmittedDateDesc("APPROVED");
    }

    public List<ExchangeRequest> getRejectedRequests() {
        return exchangeRequestRepository.findByStatusOrderBySubmittedDateDesc("REJECTED");
    }

    public List<ExchangeRequest> getAllRequests() {
        return exchangeRequestRepository.findAllByOrderBySubmittedDateDesc();
    }

    public List<ExchangeRequest> getRequestsByStatus(String status) {
        return exchangeRequestRepository.findByStatusIgnoreCase(status);
    }

    public ExchangeRequest getRequestById(Long id) {
        return exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found"));
    }
}