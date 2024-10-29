package com.ledger.demo.infrastructure.controller

import com.ledger.demo.application.ITransactionService
import com.ledger.demo.application.dto.TransactionRequestDto
import com.ledger.demo.infrastructure.controller.dto.BalanceResponse
import com.ledger.demo.infrastructure.controller.dto.CreateTransactionRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Transaction management rest APIs
 */
@Tag(name = "Transaction", description = "Transaction management APIs")
@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController(private val transactionService: ITransactionService) {

    /**
     * Adds a new transaction
     *
     * @param accountId AccountId to add the transaction against
     * @param createTransactionRequest Transaction details
     *
     * @return Success
     */
    @Operation(
        summary = "Add a transaction",
        description = "Add a transaction against a given account"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully added a new transaction",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = String::class),
                    examples = [ExampleObject(value = """
                        {}
                    """)]
                )]),
            ApiResponse(responseCode = "400", description = "Invalid parameter(s) passed"),
            ApiResponse(responseCode = "404", description = "Account not found")
        ]
    )
    @PostMapping("/{accountId}")
    fun addTransaction(
        @PathVariable accountId: Long,
        @RequestBody createTransactionRequest: CreateTransactionRequest
    ): ResponseEntity<String> {

        val transactionRequest = TransactionRequestDto(
            accountId = accountId,
            amount = createTransactionRequest.amount,
            currency = createTransactionRequest.currency,
            type = createTransactionRequest.type,
            direction = createTransactionRequest.direction,
            vendorId = createTransactionRequest.vendorId
        )

        transactionService.addTransaction(transactionRequest)
        return ResponseEntity.ok("{}")
    }
}