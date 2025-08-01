package com.mits.subscription.data.repo

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext


import java.io.File
import java.io.FileOutputStream
import java.util.Date
import javax.inject.Inject

class FileHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val FILE_NAME_PDF = "pdfs"
        const val FILE_NAME_IMAGE = "images"
        const val FILE_NAME_OTHER = "others"
    }

    fun convert(uriStr: String?, fileName: String? = null): PaymentFile? {
        uriStr?.let { uri ->
            return PaymentFile(
                name = fileName ?: "",
                uri = Uri.parse(uri),
                mimeType = getMimeType(Uri.parse(uri))
            )
        } ?: return null
    }

    fun handleFile(uriStr: String): PaymentFile? {
        return try {
            val initialUri = Uri.parse(uriStr)
            val name =
                queryFileName(initialUri) ?: initialUri.lastPathSegment?.substringAfterLast("/")
                ?: "unknown_file"
            val mime = getMimeType(initialUri)
            val inputStream = context.contentResolver.openInputStream(initialUri) ?: return null

            val outputFile = getFileForSaving(mime)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            val newUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                outputFile
            )
            PaymentFile(name = name, mimeType = mime, uri = newUri)
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileForSaving(mime: String): File {
        val (filesDir, fileName) = if (mime == "application/pdf") {
            File(context.filesDir, FILE_NAME_PDF) to "${Date().time}.pdf"
        } else if (mime.startsWith("image/")) {
            File(context.filesDir, FILE_NAME_IMAGE) to "${Date().time}.jpg"
        } else {
            File(context.filesDir, FILE_NAME_OTHER) to "${Date().time}"
        }
        if (!filesDir.exists()) {
            filesDir.mkdirs()
        }
        return File(filesDir, fileName)
    }

    private fun getMimeType(uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "*/*"
    }

    private fun queryFileName(uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex != -1) {
                return it.getString(nameIndex)
            }
        }
        return null
    }

    fun clean(list: List<String>) {
        removeFilesFromDirectory(
            File(context.filesDir, FILE_NAME_PDF),
            list
        )
        removeFilesFromDirectory(
            File(context.filesDir, FILE_NAME_IMAGE),
            list
        )
        removeFilesFromDirectory(
            File(context.filesDir, FILE_NAME_OTHER),
            list
        )
    }

    private fun removeFilesFromDirectory(dir: File, listForExclude: List<String>) {
        if (!dir.exists() || !dir.isDirectory) return
        dir.listFiles()?.forEach { file ->
            val newUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            if (!listForExclude.contains(newUri.toString())) {
                Log.e("TEST", "Deleting file: ${file.name}")
                file.delete()
            }
        }
    }
}