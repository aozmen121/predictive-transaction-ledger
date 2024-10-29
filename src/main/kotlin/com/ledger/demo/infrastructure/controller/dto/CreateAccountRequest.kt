package com.ledger.demo.infrastructure.controller.dto

import java.math.BigDecimal

data class CreateAccountRequest(
    val fullName: String,
    val email: String,
    val amount: BigDecimal,
    val currency: String,
)