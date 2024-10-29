package com.ledger.demo.application

import com.ledger.demo.application.dto.AccountRequestDto

/**
 * Contract to implement account management
 */
interface IAccountService {

    /**
     * Creates and persists new accounts
     *
     * @param accountRequestDto Account details to persist
     *
     * @return Long account Id after persistance
     */
    fun addAccount(accountRequestDto: AccountRequestDto): Long
}