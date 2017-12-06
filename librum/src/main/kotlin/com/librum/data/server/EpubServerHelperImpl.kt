package com.librum.data.server

import com.librum.utils.LOCALHOST
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.error
import org.readium.r2_streamer.model.container.EpubContainer
import org.readium.r2_streamer.model.publication.EpubPublication
import org.readium.r2_streamer.server.EpubServer
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author lusinabrian on 06/09/17.
 * @Notes implementation for server helper
 */
@Singleton
class EpubServerHelperImpl
@Inject
constructor(@Named("EpubServerInstance")
            val mEpubServerInstance: EpubServer,
            val mEpubRetrofitService: EpubRetrofitService) : EpubServerHelper {

    override val loggerTag: String
        get() = super.loggerTag

    override fun startEpubServer() {
        mEpubServerInstance.start()
    }

    override fun stopEpubServerIfRunning() {
        if (mEpubServerInstance.isAlive) {
            mEpubServerInstance.stop()
        }
    }

    override fun addEpubToContainer(filePath: String, epubTitle: String) {
        val epubContainer = EpubContainer(filePath)
        mEpubServerInstance.addEpub(epubContainer, "/" + epubTitle)
    }

    override fun parseManifest(epubFileName: String): Observable<EpubPublication> {
        val epubUrl = LOCALHOST + epubFileName + "/manifest"

        val epubPublicationTask: (AnkoAsyncContext<Any>.() -> Observable<EpubPublication>) = {
            mEpubRetrofitService.parseEpubManifest(epubUrl)
        }

        val epubAsyncResult = doAsyncResult({
            // todo: exception handler
            error("Error encountered fetching Epub Publication", it)
        }, epubPublicationTask)

        return epubAsyncResult.get()
    }

    override fun downloadHtmlWebPage(webPageUrl: String): Observable<Response<ResponseBody>> {
        return mEpubRetrofitService.downloadHtmlWebPage(webPageUrl)
    }

    override fun downloadEpubFile(fileUrl: String): Observable<Response<ResponseBody>> {
        return mEpubRetrofitService.downloadEpubFile(fileUrl)
    }
}