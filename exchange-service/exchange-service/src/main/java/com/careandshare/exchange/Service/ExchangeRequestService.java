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

    @Autowired
    public ExchangeRequestService(
            ExchangeRequestRepository exchangeRequestRepository,
            ItemRepository itemRepository) {
        this.exchangeRequestRepository = exchangeRequestRepository;
        this.itemRepository = itemRepository;
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

        // Log instead of sending email
        System.out.println("New exchange request created: " + savedRequest.getId());
        System.out.println("Requested Item: " + requestedItem.getTitle());
        System.out.println("Requester: " + exchangerName);

        return savedRequest;
    }

    public List<ExchangeRequest> getPendingRequests() {
        return exchangeRequestRepository.findByStatusOrderBySubmittedDateDesc("PENDING");
    }

    public List<ExchangeRequest> getApprovedRequests() {
        return exchangeRequestRepository.findByStatusOrderBySubmittedDateDesc("APPROVED");
    }

    public List<ExchangeRequest> getRejectedRequests() {
        return exchangeRequestRepository.findByStatusOrderBySubmittedDateDesc("REJECTED");
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

        // Log instead of sending email
        System.out.println("Exchange request approved: " + updatedRequest.getId());
        System.out.println("Item owner notified: " + updatedRequest.getItemOwnerEmail());

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

        return exchangeRequestRepository.save(request);
    }
}