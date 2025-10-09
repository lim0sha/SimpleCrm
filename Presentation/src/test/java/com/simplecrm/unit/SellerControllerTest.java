package com.simplecrm.unit;

import com.simplecrm.Controllers.SellerController;
import com.simplecrm.RequestDTO.Seller.SellerCreateRequestDTO;
import com.simplecrm.RequestDTO.Seller.SellerUpdateRequestDTO;
import com.simplecrm.ResultTypes.SellerResult;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.Services.Interfaces.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerControllerTest {

    @InjectMocks
    private SellerController sellerController;

    @Mock
    private SellerService sellerService;

    private SellerResponseDTO sellerDto;
    private SellerCreateRequestDTO createDto;
    private SellerUpdateRequestDTO updateDto;

    @BeforeEach
    void setup() {
        sellerDto = new SellerResponseDTO();
        sellerDto.setId(1L);
        sellerDto.setName("John Doe");
        sellerDto.setContactInfo("john@example.com");
        sellerDto.setRegistrationDate(LocalDateTime.now());
        sellerDto.setVersion(1L);

        createDto = new SellerCreateRequestDTO();
        createDto.setName("John Doe");
        createDto.setContactInfo("john@example.com");

        updateDto = new SellerUpdateRequestDTO();
        updateDto.setName("John Updated");
        updateDto.setContactInfo("john.updated@example.com");
        updateDto.setVersion(1L);
    }

    @Test
    void getAllSellers_shouldReturnList() {

        when(sellerService.getAllSellers()).thenReturn(
                CompletableFuture.completedFuture(List.of(sellerDto))
        );

        ResponseEntity<List<SellerResponseDTO>> response =
                sellerController.getAllSellers().join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1).contains(sellerDto);
    }

    @Test
    void getSellerById_shouldReturnSellerResult() {

        SellerResult.Success result = new SellerResult.Success(sellerDto);
        when(sellerService.getSellerById(1L)).thenReturn(
                CompletableFuture.completedFuture(result)
        );

        ResponseEntity<SellerResult> response = sellerController.getSellerById(1L).join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(SellerResult.Success.class);
    }

    @Test
    void createSeller_shouldReturnCreated() {

        SellerResult.Success result = new SellerResult.Success(sellerDto);
        when(sellerService.createSeller(createDto)).thenReturn(
                CompletableFuture.completedFuture(result)
        );

        ResponseEntity<SellerResult> response = sellerController.createSeller(createDto).join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(SellerResult.Success.class);
    }

    @Test
    void updateSeller_shouldReturnOk() {

        SellerResult.Success result = new SellerResult.Success(sellerDto);
        when(sellerService.updateSeller(1L, updateDto)).thenReturn(
                CompletableFuture.completedFuture(result)
        );

        ResponseEntity<SellerResult> response = sellerController.updateSeller(1L, updateDto).join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(SellerResult.Success.class);
    }

    @Test
    void deleteSeller_soft_shouldReturnNoContent() {
        when(sellerService.deleteSellerByIdSoft(1L))
                .thenReturn(CompletableFuture.completedFuture(new SellerResult.Success(sellerDto)));

        ResponseEntity<Void> response =
                sellerController.deleteSeller(1L, "soft").join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteSeller_invalidType_shouldReturnBadRequest() {
        ResponseEntity<Void> response =
                sellerController.deleteSeller(1L, "unknown").join();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetSellerById_Success() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.Success(sellerDto));
        when(sellerService.getSellerById(1L)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.getSellerById(1L).get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(SellerResult.Success.class, response.getBody());
    }

    @Test
    void testGetSellerById_NotFound() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.NotFoundError("Not found"));
        when(sellerService.getSellerById(1L)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.getSellerById(1L).get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(SellerResult.NotFoundError.class, response.getBody());
    }

    @Test
    void testGetSellerById_ValidationError() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.ValidationError("Invalid ID"));
        when(sellerService.getSellerById(1L)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.getSellerById(1L).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(SellerResult.ValidationError.class, response.getBody());
    }

    @Test
    void testGetSellerById_GenericError() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.GenericError("Generic error"));
        when(sellerService.getSellerById(1L)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.getSellerById(1L).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(SellerResult.GenericError.class, response.getBody());
    }

    @Test
    void testGetSellerById_Exceptionally() throws Exception {
        CompletableFuture<SellerResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Unexpected"));
        when(sellerService.getSellerById(1L)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.getSellerById(1L).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(SellerResult.GenericError.class, response.getBody());
        assertTrue(response.getBody().getMessage().contains("Unexpected"));
    }

    @Test
    void testDeleteSeller_Soft_Success() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.Success(sellerDto));
        when(sellerService.deleteSellerByIdSoft(1L)).thenReturn(future);

        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "soft").get();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteSeller_Hard_Success() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.Success(sellerDto));
        when(sellerService.deleteSellerByIdHard(1L)).thenReturn(future);

        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "hard").get();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteSeller_Soft_NotFound() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.NotFoundError("Not found"));
        when(sellerService.deleteSellerByIdSoft(1L)).thenReturn(future);

        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "soft").get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteSeller_Hard_NotFound() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.NotFoundError("Not found"));
        when(sellerService.deleteSellerByIdHard(1L)).thenReturn(future);

        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "hard").get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteSeller_InvalidDeleteType() throws Exception {
        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "unknown").get();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteSeller_Exceptionally() throws Exception {
        CompletableFuture<SellerResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Boom"));
        when(sellerService.deleteSellerByIdSoft(1L)).thenReturn(future);

        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "soft").get();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetAllSellers_Success() throws Exception {
        List<SellerResponseDTO> sellers = List.of(new SellerResponseDTO(), new SellerResponseDTO());
        CompletableFuture<List<SellerResponseDTO>> future = CompletableFuture.completedFuture(sellers);
        when(sellerService.getAllSellers()).thenReturn(future);

        ResponseEntity<List<SellerResponseDTO>> response = sellerController.getAllSellers().get();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void testGetAllSellers_Exceptionally() throws Exception {
        CompletableFuture<List<SellerResponseDTO>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Unexpected error"));
        when(sellerService.getAllSellers()).thenReturn(future);

        ResponseEntity<List<SellerResponseDTO>> response = sellerController.getAllSellers().get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }


    @Test
    void testCreateSeller_ValidationError() throws Exception {
        SellerCreateRequestDTO dto = new SellerCreateRequestDTO();
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.ValidationError("Invalid data"));
        when(sellerService.createSeller(dto)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.createSeller(dto).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(SellerResult.ValidationError.class, response.getBody());
    }

    @Test
    void testCreateSeller_GenericError() throws Exception {
        SellerCreateRequestDTO dto = new SellerCreateRequestDTO();
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.GenericError("Generic error"));
        when(sellerService.createSeller(dto)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.createSeller(dto).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(SellerResult.GenericError.class, response.getBody());
    }

    @Test
    void testCreateSeller_Exceptionally() throws Exception {
        SellerCreateRequestDTO dto = new SellerCreateRequestDTO();
        CompletableFuture<SellerResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Unexpected"));
        when(sellerService.createSeller(dto)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.createSeller(dto).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(SellerResult.GenericError.class, response.getBody());
        assertTrue(response.getBody().getMessage().contains("Unexpected"));
    }

    @Test
    void testUpdateSeller_NotFoundError() throws Exception {
        SellerUpdateRequestDTO dto = new SellerUpdateRequestDTO();
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.NotFoundError("Not found"));
        when(sellerService.updateSeller(1L, dto)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.updateSeller(1L, dto).get();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertInstanceOf(SellerResult.NotFoundError.class, response.getBody());
    }

    @Test
    void testUpdateSeller_ValidationError() throws Exception {
        SellerUpdateRequestDTO dto = new SellerUpdateRequestDTO();
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.ValidationError("Invalid data"));
        when(sellerService.updateSeller(1L, dto)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.updateSeller(1L, dto).get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(SellerResult.ValidationError.class, response.getBody());
    }

    @Test
    void testUpdateSeller_GenericError() throws Exception {
        SellerUpdateRequestDTO dto = new SellerUpdateRequestDTO();
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.GenericError("Generic error"));
        when(sellerService.updateSeller(1L, dto)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.updateSeller(1L, dto).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(SellerResult.GenericError.class, response.getBody());
    }

    @Test
    void testUpdateSeller_Exceptionally() throws Exception {
        SellerUpdateRequestDTO dto = new SellerUpdateRequestDTO();
        CompletableFuture<SellerResult> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Unexpected"));
        when(sellerService.updateSeller(1L, dto)).thenReturn(future);

        ResponseEntity<SellerResult> response = sellerController.updateSeller(1L, dto).get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertInstanceOf(SellerResult.GenericError.class, response.getBody());
        assertTrue(response.getBody().getMessage().contains("Unexpected"));
    }

    @Test
    void testDeleteSeller_ValidationErrorSoft() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.ValidationError("Invalid"));
        when(sellerService.deleteSellerByIdSoft(1L)).thenReturn(future);

        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "soft").get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteSeller_GenericErrorSoft() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.GenericError("Generic"));
        when(sellerService.deleteSellerByIdSoft(1L)).thenReturn(future);

        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "soft").get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testDeleteSeller_ValidationErrorHard() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.ValidationError("Invalid"));
        when(sellerService.deleteSellerByIdHard(1L)).thenReturn(future);

        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "hard").get();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testDeleteSeller_GenericErrorHard() throws Exception {
        CompletableFuture<SellerResult> future = CompletableFuture.completedFuture(new SellerResult.GenericError("Generic"));
        when(sellerService.deleteSellerByIdHard(1L)).thenReturn(future);

        ResponseEntity<Void> response = sellerController.deleteSeller(1L, "hard").get();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
