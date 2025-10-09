package com.simplecrm.Services;

import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Repositories.SellerRepository;
import com.simplecrm.RequestDTO.Seller.SellerCreateRequestDTO;
import com.simplecrm.RequestDTO.Seller.SellerUpdateRequestDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResultTypes.SellerResult;
import com.simplecrm.Services.Interfaces.SellerService;
import com.simplecrm.Utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {

    private final SellerRepository sellerRepository;
    private final Mapper mapper;

    @Override
    @Async
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<SellerResult> createSeller(@Valid SellerCreateRequestDTO requestDto) {
        try {
            Seller seller = new Seller();
            seller.setName(requestDto.getName());
            seller.setContactInfo(requestDto.getContactInfo());
            seller.setRegistrationDate(LocalDateTime.now());

            Seller savedEntity = sellerRepository.save(seller);
            SellerResponseDTO responseDto = mapper.mapEntityToSellerResponseDto(savedEntity);

            return CompletableFuture.completedFuture(new SellerResult.Success(responseDto));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    new SellerResult.GenericError("Error creating seller: " + e.getMessage())
            );
        }
    }

    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<SellerResult> getSellerById(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.completedFuture(new SellerResult.ValidationError("Seller ID must be positive"));
        }

        return CompletableFuture.completedFuture(
                sellerRepository.findNotDeletedById(id)
                        .map(seller -> (SellerResult) new SellerResult.Success(mapper.mapEntityToSellerResponseDto(seller)))
                        .orElse(new SellerResult.NotFoundError("Seller not found with id: " + id))
        );
    }

    @Override
    @Async
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<List<SellerResponseDTO>> getAllSellers() {
        try {
            List<SellerResponseDTO> sellers = sellerRepository.findAllNotDeleted().stream()
                    .map(mapper::mapEntityToSellerResponseDto)
                    .collect(Collectors.toList());
            return CompletableFuture.completedFuture(sellers);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(List.of());
        }
    }

    @Override
    @Async
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<SellerResult> updateSeller(Long id, @Valid SellerUpdateRequestDTO requestDto) {
        if (id == null || id <= 0) {
            return CompletableFuture.completedFuture(new SellerResult.ValidationError("Seller ID must be positive"));
        }

        return CompletableFuture.completedFuture(
                sellerRepository.findNotDeletedById(id)
                        .map(existingSeller -> {
                            try {
                                if (!existingSeller.getVersion().equals(requestDto.getVersion())) {
                                    return new SellerResult.ValidationError(
                                            "Data is stale, please refresh and try again. Expected version: "
                                                    + requestDto.getVersion() + ", but found: " + existingSeller.getVersion()
                                    );
                                }

                                existingSeller.setName(requestDto.getName());
                                existingSeller.setContactInfo(requestDto.getContactInfo());

                                Seller updatedEntity = sellerRepository.save(existingSeller);
                                SellerResponseDTO responseDto = mapper.mapEntityToSellerResponseDto(updatedEntity);
                                return new SellerResult.Success(responseDto);

                            } catch (ObjectOptimisticLockingFailureException e) {
                                return new SellerResult.GenericError("Concurrent update error. Please try again.");
                            } catch (Exception e) {
                                return new SellerResult.GenericError("Error updating seller: " + e.getMessage());
                            }
                        })
                        .orElse(new SellerResult.NotFoundError("Seller not found with id: " + id))
        );
    }

    @Override
    @Async
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<SellerResult> deleteSellerByIdSoft(Long id) {
        if (id == null || id <= 0) {
            return CompletableFuture.completedFuture(new SellerResult.ValidationError("Seller ID must be positive"));
        }

        return CompletableFuture.completedFuture(
                sellerRepository.findNotDeletedById(id)
                        .map(seller -> {
                            try {
                                seller.setDeleted(true);
                                sellerRepository.save(seller);
                                return new SellerResult.Success(mapper.mapEntityToSellerResponseDto(seller));
                            } catch (Exception e) {
                                return new SellerResult.GenericError("Error deleting seller: " + e.getMessage());
                            }
                        })
                        .orElse(new SellerResult.NotFoundError("Seller not found with id: " + id))
        );
    }

    @Override
    @Async
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompletableFuture<SellerResult> deleteSellerByIdHard(Long id) {
        try {
            if (id == null || id <= 0) {
                return CompletableFuture.completedFuture(new SellerResult.ValidationError("Seller ID must be positive"));
            }

            return CompletableFuture.supplyAsync(() -> {
                var optionalSeller = sellerRepository.findById(id);
                if (optionalSeller.isEmpty()) {
                    return new SellerResult.NotFoundError("Seller not found with id: " + id);
                }

                try {
                    sellerRepository.delete(optionalSeller.get());
                    return new SellerResult.Success(null);
                } catch (Exception e) {
                    return new SellerResult.GenericError("Error performing hard delete: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    new SellerResult.GenericError("Error deleting seller: " + e.getMessage())
            );
        }
    }
}

