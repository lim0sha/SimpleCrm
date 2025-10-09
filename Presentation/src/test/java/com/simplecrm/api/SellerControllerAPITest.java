package com.simplecrm.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecrm.Controllers.SellerController;
import com.simplecrm.RequestDTO.Seller.SellerCreateRequestDTO;
import com.simplecrm.RequestDTO.Seller.SellerUpdateRequestDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResultTypes.SellerResult;
import com.simplecrm.Services.Interfaces.SellerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SellerController.class)
@Import({SellerControllerAPITest.TestConfig.class, SellerController.class})
class SellerControllerAPITest {

    @Configuration
    static class TestConfig {
        @Bean
        public SellerService sellerService() {
            return org.mockito.Mockito.mock(SellerService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerService sellerService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getAllSellers_success() throws Exception {
        SellerResponseDTO s1 = new SellerResponseDTO();
        s1.setId(1L);
        s1.setName("S1");
        s1.setContactInfo("c1");
        s1.setRegistrationDate(LocalDateTime.now());
        s1.setVersion(1L);

        SellerResponseDTO s2 = new SellerResponseDTO();
        s2.setId(2L);
        s2.setName("S2");
        s2.setContactInfo("c2");
        s2.setRegistrationDate(LocalDateTime.now());
        s2.setVersion(1L);

        when(sellerService.getAllSellers())
                .thenReturn(CompletableFuture.supplyAsync(() -> List.of(s1, s2)));

        MvcResult mvcResult = mockMvc.perform(get("/api/sellers"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("S1"))
                .andExpect(jsonPath("$[1].name").value("S2"));
    }

    @Test
    void getAllSellers_error() throws Exception {
        when(sellerService.getAllSellers())
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(get("/api/sellers"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void getSellerById_success() throws Exception {
        SellerResponseDTO dto = new SellerResponseDTO();
        dto.setId(1L);
        dto.setName("John");
        dto.setContactInfo("john@mail.com");
        SellerResult.Success res = new SellerResult.Success(dto);

        when(sellerService.getSellerById(1L))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        MvcResult mvcResult = mockMvc.perform(get("/api/sellers/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller.name").value("John"));
    }

    @Test
    void getSellerById_notFound() throws Exception {
        SellerResult.NotFoundError res = new SellerResult.NotFoundError("Seller not found");
        when(sellerService.getSellerById(1L))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        MvcResult mvcResult = mockMvc.perform(get("/api/sellers/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Seller not found"));
    }

    @Test
    void getSellerById_validationError() throws Exception {
        SellerResult.ValidationError res = new SellerResult.ValidationError("Invalid ID");
        when(sellerService.getSellerById(-1L))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        MvcResult mvcResult = mockMvc.perform(get("/api/sellers/-1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid ID"));
    }

    @Test
    void getSellerById_genericError() throws Exception {
        SellerResult.GenericError res = new SellerResult.GenericError("Server error");
        when(sellerService.getSellerById(1L))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        MvcResult mvcResult = mockMvc.perform(get("/api/sellers/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Server error"));
    }

    @Test
    void getSellerById_exception() throws Exception {
        when(sellerService.getSellerById(1L))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(get("/api/sellers/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error: java.lang.RuntimeException: DB error"));
    }


    @Test
    void createSeller_success() throws Exception {
        SellerCreateRequestDTO req = new SellerCreateRequestDTO();
        req.setName("New");
        req.setContactInfo("mail@ex.com");

        SellerResponseDTO dto = new SellerResponseDTO();
        dto.setId(1L);
        dto.setName("New");
        dto.setContactInfo("mail@ex.com");
        SellerResult.Success res = new SellerResult.Success(dto);

        when(sellerService.createSeller(any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        MvcResult mvcResult = mockMvc.perform(post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seller.name").value("New"));
    }

    @Test
    void createSeller_validationError() throws Exception {
        SellerCreateRequestDTO req = new SellerCreateRequestDTO();
        req.setName("");
        req.setContactInfo("");

        SellerResult.ValidationError res = new SellerResult.ValidationError("Invalid data");
        when(sellerService.createSeller(any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        mockMvc.perform(post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void createSeller_exception() throws Exception {
        SellerCreateRequestDTO req = new SellerCreateRequestDTO();
        req.setName("X");
        req.setContactInfo("mail");

        when(sellerService.createSeller(any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult result = mockMvc.perform(post("/api/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error: java.lang.RuntimeException: DB error"));
    }


    @Test
    void updateSeller_success() throws Exception {
        SellerUpdateRequestDTO req = new SellerUpdateRequestDTO();
        req.setName("Upd");
        req.setContactInfo("upd@mail.com");
        req.setVersion(1L);

        SellerResponseDTO dto = new SellerResponseDTO();
        dto.setId(1L);
        dto.setName("Upd");
        dto.setContactInfo("upd@mail.com");
        SellerResult.Success res = new SellerResult.Success(dto);

        when(sellerService.updateSeller(anyLong(), any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        MvcResult mvcResult = mockMvc.perform(put("/api/sellers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller.name").value("Upd"));
    }

    @Test
    void updateSeller_notFound() throws Exception {
        SellerUpdateRequestDTO req = new SellerUpdateRequestDTO();
        req.setName("Upd");
        req.setContactInfo("upd@mail.com");
        req.setVersion(1L);

        SellerResult.NotFoundError res = new SellerResult.NotFoundError("Not found");
        when(sellerService.updateSeller(anyLong(), any()))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        MvcResult mvcResult = mockMvc.perform(put("/api/sellers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Not found"));
    }

    @Test
    void updateSeller_exception() throws Exception {
        SellerUpdateRequestDTO req = new SellerUpdateRequestDTO();
        req.setName("Upd");
        req.setContactInfo("upd@mail.com");
        req.setVersion(1L);

        when(sellerService.updateSeller(anyLong(), any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult result = mockMvc.perform(put("/api/sellers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error: java.lang.RuntimeException: DB error"));
    }

    @Test
    void deleteSeller_soft_success() throws Exception {
        SellerResult.Success res = new SellerResult.Success(null);
        when(sellerService.deleteSellerByIdSoft(1L))
                .thenReturn(CompletableFuture.supplyAsync(() -> res));

        MvcResult mvcResult = mockMvc.perform(delete("/api/sellers/1").param("deleteType", "soft"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSeller_invalidType() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/sellers/1")
                        .param("deleteType", "invalid"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSeller_exception() throws Exception {
        when(sellerService.deleteSellerByIdSoft(1L))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB error")));

        MvcResult mvcResult = mockMvc.perform(delete("/api/sellers/1").param("deleteType", "soft"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isInternalServerError());
    }
}
