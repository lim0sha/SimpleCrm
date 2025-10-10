package com.simplecrm.Controllers;

import com.simplecrm.RequestDTO.Seller.SellerCreateRequestDTO;
import com.simplecrm.RequestDTO.Seller.SellerUpdateRequestDTO;
import com.simplecrm.ResponseDTO.SellerResponseDTO;
import com.simplecrm.ResultTypes.SellerResult;
import com.simplecrm.Services.Interfaces.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @Operation(
            summary = "Получить список всех продавцов",
            description = "Возвращает полный список всех продавцов")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SellerResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping
    public CompletableFuture<ResponseEntity<List<SellerResponseDTO>>> getAllSellers() {
        return sellerService.getAllSellers()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    @Operation(
            summary = "Получить продавца по ID",
            description = "Возвращает информацию о продавце по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Продавец найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SellerResult.class))),
            @ApiResponse(responseCode = "404", description = "Продавец не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<SellerResult>> getSellerById(@PathVariable Long id) {
        return sellerService.getSellerById(id)
                .thenApply(result -> (ResponseEntity<SellerResult>) switch (result) {
                    case SellerResult.NotFoundError e -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
                    case SellerResult.ValidationError e -> ResponseEntity.badRequest().body(e);
                    case SellerResult.GenericError e -> ResponseEntity.internalServerError().body(e);
                    default -> ResponseEntity.ok(result);
                })
                .exceptionally(ex ->
                        ResponseEntity.internalServerError()
                                .body(new SellerResult.GenericError("Error: " + ex.getMessage())));
    }

    @Operation(
            summary = "Создать нового продавца",
            description = "Добавляет нового продавца в систему")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Продавец успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SellerResult.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping
    public CompletableFuture<ResponseEntity<SellerResult>> createSeller(@RequestBody @Valid SellerCreateRequestDTO dto) {
        return sellerService.createSeller(dto)
                .thenApply(result -> (ResponseEntity<SellerResult>) switch (result) {
                    case SellerResult.ValidationError e -> ResponseEntity.badRequest().body(e);
                    case SellerResult.GenericError e -> ResponseEntity.internalServerError().body(e);
                    default -> ResponseEntity.status(HttpStatus.CREATED).body(result);
                })
                .exceptionally(ex ->
                        ResponseEntity.internalServerError()
                                .body(new SellerResult.GenericError("Error: " + ex.getMessage())));
    }

    @Operation(
            summary = "Обновить продавца",
            description = "Обновляет данные продавца по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Продавец обновлён"),
            @ApiResponse(responseCode = "404", description = "Продавец не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<SellerResult>> updateSeller(
            @PathVariable Long id,
            @RequestBody @Valid SellerUpdateRequestDTO dto) {

        return sellerService.updateSeller(id, dto)
                .thenApply(result -> (ResponseEntity<SellerResult>) switch (result) {
                    case SellerResult.NotFoundError e -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
                    case SellerResult.ValidationError e -> ResponseEntity.badRequest().body(e);
                    case SellerResult.GenericError e -> ResponseEntity.internalServerError().body(e);
                    default -> ResponseEntity.ok(result);
                })
                .exceptionally(ex ->
                        ResponseEntity.internalServerError()
                                .body(new SellerResult.GenericError("Error: " + ex.getMessage())));
    }

    @Operation(
            summary = "Удалить продавца",
            description = "Удаляет продавца по его ID. Обязательный параметр deleteType определяет тип удаления: soft или hard")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Продавец успешно удалён"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID или deleteType"),
            @ApiResponse(responseCode = "404", description = "Продавец не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteSeller(
            @PathVariable Long id,
            @RequestParam(name = "deleteType", required = false) String deleteType) {

        if (deleteType == null || (!deleteType.equalsIgnoreCase("soft") && !deleteType.equalsIgnoreCase("hard"))) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }

        CompletableFuture<SellerResult> resultFuture = "soft".equalsIgnoreCase(deleteType)
                ? sellerService.deleteSellerByIdSoft(id)
                : sellerService.deleteSellerByIdHard(id);

        return resultFuture.thenApply(result -> switch (result) {
                    case SellerResult.NotFoundError e -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build();
                    case SellerResult.ValidationError e -> ResponseEntity.badRequest().<Void>build();
                    case SellerResult.GenericError e -> ResponseEntity.internalServerError().<Void>build();
                    default -> ResponseEntity.noContent().<Void>build();
                })
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }
}