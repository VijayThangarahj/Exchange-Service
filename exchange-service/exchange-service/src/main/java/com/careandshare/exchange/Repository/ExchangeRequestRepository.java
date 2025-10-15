package com.careandshare.exchange.Repository;

import com.careandshare.exchange.Model.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    List<ExchangeRequest> findByStatusIgnoreCase(String status);
    List<ExchangeRequest> findByExchangerEmail(String email);
    List<ExchangeRequest> findByExchangerEmailIgnoreCase(String email);
}