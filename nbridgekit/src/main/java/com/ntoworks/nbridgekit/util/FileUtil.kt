package com.ntoworks.nbridgekit.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class FileUtil {

    companion object {
        /**
         * file Uri로 외부앱내 저장소에 파일저장
         */
        suspend fun saveFileUriToExternalStorage(
            context: Context,
            uri: Uri,
            folder: String,
            fileName: String
        ) {
            withContext(Dispatchers.IO) {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext
                val outputFile = File(context.getExternalFilesDir(folder), fileName)

                try {
                    FileOutputStream(outputFile).use { outputStream ->
                        val buffer = ByteArray(4 * 1024) // 4KB buffer
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                        outputStream.flush()
                    }
                } catch (e: Exception) {
                    // Handle exceptions (e.g., IOException, SecurityException)
                    // Log the error, display a message to the user, etc.
                } finally {
                    inputStream.close()
                }
            }
        }

        /**
         *  파일명 생성
         */
        @SuppressLint("SimpleDateFormat")
        fun generateFileName(prefix: String, extension: String): String {
            val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            return "cam_$timeStamp.$extension"
        }

        /**
         * Uri의 파일명을 가져와서 새로운 파일명 생성
         */
        fun generateFileName(context: Context, uri: Uri): String {
            context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                cursor?.let { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) // 파일 이름
//                val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE) // 파일 사이즈
                    cursor.moveToFirst()
                    val fileName = cursor.getString(nameIndex)

                    return "${System.currentTimeMillis()}_${fileName}"

                }
            }
            return "${System.currentTimeMillis()}"
        }

        /**
         * 비트맵을 파일로 저장
         */
        fun saveBitmap(context: Context, file: File, bitmap: Bitmap) {
            // Bitmap을 파일로 저장
            try {
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}