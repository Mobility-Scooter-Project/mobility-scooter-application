package com.mobility.scooterapp

import org.junit.Assert.*
import org.junit.Test
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import androidx.test.core.app.ApplicationProvider
import java.io.File
import java.io.FileOutputStream

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
    @Test
    fun encryptFile_createsEncryptedFileSuccessfully() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val contentResolver = context.contentResolver

        // Create a dummy test file
        val testFile = File(context.cacheDir, "test_video.mp4")
        FileOutputStream(testFile).use { it.write("This is a test".toByteArray()) }

        // Use FileProvider to get a valid content:// Uri
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            testFile
        )

        // Encrypt
        val encryptedPath = RecordingHelper.encryptFile(context, fileUri, contentResolver)
        val encryptedFile = File(encryptedPath)

        // Assertions
        assertTrue(encryptedFile.exists())
        assertTrue(encryptedFile.length() > 0)

        // Cleanup
        testFile.delete()
        encryptedFile.delete()
    }



}