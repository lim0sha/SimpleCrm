package com.simplecrm.Controllers;

import com.simplecrm.Projections.TransactionFlatView;
import com.simplecrm.RequestDTO.Transaction.TransactionCreateRequestDTO;
import com.simplecrm.RequestDTO.Transaction.TransactionUpdateRequestDTO;
import com.simplecrm.ResponseDTO.TransactionResponseDTO;
import com.simplecrm.ResultTypes.TransactionResult;
import com.simplecrm.Services.Interfaces.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Получить список всех транзакций",
            description = "Возвращает полный список всех транзакций")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping
    public CompletableFuture<ResponseEntity<List<TransactionResponseDTO>>> getAllTransactions() {
        return transactionService.getAllTransactions()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    @Operation(
            summary = "Получить транзакцию по ID",
            description = "Возвращает информацию о конкретной транзакции по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Транзакция найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResult.class))),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<TransactionResult>> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .thenApply(result -> {
                    return switch (result) {
                        case TransactionResult.NotFoundError e ->
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                        case TransactionResult.ValidationError e -> ResponseEntity.badRequest().body(result);
                        case TransactionResult.GenericError e -> ResponseEntity.internalServerError().body(result);
                        default -> ResponseEntity.ok(result);
                    };
                })
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(new TransactionResult.GenericError("Error: " + ex.getMessage())));
    }

    @Operation(
            summary = "Создать новую транзакцию",
            description = "Добавляет новую транзакцию для продавца")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Транзакция успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResult.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Продавец не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping
    public CompletableFuture<ResponseEntity<TransactionResult>> createTransaction(@RequestBody TransactionCreateRequestDTO dto) {
        return transactionService.createTransaction(dto)
                .thenApply(result -> {
                    return switch (result) {
                        case TransactionResult.ValidationError e -> ResponseEntity.badRequest().body(result);
                        case TransactionResult.SellerNotFoundError e ->
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                        case TransactionResult.GenericError e -> ResponseEntity.internalServerError().body(result);
                        default -> ResponseEntity.status(HttpStatus.CREATED).body(result);
                    };
                })
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(new TransactionResult.GenericError("Error: " + ex.getMessage())));
    }

    @Operation(
            summary = "Обновить транзакцию",
            description = "Обновляет информацию о транзакции по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Транзакция успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<TransactionResult>> updateTransaction(@PathVariable Long id,
                                                                                  @RequestBody TransactionUpdateRequestDTO dto) {
        return transactionService.updateTransactionById(id, dto)
                .thenApply(result -> {
                    return switch (result) {
                        case TransactionResult.NotFoundError e ->
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                        case TransactionResult.ValidationError e -> ResponseEntity.badRequest().body(result);
                        case TransactionResult.GenericError e -> ResponseEntity.internalServerError().body(result);
                        default -> ResponseEntity.ok(result);
                    };
                })
                .exceptionally(ex -> ResponseEntity.internalServerError()
                        .body(new TransactionResult.GenericError("Error: " + ex.getMessage())));
    }

    @Operation(
            summary = "Удалить транзакцию",
            description = "Удаляет транзакцию по её ID. Обязательный параметр deleteType определяет тип удаления: soft или hard"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Транзакция успешно удалена"),
            @ApiResponse(responseCode = "400", description = "Некорректный ID или deleteType"),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteTransaction(
            @PathVariable Long id,
            @RequestParam(name = "deleteType") String deleteType
    ) {
        CompletableFuture<TransactionResult> resultFuture;

        if ("soft".equalsIgnoreCase(deleteType)) {
            resultFuture = transactionService.deleteTransactionByIdSoft(id);
        } else if ("hard".equalsIgnoreCase(deleteType)) {
            resultFuture = transactionService.deleteTransactionByIdHard(id);
        } else {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
        }

        return resultFuture
                .thenApply(result -> switch (result) {
                    case TransactionResult.NotFoundError e -> ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build();
                    case TransactionResult.ValidationError e -> ResponseEntity.badRequest().<Void>build();
                    case TransactionResult.GenericError e -> ResponseEntity.internalServerError().<Void>build();
                    default -> ResponseEntity.noContent().<Void>build();
                })
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }

    @Operation(
            summary = "Получить все транзакции продавца",
            description = "Возвращает список всех транзакций конкретного продавца")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Транзакции найдены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Продавец не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/seller/{sellerId}")
    public CompletableFuture<ResponseEntity<List<TransactionFlatView>>> getTransactionsBySellerId(
            @PathVariable Long sellerId) {
        return transactionService.getTransactionsBySellerId(sellerId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.internalServerError().build());
    }
}