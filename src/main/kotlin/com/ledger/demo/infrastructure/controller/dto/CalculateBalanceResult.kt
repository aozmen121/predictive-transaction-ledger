package com.ledger.demo.infrastructure.controller.dto

import java.math.BigDecimal

data class CalculateBalanceResult(
    val accountId: Long,
    val predictedBalance: BigDecimal,
)