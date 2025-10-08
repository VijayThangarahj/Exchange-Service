package com.careandshare.exchange.Model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // book/clothes
    private String conditionDesc;
    private String status; // AVAILABLE, PENDING, EXCHANGED, SOLD

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
