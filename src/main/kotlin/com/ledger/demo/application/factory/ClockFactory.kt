package com.ledger.demo.application.factory

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 * Factory to initialize current system UTC clock, used to custom inject clock instance within tests
 */
@Configuration
class ClockFactory {

    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }
}
