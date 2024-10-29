package com.ledger.demo.application.exception

/**
 * Exception is thrown if the dateTime given is within an invalid range
 */
class InvalidTimestampRangeException(message: String) : Exception(message)
