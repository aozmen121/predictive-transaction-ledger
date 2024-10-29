package com.ledger.demo.application.dto

import java.math.BigDecimal

/**
 * Transaction request data transfer object
 */
data class TransactionRequestDto(
    val accountId: Long,
    val amount: BigDecimal,
    val currency: String,
    val type: String,
    val direction: Direction,
    val vendorId: String
)