package com.ledger.demo.application.predictor

import com.ledger.demo.domain.TransactionEntity
import java.math.BigDecimal
import java.time.OffsetDateTime

/**
 * Interface to implement different estimated/predictor algorithms for future amount calculations
 */
interface IPredictBalanceService {

    /**
     * This method calculates and estimates based on historical transaction as to what
     * the future amount will be
     *
     * @param futureDateTime Offsetdatetime object that represents the future timestamp
     * @param transactions List of historical transactions
     *
     * @return BigDecimal calculated estimation amount based on historical data
     */
    fun predictAmount(futureDateTime: OffsetDateTime, transactions: List<TransactionEntity>): BigDecimal
}