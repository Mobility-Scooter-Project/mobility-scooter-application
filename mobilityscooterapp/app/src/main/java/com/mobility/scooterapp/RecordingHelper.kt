package com.mobility.scooterapp

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RecordingHelper {

    fun decryptFile(context: Context, encryptedFile: File): File {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val decryptedFile = File(context.filesDir, encryptedFile.name.replace("encrypted", "decrypted"))

        val encryptedFileInput = EncryptedFile.Builder(
            encryptedFile,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFileInput.openFileInput().use { inputStream ->
            FileOutputStream(decryptedFile).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }

        return decryptedFile
    }
    public fun encryptFile(context: Context, fileUri: Uri, contentResolver: ContentResolver): String {

        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val originalFile = DocumentFile.fromSingleUri(context, fileUri)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val encryptedFile = File(context.filesDir, "encrypted_$timeStamp.mp4")

        val encryptedFileOutput = EncryptedFile.Builder(
            encryptedFile,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        contentResolver.openInputStream(fileUri)?.use { inputStream ->
            encryptedFileOutput.openFileOutput().use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    outputStream.write(buffer, 0, length)
                }
            }
        }

        contentResolver.delete(fileUri, null, null)

        return encryptedFile.absolutePath
    }

    fun convertMillisToTimeFormat(millis: Long): String {
        val hours = millis / (1000 * 60 * 60) % 24
        val minutes = millis / (1000 * 60) % 60
        val seconds = millis / 1000 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}