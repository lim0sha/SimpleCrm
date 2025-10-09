package com.simplecrm.ResponseDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SellerResponseDTO {
    private Long id;
    private String name;
    private String contactInfo;
    private LocalDateTime registrationDate;
    private Long version;
}