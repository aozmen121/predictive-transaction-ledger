package com.ledger.demo.application

import com.ledger.demo.application.dto.Direction
import com.ledger.demo.application.exception.InvalidTimestampRangeException
import com.ledger.demo.application.predictor.IPredictBalanceService
import com.ledger.demo.domain.AccountEntity
import com.ledger.demo.domain.TransactionEntity
import com.ledger.demo.infrastructure.persist.IAccountRepository
import com.ledger.demo.infrastructure.persist.ITransactionRepository
import com.ledger.demo.infrastructure.controller.dto.CalculateBalanceRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class CalculateBalanceServiceImplTest {

    private lateinit var predictBalanceService: IPredictBalanceService
    private lateinit var accountRepository: IAccountRepository
    private lateinit var transactionRepository: ITransactionRepository
    private lateinit var clock: Clock
    private lateinit var calculateBalanceService: CalculateBalanceServiceImpl

    @BeforeEach
    fun setUp() {
        predictBalanceService = mock(IPredictBalanceService::class.java)
        accountRepository = mock(IAccountRepository::class.java)
        transactionRepository = mock(ITransactionRepository::class.java)
        val fixedInstant = OffsetDateTime.now().toInstant()
        clock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
        calculateBalanceService =
            CalculateBalanceServiceImpl(predictBalanceService, accountRepository, transactionRepository, clock)
    }

    @Test
    fun `should throw InvalidTimestampRangeException when fromDateTime is after toDateTime`() {
        val request = CalculateBalanceRequest(
            accountId = 1,
            fromDateTime = OffsetDateTime.now(clock).plusDays(1),
            toDateTime = OffsetDateTime.now(clock)
        )

        val exception = assertThrows<InvalidTimestampRangeException> {
            calculateBalanceService.calculateBalance(request)
        }
        assertEquals(
            "Given from date is after to date. From: ${request.fromDateTime}, To: ${request.toDateTime}",
            exception.message
        )
    }

    @Test
    fun `should throw IllegalArgumentException when account is not found`() {
        val request = CalculateBalanceRequest(
            accountId = 1,
            fromDateTime = OffsetDateTime.now(clock).minusDays(1),
            toDateTime = OffsetDateTime.now(clock)
        )

        `when`(accountRepository.findById(request.accountId)).thenReturn(Optional.empty())

        val exception = assertThrows<IllegalArgumentException> {
            calculateBalanceService.calculateBalance(request)
        }
        assertEquals("Account not found, accountId ${request.accountId}", exception.message)
    }

    @Test
    fun `should return current balance when there are no recent transactions`() {
        val accountId = 1L
        val request = CalculateBalanceRequest(
            accountId = accountId,
            fromDateTime = OffsetDateTime.now(clock).minusDays(1),
            toDateTime = OffsetDateTime.now(clock)
        )

        val accountBalance = BigDecimal(1000)
        val account = mock(AccountEntity::class.java).apply {
            `when`(this.balance).thenReturn(accountBalance)
        }

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(account))
        `when`(
            transactionRepository.findByAccountIdAndCreatedAtBetween(
                accountId,
                request.fromDateTime,
                request.toDateTime
            )
        )
            .thenReturn(emptyList())

        val result = calculateBalanceService.calculateBalance(request)

        assertEquals(accountId, result.accountId)
        assertEquals(accountBalance, result.predictedBalance)
    }

    @Test
    fun `should calculate past balance when transactions are present and date is in the past`() {
        val accountId = 1L
        val request = CalculateBalanceRequest(
            accountId = accountId,
            fromDateTime = OffsetDateTime.now(clock).minusDays(10),
            toDateTime = OffsetDateTime.now(clock)
        )

        val accountBalance = BigDecimal(1000)
        val account = mock(AccountEntity::class.java).apply {
            `when`(this.balance).thenReturn(accountBalance)
        }

        val transactions = listOf(
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(5),
                direction = Direction.IN,
                amount = BigDecimal(300),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 1,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(2),
                direction = Direction.OUT,
                amount = BigDecimal(100),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 2,
                accountId = 1
            ),
        )

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(account))
        `when`(
            transactionRepository.findByAccountIdAndCreatedAtBetween(
                accountId,
                request.fromDateTime,
                request.toDateTime
            )
        )
            .thenReturn(transactions)

        val result = calculateBalanceService.calculateBalance(request)

        assertEquals(accountId, result.accountId)
        assertEquals(BigDecimal(200), result.predictedBalance)
    }

    @Test
    fun `should predict future balance when date is in the future`() {
        val accountId = 1L
        val request = CalculateBalanceRequest(
            accountId = accountId,
            fromDateTime = OffsetDateTime.now(clock).minusDays(10),
            toDateTime = OffsetDateTime.now(clock).plusDays(5) // Future date
        )

        val accountBalance = BigDecimal(1000)
        val account = mock(AccountEntity::class.java).apply {
            `when`(this.balance).thenReturn(accountBalance)
        }

        val transactions = listOf(
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(5),
                direction = Direction.IN,
                amount = BigDecimal(300),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 1,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(2),
                direction = Direction.IN,
                amount = BigDecimal(100),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 2,
                accountId = 1
            ),
        )

        `when`(accountRepository.findById(accountId)).thenReturn(Optional.of(account))
        `when`(
            transactionRepository.findByAccountIdAndCreatedAtBetween(
                accountId,
                request.fromDateTime,
                request.toDateTime
            )
        )
            .thenReturn(transactions)

        // Mock prediction
        `when`(predictBalanceService.predictAmount(request.toDateTime, transactions)).thenReturn(BigDecimal(1200))

        val result = calculateBalanceService.calculateBalance(request)

        assertEquals(accountId, result.accountId)
        assertEquals(BigDecimal(1200), result.predictedBalance)
    }
}