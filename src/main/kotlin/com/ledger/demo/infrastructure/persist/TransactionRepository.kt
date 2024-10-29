package com.ledger.demo.infrastructure.persist

import com.ledger.demo.domain.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ITransactionRepository : JpaRepository<TransactionEntity, Long> {

    fun findByAccountIdAndCreatedAtBetween(
        accountId: Long,
        start: OffsetDateTime,
        end: OffsetDateTime
    ): List<TransactionEntity>
}