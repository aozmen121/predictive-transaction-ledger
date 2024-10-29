package com.ledger.demo.infrastructure.controller.dto

import java.time.OffsetDateTime

data class CalculateBalanceRequest(
    val accountId: Long,
    val fromDateTime: OffsetDateTime,
    val toDateTime: OffsetDateTime
)
