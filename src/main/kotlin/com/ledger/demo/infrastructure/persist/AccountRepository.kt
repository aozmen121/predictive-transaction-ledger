package com.ledger.demo.infrastructure.persist

import com.ledger.demo.domain.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IAccountRepository : JpaRepository<AccountEntity, Long>