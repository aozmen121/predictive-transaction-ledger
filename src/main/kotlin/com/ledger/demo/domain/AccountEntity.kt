package com.ledger.demo.domain

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.math.BigDecimal
import java.time.OffsetDateTime

/**
 * Account hibernate mapper entity
 */
@Entity
@Table(name = "accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
data class AccountEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0,

    @Column(name = "full_name", nullable = false)
    var fullName: String,

    @Column(name = "email", nullable = false)
    var email: String,

    @Column(name = "balance", nullable = false)
    var balance: BigDecimal,

    @Column(name = "currency", nullable = false)
    var currency: String = "en-GB",

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime,
)