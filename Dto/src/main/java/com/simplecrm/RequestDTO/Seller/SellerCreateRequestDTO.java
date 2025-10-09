package com.simplecrm.RequestDTO.Seller;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SellerCreateRequestDTO {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Contact info cannot be blank")
    private String contactInfo;
}