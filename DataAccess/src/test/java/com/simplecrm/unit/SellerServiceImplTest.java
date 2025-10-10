package com.simplecrm.unit;

import com.simplecrm.Models.Entities.Seller;
import com.simplecrm.Repositories.SellerRepository;
import com.simplecrm.RequestDTO.Seller.SellerCreateRequestDTO;
import com.simplecrm.RequestDTO.Seller.SellerUpdateRequestDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResultTypes.SellerResult;
import com.simplecrm.Services.SellerServiceImpl;
import com.simplecrm.Utils.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private SellerServiceImpl sellerService;

    private Seller testSeller;
    private SellerResponseDTO testResponseDTO;
    private SellerCreateRequestDTO createRequestDTO;
    private SellerUpdateRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {
        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setName("Test Seller");
        testSeller.setContactInfo("test@example.com");
        testSeller.setRegistrationDate(LocalDateTime.now());
        testSeller.setVersion(1L);

        testResponseDTO = new SellerResponseDTO();
        testResponseDTO.setId(1L);
        testResponseDTO.setName("Test Seller");
        testResponseDTO.setContactInfo("test@example.com");
        testResponseDTO.setRegistrationDate(LocalDateTime.now());
        testResponseDTO.setVersion(1L);

        createRequestDTO = new SellerCreateRequestDTO();
        createRequestDTO.setName("New Seller");
        createRequestDTO.setContactInfo("new@example.com");

        updateRequestDTO = new SellerUpdateRequestDTO();
        updateRequestDTO.setName("Updated Seller");
        updateRequestDTO.setContactInfo("updated@example.com");
        updateRequestDTO.setVersion(1L);
    }

    @Test
    void createSeller_success_returnsSuccessResult() throws Exception {
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);
        when(mapper.mapEntityToSellerResponseDto(any(Seller.class))).thenReturn(testResponseDTO);

        CompletableFuture<SellerResult> future = sellerService.createSeller(createRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.Success.class, result);
        SellerResult.Success success = (SellerResult.Success) result;
        assertEquals(testResponseDTO, success.seller());
        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void createSeller_exception_returnsGenericError() throws Exception {
        when(sellerRepository.save(any(Seller.class))).thenThrow(new RuntimeException("Database error"));

        CompletableFuture<SellerResult> future = sellerService.createSeller(createRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.GenericError.class, result);
        SellerResult.GenericError error = (SellerResult.GenericError) result;
        assertTrue(error.message().contains("Error creating seller"));
        assertTrue(error.message().contains("Database error"));
    }
    
    @Test
    void getSellerById_validId_success() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(mapper.mapEntityToSellerResponseDto(testSeller)).thenReturn(testResponseDTO);

        CompletableFuture<SellerResult> future = sellerService.getSellerById(1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.Success.class, result);
        SellerResult.Success success = (SellerResult.Success) result;
        assertEquals(testResponseDTO, success.seller()); 
    }

    @Test
    void getSellerById_nullId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.getSellerById(null);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void getSellerById_zeroId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.getSellerById(0L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void getSellerById_negativeId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.getSellerById(-1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void getSellerById_notFound_returnsNotFoundError() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.empty());

        CompletableFuture<SellerResult> future = sellerService.getSellerById(1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.NotFoundError.class, result);
        SellerResult.NotFoundError error = (SellerResult.NotFoundError) result;
        assertEquals("Seller not found with id: 1", error.message()); 
    }
    
    @Test
    void getAllSellers_success_returnsList() throws Exception {
        List<Seller> sellers = List.of(testSeller);
        List<SellerResponseDTO> expectedDtos = List.of(testResponseDTO);

        when(sellerRepository.findAllNotDeleted()).thenReturn(sellers);
        when(mapper.mapEntityToSellerResponseDto(testSeller)).thenReturn(testResponseDTO);

        CompletableFuture<List<SellerResponseDTO>> future = sellerService.getAllSellers();
        List<SellerResponseDTO> result = future.get();

        assertEquals(expectedDtos, result);
    }

    @Test
    void getAllSellers_exception_returnsEmptyList() throws Exception {
        when(sellerRepository.findAllNotDeleted()).thenThrow(new RuntimeException("Database error"));

        CompletableFuture<List<SellerResponseDTO>> future = sellerService.getAllSellers();
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }
    
    @Test
    void updateSeller_success_returnsUpdatedSeller() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);
        when(mapper.mapEntityToSellerResponseDto(any(Seller.class))).thenReturn(testResponseDTO);

        CompletableFuture<SellerResult> future = sellerService.updateSeller(1L, updateRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.Success.class, result);
        SellerResult.Success success = (SellerResult.Success) result;
        assertEquals(testResponseDTO, success.seller()); 
        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void updateSeller_nullId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.updateSeller(null, updateRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void updateSeller_zeroId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.updateSeller(0L, updateRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void updateSeller_negativeId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.updateSeller(-1L, updateRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void updateSeller_notFound_returnsNotFoundError() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.empty());

        CompletableFuture<SellerResult> future = sellerService.updateSeller(1L, updateRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.NotFoundError.class, result);
        SellerResult.NotFoundError error = (SellerResult.NotFoundError) result;
        assertEquals("Seller not found with id: 1", error.message()); 
    }

    @Test
    void updateSeller_versionMismatch_returnsValidationError() throws Exception {
        testSeller.setVersion(2L);
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));

        CompletableFuture<SellerResult> future = sellerService.updateSeller(1L, updateRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertTrue(error.message().contains("Data is stale")); 
        assertTrue(error.message().contains("Expected version: 1"));
        assertTrue(error.message().contains("but found: 2"));
    }

    @Test
    void updateSeller_optimisticLockingFailure_returnsGenericError() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(any(Seller.class))).thenThrow(new ObjectOptimisticLockingFailureException(Seller.class, 1L));

        CompletableFuture<SellerResult> future = sellerService.updateSeller(1L, updateRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.GenericError.class, result);
        SellerResult.GenericError error = (SellerResult.GenericError) result;
        assertEquals("Concurrent update error. Please try again.", error.message()); 
    }

    @Test
    void updateSeller_generalException_returnsGenericError() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(any(Seller.class))).thenThrow(new RuntimeException("Database error"));

        CompletableFuture<SellerResult> future = sellerService.updateSeller(1L, updateRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.GenericError.class, result);
        SellerResult.GenericError error = (SellerResult.GenericError) result;
        assertTrue(error.message().contains("Error updating seller")); 
        assertTrue(error.message().contains("Database error"));
    }
    
    @Test
    void deleteSellerByIdSoft_success_returnsSuccess() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);
        when(mapper.mapEntityToSellerResponseDto(any(Seller.class))).thenReturn(testResponseDTO);

        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdSoft(1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.Success.class, result);
        SellerResult.Success success = (SellerResult.Success) result;
        assertEquals(testResponseDTO, success.seller()); 
        assertTrue(testSeller.getDeleted());
        verify(sellerRepository).save(testSeller);
    }

    @Test
    void deleteSellerByIdSoft_nullId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdSoft(null);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void deleteSellerByIdSoft_zeroId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdSoft(0L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void deleteSellerByIdSoft_negativeId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdSoft(-1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void deleteSellerByIdSoft_notFound_returnsNotFoundError() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.empty());

        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdSoft(1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.NotFoundError.class, result);
        SellerResult.NotFoundError error = (SellerResult.NotFoundError) result;
        assertEquals("Seller not found with id: 1", error.message()); 
    }

    @Test
    void deleteSellerByIdSoft_exception_returnsGenericError() throws Exception {
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(any(Seller.class))).thenThrow(new RuntimeException("Database error"));

        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdSoft(1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.GenericError.class, result);
        SellerResult.GenericError error = (SellerResult.GenericError) result;
        assertTrue(error.message().contains("Error deleting seller")); 
        assertTrue(error.message().contains("Database error"));
    }
    
    @Test
    void deleteSellerByIdHard_success_returnsSuccess() throws Exception {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));

        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdHard(1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.Success.class, result);
        SellerResult.Success success = (SellerResult.Success) result;
        assertNull(success.seller());
        verify(sellerRepository).delete(testSeller);
    }

    @Test
    void deleteSellerByIdHard_nullId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdHard(null);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void deleteSellerByIdHard_zeroId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdHard(0L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void deleteSellerByIdHard_negativeId_returnsValidationError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdHard(-1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertEquals("Seller ID must be positive", error.message()); 
    }

    @Test
    void deleteSellerByIdHard_notFound_returnsNotFoundError() throws Exception {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdHard(1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.NotFoundError.class, result);
        SellerResult.NotFoundError error = (SellerResult.NotFoundError) result;
        assertEquals("Seller not found with id: 1", error.message()); 
    }

    @Test
    void deleteSellerByIdHard_deleteException_returnsGenericError() throws Exception {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
        doThrow(new RuntimeException("Delete error")).when(sellerRepository).delete(testSeller);

        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdHard(1L);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.GenericError.class, result);
        SellerResult.GenericError error = (SellerResult.GenericError) result;
        assertTrue(error.message().contains("Error performing hard delete")); 
        assertTrue(error.message().contains("Delete error"));
    }

    @Test
    void deleteSellerByIdHard_generalException_returnsGenericError() throws Exception {
        when(sellerRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));
        CompletableFuture<SellerResult> future = sellerService.deleteSellerByIdHard(1L);
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertEquals("Database error", exception.getCause().getMessage());
    }
    
    @Test
    void updateSeller_versionNull_returnsValidationError() throws Exception {
        updateRequestDTO.setVersion(null);
        when(sellerRepository.findNotDeletedById(1L)).thenReturn(Optional.of(testSeller));

        CompletableFuture<SellerResult> future = sellerService.updateSeller(1L, updateRequestDTO);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.ValidationError.class, result);
        SellerResult.ValidationError error = (SellerResult.ValidationError) result;
        assertTrue(error.message().contains("Data is stale")); 
    }

    @Test
    void getAllSellers_emptyList_returnsEmptyList() throws Exception {
        when(sellerRepository.findAllNotDeleted()).thenReturn(List.of());

        CompletableFuture<List<SellerResponseDTO>> future = sellerService.getAllSellers();
        List<SellerResponseDTO> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void createSeller_nullRequest_returnsGenericError() throws Exception {
        CompletableFuture<SellerResult> future = sellerService.createSeller(null);
        SellerResult result = future.get();

        assertInstanceOf(SellerResult.GenericError.class, result);
        SellerResult.GenericError error = (SellerResult.GenericError) result;
        assertTrue(error.message().contains("Error creating seller"));
    }
}