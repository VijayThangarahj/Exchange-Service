package com.careandshare.exchange.Service;

import com.careandshare.exchange.Model.ExchangeRequest;
import com.careandshare.exchange.Repository.ExchangeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExchangeRequestService {

    @Autowired
    private ExchangeRequestRepository exchangeRequestRepository;

    public ExchangeRequest createExchangeRequest(ExchangeRequest exchangeRequest) {
        // Set default values if not provided
        if (exchangeRequest.getStatus() == null) {
            exchangeRequest.setStatus("PENDING");
        }
        if (exchangeRequest.getExchangeMethod() == null) {
            exchangeRequest.setExchangeMethod("meet_in_person");
        }

        return exchangeRequestRepository.save(exchangeRequest);
    }

    public List<ExchangeRequest> getPendingRequests() {
        return exchangeRequestRepository.findByStatusIgnoreCase("PENDING");
    }

    public List<ExchangeRequest> getApprovedRequests() {
        return exchangeRequestRepository.findByStatusIgnoreCase("APPROVED");
    }

    public List<ExchangeRequest> getRejectedRequests() {
        return exchangeRequestRepository.findByStatusIgnoreCase("REJECTED");
    }

    public ExchangeRequest approveRequest(Long id) {
        ExchangeRequest request = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found with id: " + id));

        request.setStatus("APPROVED");
        request.setReviewedDate(LocalDateTime.now());

        return exchangeRequestRepository.save(request);
    }

    public ExchangeRequest rejectRequest(Long id) {
        ExchangeRequest request = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange request not found with id: " + id));

        request.setStatus("REJECTED");
        request.setReviewedDate(LocalDateTime.now());

        return exchangeRequestRepository.save(request);
    }

    public List<ExchangeRequest> getAllExchangeRequests() {
        return exchangeRequestRepository.findAll();
    }

    public Optional<ExchangeRequest> getExchangeRequestById(Long id) {
        return exchangeRequestRepository.findById(id);
    }

    public List<ExchangeRequest> getExchangeRequestsByExchangerEmail(String email) {
        return exchangeRequestRepository.findByExchangerEmail(email);
    }

    public List<ExchangeRequest> getExchangeRequestsByStatus(String status) {
        return exchangeRequestRepository.findByStatusIgnoreCase(status);
    }
}