package com.careandshare.exchange.Dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatContactDto {
    private Long id;
    private String name;
    private String role; // "Owner" or "Exchanger"
    private String item;
    private String phone;
}
