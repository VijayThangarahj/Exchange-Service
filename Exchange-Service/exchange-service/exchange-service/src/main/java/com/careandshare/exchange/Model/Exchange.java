package com.careandshare.exchange.Model;


import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "exchanges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exchange {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester; // who initiated

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver; // owner of requested item

    @ManyToOne
    @JoinColumn(name = "requested_item_id")
    private Item requestedItem; // item requester wants

    @ManyToOne
    @JoinColumn(name = "offered_item_id")
    private Item offeredItem; // item offered in exchange (can be null)

    @Column(nullable=false)
    private String status; // PENDING, ACCEPTED, REJECTED, CANCELLED

    private OffsetDateTime createdAt;
}
