package com.librum.data.server

import io.reactivex.Observable
import okhttp3.ResponseBody
import org.readium.r2_streamer.model.publication.EpubPublication
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @author lusinabrian on 06/09/17.
 * @Notes Retrofit service for the application
 */
interface EpubRetrofitService {

    @Streaming
    @GET
    fun downloadEpubFile(@Url fileUrl: String): Observable<Response<ResponseBody>>

    /**
     * parses the given manifest from the given url to be able ot load the epub document
     * chapters and details
     * */
    @Streaming
    @GET
    fun parseEpubManifest(@Url fileUrl: String): Observable<EpubPublication>

    /**
     * downloads a given web page for display on a html document or the epub page fragment
     * @param webPage the web page url to download
     * @return [Observable] observable that is the Response of the download
     * */
    @Streaming
    @GET
    fun downloadHtmlWebPage(@Url webPage: String): Observable<Response<ResponseBody>>
}