package com.mobility.scooterapp
import org.junit.Assert.*
import org.junit.Test
class ResetPasswordTest {
    @Test
    fun validEmail_returnsTrue() {
        assertTrue(InputValidator.isValidEmail("test@example.com"))
    }

    @Test
    fun blankEmail_returnsFalse() {
        assertFalse(InputValidator.isValidEmail(""))
    }

    @Test
    fun malformedEmail_returnsFalse() {
        assertFalse(InputValidator.isValidEmail("not-an-email"))
    }
}