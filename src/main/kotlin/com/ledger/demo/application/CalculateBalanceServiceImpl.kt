// PredictionServiceImpl.kt
package com.ledger.demo.application

import com.ledger.demo.application.dto.Direction
import com.ledger.demo.application.exception.InvalidTimestampRangeException
import com.ledger.demo.application.predictor.IPredictBalanceService
import com.ledger.demo.domain.TransactionEntity
import com.ledger.demo.infrastructure.persist.IAccountRepository
import com.ledger.demo.infrastructure.persist.ITransactionRepository
import com.ledger.demo.infrastructure.controller.dto.CalculateBalanceRequest
import com.ledger.demo.infrastructure.controller.dto.CalculateBalanceResult
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Clock
import java.time.OffsetDateTime

/**
 * Manages amount balance calculations using certain algorithms.
 */
@Service
class CalculateBalanceServiceImpl(
    private val predictBalanceService: IPredictBalanceService,
    private val accountRepository: IAccountRepository,
    private val transactionRepository: ITransactionRepository,
    private val clock: Clock
) : ICalculateBalanceService {

    /**
     * Calculates amount value based on the retrieved historical transactions via from and to timestamp ranges.
     *
     * @param calculateBalanceRequest Object that hols the datetime range info and the account info to apply the calculation.
     *
     * @return CalculateBalanceResult Calculated amount for the given account
     */
    override fun calculateBalance(calculateBalanceRequest: CalculateBalanceRequest): CalculateBalanceResult {
        if(calculateBalanceRequest.fromDateTime.isAfter(calculateBalanceRequest.toDateTime)){
            throw InvalidTimestampRangeException("Given from date is after to date. From: ${calculateBalanceRequest.fromDateTime}, To: ${calculateBalanceRequest.toDateTime}")
        }

        val account = accountRepository
            .findById(calculateBalanceRequest.accountId)
            .orElseThrow { IllegalArgumentException("Account not found, accountId ${calculateBalanceRequest.accountId}") }

        // Retrieve recent transactions for the account
        val recentTransactions = transactionRepository.findByAccountIdAndCreatedAtBetween(
            calculateBalanceRequest.accountId,
            calculateBalanceRequest.fromDateTime,
            calculateBalanceRequest.toDateTime
        )

        if (recentTransactions.isEmpty()) {
            // No recent transactions; return the current balance as predicted balance
            return CalculateBalanceResult(
                accountId = calculateBalanceRequest.accountId,
                predictedBalance = account.balance,
            )
        }

        val currenDateTime = OffsetDateTime.now(clock)
        val calculatedAmount =
            if (calculateBalanceRequest.toDateTime.isAfter(currenDateTime)) {
                // Future balance prediction using linear regression
                predictBalanceService.predictAmount(calculateBalanceRequest.toDateTime, recentTransactions)
            } else {
                // Past or present balance calculation based on actual transactions
                calculatePastBalance(recentTransactions)
            }

        return CalculateBalanceResult(
            accountId = calculateBalanceRequest.accountId,
            predictedBalance = calculatedAmount,
        )
    }

    private fun calculatePastBalance(transactions: List<TransactionEntity>): BigDecimal {
        // Calculate balance by summing/sub transactions
        return transactions.fold(BigDecimal.ZERO) { acc, transaction ->
            when (transaction.direction) {
                Direction.OUT -> acc.subtract(transaction.amount)
                Direction.IN -> acc.add(transaction.amount)
            }
        }
    }
}
