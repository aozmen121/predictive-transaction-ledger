package com.ledger.demo.application

import com.ledger.demo.application.dto.AccountRequestDto
import com.ledger.demo.domain.AccountEntity
import com.ledger.demo.infrastructure.persist.IAccountRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.OffsetDateTime

/**
 * Manages account related actions
 */
@Service
class AccountServiceImpl(
    private val accountRepository: IAccountRepository,
    private val clock: Clock
) : IAccountService {

    /**
     * Adds new accounts to persistence layer via Crud Repos
     *
     * @param accountRequestDto Contract to transfer account dto object to persist layer storage
     *
     * @return Long AccountID generated after persistence
     */
    override fun addAccount(accountRequestDto: AccountRequestDto): Long {
        return accountRepository.save(
            AccountEntity(
                fullName = accountRequestDto.fullName,
                email = accountRequestDto.email,
                balance = accountRequestDto.amount,
                createdAt = OffsetDateTime.now(clock),
            )
        ).id
    }
}