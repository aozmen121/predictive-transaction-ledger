package com.ledger.demo.application

import com.ledger.demo.application.dto.TransactionRequestDto

/**
 * Manages transactional actions
 */
interface ITransactionService {

    /**
     * Adds new transactions
     *
     * @param transactionRequest Transactional request details
     *
     */
    fun addTransaction(transactionRequest: TransactionRequestDto)
}