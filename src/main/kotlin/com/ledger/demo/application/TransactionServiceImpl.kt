package com.ledger.demo.application

import com.ledger.demo.application.dto.Direction
import com.ledger.demo.application.dto.TransactionRequestDto
import com.ledger.demo.domain.TransactionEntity
import com.ledger.demo.infrastructure.persist.IAccountRepository
import com.ledger.demo.infrastructure.persist.ITransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime

/**
 * Manages transactional actions
 */
@Service
class TransactionServiceImpl(
    private val accountRepository: IAccountRepository,
    private val transactionRepository: ITransactionRepository,
    private val clock: Clock,
) : ITransactionService {

    /**
     * Add new transactions against a valid account
     *
     * @param transactionRequest Transactional request details
     */
    @Transactional
    override fun addTransaction(transactionRequest: TransactionRequestDto) {
        // Check if account exists
        val account = accountRepository
            .findById(transactionRequest.accountId)
            .orElseThrow { IllegalArgumentException("Account not found, accountId ${transactionRequest.accountId}") }

        // Ensure there is enough funding for transactions
        if (transactionRequest.direction == Direction.OUT && account.balance < transactionRequest.amount) {
            throw IllegalArgumentException("Insufficient funds for available, accountId ${transactionRequest.accountId}")
        }

        when (transactionRequest.direction) {
            Direction.OUT -> account.balance = account.balance.subtract(transactionRequest.amount)
            Direction.IN -> account.balance = account.balance.add(transactionRequest.amount)
        }

        val transaction = TransactionEntity(
            accountId = account.id,
            amount = transactionRequest.amount,
            createdAt = OffsetDateTime.now(clock),
            direction = transactionRequest.direction,
            vendorId = transactionRequest.vendorId,
        )

        // persist transaction and account
        transactionRepository.save(transaction)
        accountRepository.save(account)
    }
}