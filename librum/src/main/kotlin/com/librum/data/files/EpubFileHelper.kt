package com.librum.data.files

import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoLogger

/**
 * @author lusinabrian on 05/09/17.
 * @Notes
 */
interface EpubFileHelper : AnkoLogger {

    /**
     * saves response body of file helper to disk and returns the file path
     * @param documentId
     * */
    fun saveResponseBodyToDisk(documentId: String, responseBody: ResponseBody?): String
}