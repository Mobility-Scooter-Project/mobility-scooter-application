package com.mobility.scooterapp

import org.junit.Test
import org.junit.Assert.*
class SignupTests {
    @Test
    fun allFieldsValid_returnsSuccess() {
        val result = SignupValidator.validateFields(
            "John", "Doe", "john@example.com", "password123", "password123", true
        )
        assertTrue(result is SignupValidator.SignupValidationResult.Success)
    }

    @Test
    fun blankFields_returnsError() {
        val result = SignupValidator.validateFields(
            "", "Doe", "john@example.com", "password123", "password123", true
        )
        assertEquals("Fields cannot be empty", (result as SignupValidator.SignupValidationResult.Error).message)
    }

    @Test
    fun shortPassword_returnsError() {
        val result = SignupValidator.validateFields(
            "John", "Doe", "john@example.com", "pass", "pass", true
        )
        assertEquals("Password must be at least 9 characters", (result as SignupValidator.SignupValidationResult.Error).message)
    }

    @Test
    fun mismatchedPasswords_returnsError() {
        val result = SignupValidator.validateFields(
            "John", "Doe", "john@example.com", "password123", "different", true
        )
        assertEquals("Passwords do not match", (result as SignupValidator.SignupValidationResult.Error).message)
    }

    @Test
    fun uncheckedTerms_returnsError() {
        val result = SignupValidator.validateFields(
            "John", "Doe", "john@example.com", "password123", "password123", false
        )
        assertEquals("Please read and accept the terms and conditions", (result as SignupValidator.SignupValidationResult.Error).message)
    }
}