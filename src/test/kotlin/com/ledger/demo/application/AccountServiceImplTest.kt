package com.ledger.demo.application

import com.ledger.demo.application.dto.AccountRequestDto
import com.ledger.demo.domain.AccountEntity
import com.ledger.demo.infrastructure.persist.IAccountRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset

class AccountServiceImplTest {

    private lateinit var accountRepository: IAccountRepository
    private lateinit var clock: Clock
    private lateinit var accountService: AccountServiceImpl

    @BeforeEach
    fun setUp() {
        accountRepository = mock(IAccountRepository::class.java)
        clock = Clock.fixed(OffsetDateTime.now().toInstant(), ZoneOffset.UTC)
        accountService = AccountServiceImpl(accountRepository, clock)
    }

    @Test
    fun `should save account with correct details`() {
        // Given
        whenever(accountRepository.save(any(AccountEntity::class.java))).thenReturn(mock(AccountEntity::class.java))
        val accountRequestDto = AccountRequestDto(
            fullName = "Batman Smith",
            email = "batman@batman.com",
            amount = BigDecimal(1000),
            currency = "en-GB"
        )
        val expectedCreatedAt = OffsetDateTime.now(clock)

        // When
        accountService.addAccount(accountRequestDto)

        // Then
        val accountEntityCaptor = ArgumentCaptor.forClass(AccountEntity::class.java)
        verify(accountRepository, times(1)).save(accountEntityCaptor.capture())
        val savedAccountEntity = accountEntityCaptor.value

        assertEquals(accountRequestDto.fullName, savedAccountEntity.fullName)
        assertEquals(accountRequestDto.email, savedAccountEntity.email)
        assertEquals(accountRequestDto.amount, savedAccountEntity.balance)
        assertEquals(expectedCreatedAt, savedAccountEntity.createdAt)
    }
}