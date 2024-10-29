package com.ledger.demo.infrastructure.controller

import com.ledger.demo.application.IAccountService
import com.ledger.demo.application.ICalculateBalanceService
import com.ledger.demo.application.dto.AccountRequestDto
import com.ledger.demo.infrastructure.controller.dto.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.time.OffsetDateTime
import kotlin.test.assertEquals

class AccountControllerTest {

    private lateinit var calculateBalanceService: ICalculateBalanceService
    private lateinit var accountService: IAccountService
    private lateinit var accountController: AccountController

    @BeforeEach
    fun setUp() {
        calculateBalanceService = mock<ICalculateBalanceService>()
        accountService = mock<IAccountService>()
        accountController = AccountController(calculateBalanceService, accountService)
    }

    @Test
    fun `should return balance response for a valid account`() {
        // Given
        val accountId = 1L
        val fromTransactionDateTime = OffsetDateTime.now().minusDays(10)
        val toTransactionDateTime = OffsetDateTime.now()
        val expectedBalance = BigDecimal(1500)
        val expectedResult = CalculateBalanceResult(accountId, expectedBalance)
        val expectedBalanceResult = BalanceResponse(accountId, expectedBalance)
        whenever(calculateBalanceService.calculateBalance(any())).thenReturn(expectedResult)

        // When
        val response: ResponseEntity<BalanceResponse> = accountController.getBalance(
            accountId = accountId,
            fromTransactionDateTime = fromTransactionDateTime,
            toTransactionDateTime = toTransactionDateTime
        )

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedBalanceResult, response.body)
        verify(calculateBalanceService, times(1)).calculateBalance(any())
    }

    @Test
    fun `should add account and return create account response`() {
        // Given
        val createAccountRequest = CreateAccountRequest(
            fullName = "John Doe",
            email = "john.doe@example.com",
            amount = BigDecimal(1000),
            currency = "USD"
        )
        val expectedAccountId = 1L
        val expectedResponse = CreateAccountResponse(accountId = expectedAccountId)

        whenever(accountService.addAccount(any())).thenReturn(expectedAccountId)

        // When
        val response: ResponseEntity<CreateAccountResponse> = accountController.addAccount(createAccountRequest)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedResponse, response.body)
        verify(accountService, times(1)).addAccount(any())
    }
}