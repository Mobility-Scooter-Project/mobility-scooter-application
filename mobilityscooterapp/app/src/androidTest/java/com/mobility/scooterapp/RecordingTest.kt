package com.mobility.scooterapp

import org.junit.Assert.*
import org.junit.Test
import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import androidx.test.core.app.ApplicationProvider
import java.io.File

class RecordingTest {
    @Test
    fun decryptFile_returnsCorrectContent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val testContent = "This is secret test data"

        // Create and encrypt a file
        val encryptedFile = File(context.filesDir, "test_encrypted_file.txt")
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val encrypted = EncryptedFile.Builder(
            encryptedFile,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encrypted.openFileOutput().use { output ->
            output.write(testContent.toByteArray())
        }

        // Decrypt it using your FileDecrypter class
        val decryptedFile = com.mobility.scooterapp.RecordingHelper.decryptFile(context, encryptedFile)

        val decryptedText = decryptedFile.readText()

        // Validate the content matches
        assertEquals(testContent, decryptedText)
    }
}