package com.ledger.demo.infrastructure.controller.dto

import java.math.BigDecimal

data class BalanceResponse(
    val accountId: Long,
    val balance: BigDecimal,
)