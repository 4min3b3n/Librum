package com.librum.data.server

import io.reactivex.Observable
import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoLogger
import org.readium.r2_streamer.model.publication.EpubPublication
import retrofit2.Response
import java.io.IOException

/**
 * @author lusinabrian on 06/09/17.
 * @Notes server helper interface for loading up EPUB server
 */
interface EpubServerHelper : AnkoLogger{
    /**
     * Starts up the EPUB server to enable serving and loading epub documents
     * */
    fun startEpubServer()

    /**
     * stop epub server
     * */
    fun stopEpubServerIfRunning()

    /**
     * adds the epub document to container
     * @param filePath the epub file path
     * @throws [IOException]
     * */
    @Throws(IOException::class)
    fun addEpubToContainer(filePath : String, epubTitle : String)

    /**
     * parse manifest of the loaded Epub file
     * @param epubFileName the file name of the loaded epub file
     * @return [Observable] observable for the epub publication manifest
     * */
    fun parseManifest(epubFileName : String) : Observable<EpubPublication>

    /**
     * download html web page
     * @param webPageUrl the web page url to download the given html page
     * */
    fun downloadHtmlWebPage(webPageUrl : String) : Observable<Response<ResponseBody>>

    /**
     * downloads the epub file and saves its location to disk
     * @param fileUrl url for file to download
     * */
    fun downloadEpubFile(fileUrl : String): Observable<Response<ResponseBody>>
}