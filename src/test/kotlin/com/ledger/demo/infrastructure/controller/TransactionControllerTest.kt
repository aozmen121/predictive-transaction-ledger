package com.ledger.demo.infrastructure.controller

import com.ledger.demo.application.ITransactionService
import com.ledger.demo.application.dto.Direction
import com.ledger.demo.application.dto.TransactionRequestDto
import com.ledger.demo.infrastructure.controller.dto.CreateTransactionRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal

class TransactionControllerTest {

    private lateinit var transactionService: ITransactionService
    private lateinit var transactionController: TransactionController

    @BeforeEach
    fun setUp() {
        transactionService = mock(ITransactionService::class.java)
        transactionController = TransactionController(transactionService)
    }

    @Test
    fun `should add transaction and return OK response`() {
        // Given
        val accountId = 1L
        val createTransactionRequest = CreateTransactionRequest(
            amount = BigDecimal(500),
            currency = "en-GB",
            type = "one_off",
            direction = Direction.OUT,
            vendorId = "batman-corp"
        )

        val transactionRequestDto = TransactionRequestDto(
            accountId = accountId,
            amount = createTransactionRequest.amount,
            currency = createTransactionRequest.currency,
            type = createTransactionRequest.type,
            direction = createTransactionRequest.direction,
            vendorId = createTransactionRequest.vendorId
        )

        // When
        val response: ResponseEntity<String> =
            transactionController.addTransaction(accountId, createTransactionRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(transactionService, times(1)).addTransaction(transactionRequestDto)
    }
}