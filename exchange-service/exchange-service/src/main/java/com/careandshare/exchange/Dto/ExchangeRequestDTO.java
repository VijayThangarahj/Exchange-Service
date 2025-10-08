package com.careandshare.exchange.Dto;


import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExchangeRequestDTO {
    @NotNull
    private Long requesterId;

    @NotNull
    private Long receiverId;

    @NotNull
    private Long requestedItemId;

    private Long offeredItemId; // nullable if asking for donation
}
