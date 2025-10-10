package com.simplecrm.RequestDTO.Seller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SellerUpdateRequestDTO {

    @NotBlank(message = "Name cannot be blank for update")
    private String name;

    @NotBlank(message = "Contact info cannot be blank for update")
    private String contactInfo;

    @NotNull(message = "Version cannot be null for update")
    private Long version;
}