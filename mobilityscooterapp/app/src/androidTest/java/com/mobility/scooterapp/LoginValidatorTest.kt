package com.mobility.scooterapp

import org.junit.Assert.*
import org.junit.Test

class LoginValidatorTest {

    @Test
    fun validLogin() {
        assertTrue(LoginValidator.isValid("test@example.com", "123456"))
    }

    @Test
    fun testEmptyEmail() {
        assertFalse(LoginValidator.isValid("", "123456"))
    }

    @Test
    fun testEmptyPassword() {
        assertFalse(LoginValidator.isValid("test@example.com", ""))

    }
}
