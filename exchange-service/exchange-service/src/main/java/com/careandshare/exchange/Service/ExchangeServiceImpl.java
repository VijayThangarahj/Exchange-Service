package com.careandshare.exchange.Service;



import com.careandshare.exchange.Dto.ExchangeRequestDTO;
import com.careandshare.exchange.Dto.ExchangeResponseDTO;
import com.careandshare.exchange.Exception.ResourceNotFoundException;
import com.careandshare.exchange.Model.Exchange;
import com.careandshare.exchange.Model.Item;
import com.careandshare.exchange.Model.User;
import com.careandshare.exchange.Repository.ExchangeRepository;
import com.careandshare.exchange.Repository.ItemRepository;
import com.careandshare.exchange.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ExchangeServiceImpl(ExchangeRepository exchangeRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.exchangeRepository = exchangeRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ExchangeResponseDTO requestExchange(ExchangeRequestDTO req) {
        User requester = userRepository.findById(req.getRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Requester not found"));
        User receiver = userRepository.findById(req.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        Item requestedItem = itemRepository.findById(req.getRequestedItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Requested item not found"));

        Item offeredItem = null;
        if (req.getOfferedItemId() != null) {
            offeredItem = itemRepository.findById(req.getOfferedItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Offered item not found"));
        }

        Exchange ex = Exchange.builder()
                .requester(requester)
                .receiver(receiver)
                .requestedItem(requestedItem)
                .offeredItem(offeredItem)
                .status("PENDING")
                .createdAt(OffsetDateTime.now())
                .build();

        Exchange saved = exchangeRepository.save(ex);
        return toDTO(saved);
    }

    @Override
    public ExchangeResponseDTO acceptExchange(Long exchangeId, Long actingUserId) {
        Exchange ex = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange not found"));

        if (!ex.getReceiver().getId().equals(actingUserId)) {
            throw new IllegalStateException("Only receiver can accept the exchange");
        }

        ex.setStatus("ACCEPTED");
        // update item statuses
        Item reqItem = ex.getRequestedItem();
        reqItem.setStatus("EXCHANGED");
        itemRepository.save(reqItem);

        if (ex.getOfferedItem() != null) {
            Item off = ex.getOfferedItem();
            off.setStatus("EXCHANGED");
            itemRepository.save(off);
        }
        exchangeRepository.save(ex);
        return toDTO(ex);
    }

    @Override
    public ExchangeResponseDTO rejectExchange(Long exchangeId, Long actingUserId) {
        Exchange ex = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new ResourceNotFoundException("Exchange not found"));

        if (!ex.getReceiver().getId().equals(actingUserId)) {
            throw new IllegalStateException("Only receiver can reject the exchange");
        }
        ex.setStatus("REJECTED");
        exchangeRepository.save(ex);
        return toDTO(ex);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExchangeResponseDTO> getExchangesForUser(Long userId) {
        List<Exchange> reqs = exchangeRepository.findByRequesterId(userId);
        List<Exchange> recs = exchangeRepository.findByReceiverId(userId);
        return StreamConcat(reqs, recs).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExchangeResponseDTO getExchange(Long id) {
        Exchange ex = exchangeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Exchange not found"));
        return toDTO(ex);
    }

    // helper
    private ExchangeResponseDTO toDTO(Exchange ex) {
        return ExchangeResponseDTO.builder()
                .id(ex.getId())
                .requesterId(ex.getRequester().getId())
                .receiverId(ex.getReceiver().getId())
                .requestedItemId(ex.getRequestedItem() != null ? ex.getRequestedItem().getId() : null)
                .offeredItemId(ex.getOfferedItem() != null ? ex.getOfferedItem().getId() : null)
                .status(ex.getStatus())
                .createdAt(ex.getCreatedAt())
                .build();
    }

    // naive concat without duplicates
    private List<Exchange> StreamConcat(List<Exchange> a, List<Exchange> b) {
        return java.util.stream.Stream.concat(a.stream(), b.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
