package com.ledger.demo.application

import com.ledger.demo.infrastructure.controller.dto.CalculateBalanceRequest
import com.ledger.demo.infrastructure.controller.dto.CalculateBalanceResult

/**
 * Interface to handle balance calculator
 */
interface ICalculateBalanceService {

    /**
     * Calculates the amount based on historical transactions
     *
     * @param calculateBalanceRequest Contains from and to timestamps to calculate amount from.
     *
     * @return CalculateBalanceResult Successfully calculated balance
     */
    fun calculateBalance(calculateBalanceRequest: CalculateBalanceRequest): CalculateBalanceResult
}