package com.simplecrm.Services.Interfaces;

import com.simplecrm.RequestDTO.Seller.SellerCreateRequestDTO;
import com.simplecrm.RequestDTO.Seller.SellerUpdateRequestDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResultTypes.SellerResult;
import jakarta.validation.Valid;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SellerService {
    CompletableFuture<SellerResult> createSeller(@Valid SellerCreateRequestDTO requestDto);

    CompletableFuture<SellerResult> getSellerById(Long id);

    CompletableFuture<List<SellerResponseDTO>> getAllSellers();

    CompletableFuture<SellerResult> updateSeller(Long id, @Valid SellerUpdateRequestDTO requestDto);

    CompletableFuture<SellerResult> deleteSellerByIdSoft(Long id);

    CompletableFuture<SellerResult> deleteSellerByIdHard(Long id);
}