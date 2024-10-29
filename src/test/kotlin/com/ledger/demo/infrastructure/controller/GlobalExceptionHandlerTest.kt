package com.ledger.demo.infrastructure.controller

import com.ledger.demo.application.exception.InvalidTimestampRangeException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class GlobalExceptionHandlerTest {

    private val globalExceptionHandler = GlobalExceptionHandler()

    @Test
    fun `should return BAD_REQUEST for IllegalArgumentException`() {
        // Given
        val exception = IllegalArgumentException("Invalid argument provided")

        // When
        val response: ResponseEntity<Any> = globalExceptionHandler.handleStudentNotFoundException(exception)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid argument provided", response.body)
    }

    @Test
    fun `should return BAD_REQUEST for InvalidTimestampRangeException`() {
        // Given
        val exception = InvalidTimestampRangeException("Invalid timestamp range provided")

        // When
        val response: ResponseEntity<Any> = globalExceptionHandler.handleStudentAlreadyExistsException(exception)

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid timestamp range provided", response.body)
    }

    @Test
    fun `should return INTERNAL_SERVER_ERROR for RuntimeException`() {
        // Given
        val exception = RuntimeException("A runtime error occurred")

        // When
        val response: ResponseEntity<Any> = globalExceptionHandler.handleRuntimeException(exception)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("A runtime error occurred", response.body)
    }
}