package com.careandshare.exchange.Repository;


import com.careandshare.exchange.Model.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

    // Optional: find by status
    List<ExchangeRequest> findByStatusIgnoreCase(String status);

    // Find requests for a specific item
    List<ExchangeRequest> findByRequestedItemIdOrRequesterItemId(Long requestedItemId, Long requesterItemId);
}
