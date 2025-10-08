package com.careandshare.exchange.Dto;


import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExchangeResponseDTO {
    private Long id;
    private Long requesterId;
    private Long receiverId;
    private Long requestedItemId;
    private Long offeredItemId;
    private String status;
    private OffsetDateTime createdAt;
}
