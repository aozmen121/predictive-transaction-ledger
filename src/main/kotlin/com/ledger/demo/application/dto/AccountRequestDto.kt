package com.ledger.demo.application.dto

import java.math.BigDecimal

/**
 * Accounts request data transfer object
 */
data class AccountRequestDto (
    val fullName: String,
    val email: String,
    val amount: BigDecimal,
    val currency: String,
)
