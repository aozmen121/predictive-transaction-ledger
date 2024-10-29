package com.ledger.demo.infrastructure.controller

import com.ledger.demo.application.IAccountService
import com.ledger.demo.application.ICalculateBalanceService
import com.ledger.demo.application.dto.AccountRequestDto
import com.ledger.demo.infrastructure.controller.dto.BalanceResponse
import com.ledger.demo.infrastructure.controller.dto.CalculateBalanceRequest
import com.ledger.demo.infrastructure.controller.dto.CreateAccountRequest
import com.ledger.demo.infrastructure.controller.dto.CreateAccountResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime

/**
 * Account management rest APIs
 */
@Tag(name = "Account", description = "Account management APIs")
@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(
    private val predictionService: ICalculateBalanceService,
    private val accountService: IAccountService
) {

    /**
     * Calculates and predicts amount based on transactional history.
     *
     * @param accountId Acount id to calculate balance amount
     * @param fromTransactionDateTime From timestamp e.g. 2024-08-01T12:40:00.000Z
     * @param toTransactionDateTime From timestamp e.g. 2024-09-01T12:40:00.000Z
     *
     * return BalanceResponse calculated amount result
     */
    @Operation(
        summary = "Get account balance",
        description = "Retrieves the predicted balance for a specific account based on historical transactions and from and to timestamp ranges"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved account balance",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = BalanceResponse::class),
                    examples = [ExampleObject(value = """
                        {
                          "accountId": 1,
                          "balance": 100
                        }
                    """)]
                )]),
            ApiResponse(responseCode = "400", description = "Invalid parameter(s) passed"),
            ApiResponse(responseCode = "404", description = "Account not found")
        ]
    )
    @GetMapping("/{accountId}/balance")
    fun getBalance(
        @PathVariable accountId: Long,
        @RequestParam fromTransactionDateTime: OffsetDateTime,
        @RequestParam toTransactionDateTime: OffsetDateTime
    ): ResponseEntity<BalanceResponse> {
        val command = CalculateBalanceRequest(
            accountId = accountId,
            fromDateTime = fromTransactionDateTime,
            toDateTime = toTransactionDateTime
        )

        val result = predictionService.calculateBalance(command)
        val response = BalanceResponse(
            accountId = result.accountId,
            balance = result.predictedBalance,
        )
        return ResponseEntity.ok(response)
    }

    /**
     * Creates a new account
     *
     * @param createAccountRequest contains details about the account being created
     *
     * @return CreateAccountResponse contains the account id thats successfully created
     */
    @Operation(
        summary = "Create an account",
        description = "Creates an account and returns the auto-generated accountId"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully generated an account",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CreateAccountResponse::class),
                    examples = [ExampleObject(value = """
                        {
                          "accountId": 1
                        }
                    """)]
                )]),
            ApiResponse(responseCode = "400", description = "Invalid parameter(s) passed"),
        ]
    )
    @PostMapping
    fun addAccount(
        @RequestBody createAccountRequest: CreateAccountRequest
    ): ResponseEntity<CreateAccountResponse> {
        val accountRequest = AccountRequestDto(
            fullName = createAccountRequest.fullName,
            email = createAccountRequest.email,
            amount = createAccountRequest.amount,
            currency = createAccountRequest.currency
        )

        val result = CreateAccountResponse(accountService.addAccount(accountRequest))
        return ResponseEntity.ok(result)
    }
}