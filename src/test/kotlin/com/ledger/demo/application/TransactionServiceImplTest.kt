package com.ledger.demo.application

import com.ledger.demo.application.dto.Direction
import com.ledger.demo.application.dto.TransactionRequestDto
import com.ledger.demo.domain.AccountEntity
import com.ledger.demo.domain.TransactionEntity
import com.ledger.demo.infrastructure.persist.IAccountRepository
import com.ledger.demo.infrastructure.persist.ITransactionRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.test.assertEquals

class TransactionServiceImplTest {

    private lateinit var accountRepository: IAccountRepository
    private lateinit var transactionRepository: ITransactionRepository
    private lateinit var clock: Clock
    private lateinit var transactionService: TransactionServiceImpl

    @BeforeEach
    fun setUp() {
        accountRepository = mock(IAccountRepository::class.java)
        transactionRepository = mock(ITransactionRepository::class.java)
        clock = Clock.fixed(OffsetDateTime.now().toInstant(), ZoneOffset.UTC)
        transactionService = TransactionServiceImpl(accountRepository, transactionRepository, clock)
    }

    @Test
    fun `should add IN transaction and update balance`() {
        // Given
        val initialBalance = BigDecimal(1000)
        val account = AccountEntity(
            id = 1,
            fullName = "Sam Wise",
            email = "sam@wise.com",
            createdAt = OffsetDateTime.now(clock),
            balance = initialBalance
        )
        val transactionRequest = TransactionRequestDto(
            accountId = 1,
            amount = BigDecimal(200),
            currency = "en-GB",
            type = "one_off",
            direction = Direction.IN,
            vendorId = "barman-corp"
        )
        `when`(accountRepository.findById(transactionRequest.accountId)).thenReturn(Optional.of(account))

        // When
        transactionService.addTransaction(transactionRequest)

        // Then
        val expectedBalance = initialBalance.add(transactionRequest.amount)
        assertEquals(expectedBalance, account.balance)

        val transactionCaptor = ArgumentCaptor.forClass(TransactionEntity::class.java)
        verify(transactionRepository, times(1)).save(transactionCaptor.capture())
        val savedTransaction = transactionCaptor.value

        assertEquals(account.id, savedTransaction.accountId)
        assertEquals(transactionRequest.amount, savedTransaction.amount)
        assertEquals(transactionRequest.direction, savedTransaction.direction)
        assertEquals(transactionRequest.vendorId, savedTransaction.vendorId)
        assertEquals(OffsetDateTime.now(clock), savedTransaction.createdAt)
    }

    @Test
    fun `should add OUT transaction and update balance`() {
        // Given
        val initialBalance = BigDecimal(1000)
        val account = AccountEntity(
            id = 1,
            fullName = "Sam Wise",
            email = "sam@wise.com",
            createdAt = OffsetDateTime.now(clock),
            balance = initialBalance
        )
        val transactionRequest = TransactionRequestDto(
            accountId = 1,
            amount = BigDecimal(300),
            currency = "en-GB",
            type = "one_off",
            direction = Direction.OUT,
            vendorId = "barman-corp"
        )

        `when`(accountRepository.findById(transactionRequest.accountId)).thenReturn(Optional.of(account))

        // When
        transactionService.addTransaction(transactionRequest)

        // Then
        val expectedBalance = initialBalance.subtract(transactionRequest.amount)
        assertEquals(expectedBalance, account.balance)

        val transactionCaptor = ArgumentCaptor.forClass(TransactionEntity::class.java)
        verify(transactionRepository, times(1)).save(transactionCaptor.capture())
        val savedTransaction = transactionCaptor.value

        assertEquals(account.id, savedTransaction.accountId)
        assertEquals(transactionRequest.amount, savedTransaction.amount)
        assertEquals(transactionRequest.direction, savedTransaction.direction)
        assertEquals(transactionRequest.vendorId, savedTransaction.vendorId)
        assertEquals(OffsetDateTime.now(clock), savedTransaction.createdAt)
    }

    @Test
    fun `should throw IllegalArgumentException when account not found`() {
        // Given
        val transactionRequest = TransactionRequestDto(
            accountId = 1,
            amount = BigDecimal(100),
            direction = Direction.IN,
            currency = "en-GB",
            type = "one_off",
            vendorId = "barman-corp"
        )

        `when`(accountRepository.findById(transactionRequest.accountId)).thenReturn(Optional.empty())

        // When / Then
        val exception = assertThrows<IllegalArgumentException> {
            transactionService.addTransaction(transactionRequest)
        }
        assertEquals("Account not found, accountId ${transactionRequest.accountId}", exception.message)
    }

    @Test
    fun `should throw IllegalArgumentException when insufficient funds for OUT transaction`() {
        // Given
        val initialBalance = BigDecimal(100)
        val account = AccountEntity(
            id = 1,
            fullName = "Sam Wise",
            email = "sam@wise.com",
            createdAt = OffsetDateTime.now(clock),
            balance = initialBalance
        )
        val transactionRequest = TransactionRequestDto(
            accountId = 1,
            amount = BigDecimal(200), // Exceeds the current balance
            direction = Direction.OUT,
            currency = "en-GB",
            type = "one_off",
            vendorId = "barman-corp"
        )

        `when`(accountRepository.findById(transactionRequest.accountId)).thenReturn(Optional.of(account))

        // When / Then
        val exception = assertThrows<IllegalArgumentException> {
            transactionService.addTransaction(transactionRequest)
        }
        assertEquals("Insufficient funds for available, accountId ${transactionRequest.accountId}", exception.message)
    }
}