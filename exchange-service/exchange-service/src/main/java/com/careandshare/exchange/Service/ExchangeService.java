package com.careandshare.exchange.Service;

import com.careandshare.exchange.Dto.ExchangeRequestDTO;
import com.careandshare.exchange.Dto.ExchangeResponseDTO;

import java.util.List;

public interface ExchangeService {
    ExchangeResponseDTO requestExchange(ExchangeRequestDTO req);
    ExchangeResponseDTO acceptExchange(Long exchangeId, Long actingUserId);
    ExchangeResponseDTO rejectExchange(Long exchangeId, Long actingUserId);
    List<ExchangeResponseDTO> getExchangesForUser(Long userId);
    ExchangeResponseDTO getExchange(Long id);
}
