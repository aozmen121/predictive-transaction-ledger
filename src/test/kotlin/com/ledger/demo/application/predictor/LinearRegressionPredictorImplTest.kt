package com.ledger.demo.application.predictor

import com.ledger.demo.application.dto.Direction
import com.ledger.demo.domain.TransactionEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset

class LinearRegressionPredictorImplTest {
    private lateinit var predictor: LinearRegressionPredictorImpl

    @Mock
    private lateinit var clock: Clock

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Use a fixed point in time for testing
        val fixedInstant = OffsetDateTime.now().toInstant()
        clock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
        predictor = LinearRegressionPredictorImpl(clock)
    }

    @Test
    fun `should predict amount correctly for increasing transactions`() {
        // Given: a list of increasing transactions
        val transactions = listOf(
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(4),
                direction = Direction.IN,
                amount = BigDecimal(100),
                type = "one_off",
                currency = "en-GB",
                vendorId = "BatmanCorp-id",
                id = 1,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(3),
                direction = Direction.IN,
                amount = BigDecimal(200),
                type = "one_off",
                currency = "en-GB",
                vendorId = "BatmanCorp-id",
                id = 2,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(2),
                direction = Direction.OUT,
                amount = BigDecimal(50),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 3,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(1),
                direction = Direction.OUT,
                amount = BigDecimal(30),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 4,
                accountId = 1
            )
        )

        // When: predicting the amount for a future date
        val futureDateTime = OffsetDateTime.now(clock).plusDays(5)
        val predictedAmount = predictor.predictAmount(futureDateTime, transactions)

        // Then: assert that the predicted amount is as expected
        assertEquals(BigDecimal(419), predictedAmount.setScale(0, RoundingMode.HALF_UP))
    }

    @Test
    fun `should predict amount correctly with only OUT transactions`() {
        // Given: a list of transactions with only OUT direction
        val transactions = listOf(
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(4),
                direction = Direction.OUT,
                amount = BigDecimal(100),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 1,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(3),
                direction = Direction.OUT,
                amount = BigDecimal(200),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 2,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(2),
                direction = Direction.OUT,
                amount = BigDecimal(150),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 3,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(1),
                direction = Direction.OUT,
                amount = BigDecimal(50),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 4,
                accountId = 1
            ),
        )

        // When: predicting the amount for a future date
        val futureDateTime = OffsetDateTime.now(clock).plusDays(5)
        val predictedAmount = predictor.predictAmount(futureDateTime, transactions)

        // Then: assert that the predicted amount is as expected
        assertEquals(BigDecimal(-1215), predictedAmount.setScale(0, RoundingMode.HALF_UP))
    }

    @Test
    fun `should predict amount correctly with mixed transactions`() {
        // Given: a mixed list of transactions
        val transactions = listOf(
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(4),
                direction = Direction.IN,
                amount = BigDecimal(100),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 1,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(3),
                direction = Direction.OUT,
                amount = BigDecimal(50),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 2,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(2),
                direction = Direction.IN,
                amount = BigDecimal(300),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 3,
                accountId = 1
            ),
            TransactionEntity(
                createdAt = OffsetDateTime.now(clock).minusDays(1),
                direction = Direction.OUT,
                amount = BigDecimal(100),
                type = "one_off",
                currency = "en-GB",
                vendorId = "SupermanCorp-id",
                id = 4,
                accountId = 1
            ),
        )

        // When: predicting the amount for a future date
        val futureDateTime = OffsetDateTime.now(clock).plusDays(5)
        val predictedAmount = predictor.predictAmount(futureDateTime, transactions)

        // Then: assert that the predicted amount is as expected
        assertEquals(BigDecimal(675), predictedAmount.setScale(0, RoundingMode.HALF_UP))
    }

    @Test
    fun `should throw exception when transactions list is empty`() {
        // Given: an empty transaction list
        val transactions = emptyList<TransactionEntity>()

        // When: predicting amount should throw IllegalArgumentException
        val futureDateTime = OffsetDateTime.now(clock).plusDays(5)
        val result = assertThrows<IllegalArgumentException> {
            predictor.predictAmount(futureDateTime, transactions)
        }

        // Then: assert exception message
        assertEquals("Transactions list must not be empty", result.message)
    }
}