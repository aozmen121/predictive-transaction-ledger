package com.ledger.demo.infrastructure.controller.dto

import com.ledger.demo.application.dto.Direction
import java.math.BigDecimal

data class CreateTransactionRequest(
    val amount: BigDecimal,
    val currency: String,
    val type: String,
    val direction: Direction,
    val vendorId: String
)