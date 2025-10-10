package com.simplecrm.Utils;

import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Models.Entities.Transaction;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public TransactionResponseDTO mapEntityToTransactionResponseDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        if (transaction.getSeller() != null) {
            dto.setSeller(mapSellerEntityToResponseDto(transaction.getSeller()));
        }
        dto.setAmount(transaction.getAmount());
        dto.setPaymentType(transaction.getPaymentType());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setVersion(transaction.getVersion());
        return dto;
    }

    public SellerResponseDTO mapSellerEntityToResponseDto(Seller seller) {
        if (seller == null) {
            return null;
        }
        SellerResponseDTO dto = new SellerResponseDTO();
        dto.setId(seller.getId());
        dto.setName(seller.getName());
        dto.setContactInfo(seller.getContactInfo());
        dto.setRegistrationDate(seller.getRegistrationDate());
        dto.setVersion(seller.getVersion());
        return dto;
    }

    public SellerResponseDTO mapEntityToSellerResponseDto(Seller seller) {
        if (seller == null) {
            return null;
        }
        SellerResponseDTO dto = new SellerResponseDTO();
        dto.setId(seller.getId());
        dto.setName(seller.getName());
        dto.setContactInfo(seller.getContactInfo());
        dto.setRegistrationDate(seller.getRegistrationDate());
        return dto;
    }
}
