package com.ledger.demo.domain

import com.ledger.demo.application.dto.Direction
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.math.BigDecimal
import java.time.OffsetDateTime

/**
 * Transactions hibernate mapper entity
 */
@Entity
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
data class TransactionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @Column(name = "account_id", nullable = false)
    var accountId: Long,

    @Column(name = "amount", nullable = false)
    var amount: BigDecimal,

    @Column(name = "currency", nullable = false)
    var currency: String = "en-GB",

    @Column(name = "direction", nullable = false)
    var direction: Direction,

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime,

    @Column(name = "type", nullable = false)
    var type: String = "Recurring_Payment",

    @Column(name = "vendor_id ", nullable = false)
    var vendorId: String,
)