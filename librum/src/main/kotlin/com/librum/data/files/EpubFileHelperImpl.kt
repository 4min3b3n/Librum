package com.librum.data.files

import android.content.Context
import android.content.ContextWrapper
import com.librum.di.qualifiers.AppContextQualifier
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import java.io.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author lusinabrian on 05/09/17.
 * @Notes Implementation for the file helper
 */
@Singleton
class EpubFileHelperImpl
@Inject
constructor(@AppContextQualifier val context: Context) : EpubFileHelper {

    override val loggerTag: String get() = super.loggerTag

    override fun saveResponseBodyToDisk(documentId: String, responseBody: ResponseBody?): String {
        val cw = ContextWrapper(context.applicationContext)
        // create the directory to be used, this will be private to the application
        val directory = cw.getDir("epubs", Context.MODE_PRIVATE)

        // this will allow creating the extension of the document
        val documentFilePath = File(directory, documentId + ".epub")

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        val fileReader = ByteArray(4096)
        var fileSizeDownloaded = 0
        try {
            // we can now attempt to download the file
            try {
                // var fileSize = responseBody?.contentLength()
                inputStream = responseBody?.byteStream()
                outputStream = FileOutputStream(documentFilePath)

                while (true) {
                    val read = inputStream?.read(fileReader)
                    if (read == -1)
                        break

                    read?.let { outputStream?.write(fileReader, 0, it) }

                    fileSizeDownloaded += read!!
                }

                outputStream.flush()

                info { "Epub file path ${documentFilePath.absolutePath}" }
                // return downloaded path to file
                return documentFilePath.absolutePath
            } catch (io: IOException) {
                error("Download failed with error ${io.message}", io)

                // return empty string
                return ""
            } finally {
                doAsync {
                    if (inputStream != null) {
                        inputStream!!.close()
                    }

                    outputStream?.close()
                }
            }
        } catch (io: IOException) {
            io.printStackTrace()
            error("Error getting download ${io.message}", io)
            return ""
        }
    }

    /**
     * gets epub folder path
     * */
    private fun getEpubFolderPath(epubFileName: String): String {
        val cw = ContextWrapper(context.applicationContext)
        // create the directory to be used, this will be private to the application
        val directory = cw.getDir("epub", Context.MODE_PRIVATE)
        val documentFilePath = File(directory, epubFileName)
        return "${documentFilePath.absoluteFile.absolutePath}$epubFileName"
    }

    /**
     * Checks if a folder is available
     * */
    private fun isFolderAvailable(epubFileName: String): Boolean {
        val file = File(getEpubFolderPath(epubFileName))
        return file.isDirectory
    }
}