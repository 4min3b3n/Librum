package com.librum.data

import com.librum.data.files.EpubFileHelper
import com.librum.data.prefs.EpubReaderPrefs
import com.librum.data.server.EpubServerHelper
import io.reactivex.Observable
import okhttp3.ResponseBody
import org.readium.r2_streamer.model.publication.EpubPublication
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author lusinabrian on 05/09/17.
 * @Notes implementation for the data manager
 */
@Singleton
class EpubReaderDataManagerImpl
@Inject
constructor(val epubReaderPrefs: EpubReaderPrefs, val epubFileHelper: EpubFileHelper,
            val epubServerHelper: EpubServerHelper) : EpubReaderDataManager {
    override fun removeBookState(bookTitleKey: String) {
        epubReaderPrefs.removeBookState(bookTitleKey)
    }

    override fun getEpubFileLocation(epubId: String): String {
        return epubReaderPrefs.getEpubFileLocation(epubId)
    }

    override fun saveBookState(bookTitle: String, pageViewPagerPosition: Int, webViewScrollPosition: Int) {
        epubReaderPrefs.saveBookState(bookTitle, pageViewPagerPosition, webViewScrollPosition)
    }

    override fun checkPreviousBookStateExist(bookName: String): Boolean {
        return epubReaderPrefs.checkPreviousBookStateExist(bookName)
    }

    override fun getPreviousBookStatePosition(bookName: String): Int {
        return epubReaderPrefs.getPreviousBookStatePosition(bookName)
    }

    override fun getPreviousBookStateWebViewPosition(bookTitle: String): Int {
        return epubReaderPrefs.getPreviousBookStateWebViewPosition(bookTitle)
    }

    override fun saveEpubFileLocation(fileKeyName: String, fileLocation: String) {
        epubReaderPrefs.saveEpubFileLocation(fileKeyName, fileLocation)
    }

    // ************** FILES *******************************
    override fun saveResponseBodyToDisk(documentId: String, responseBody: ResponseBody?): String {
        return epubFileHelper.saveResponseBodyToDisk(documentId, responseBody)
    }

    // ******************** server ***************************
    override fun startEpubServer() {
        epubServerHelper.startEpubServer()
    }

    override fun stopEpubServerIfRunning() {
        epubServerHelper.stopEpubServerIfRunning()
    }

    override fun addEpubToContainer(filePath: String, epubTitle: String) {
        epubServerHelper.addEpubToContainer(filePath, epubTitle)
    }

    override fun parseManifest(epubFileName: String): Observable<EpubPublication> {
        return epubServerHelper.parseManifest(epubFileName)
    }

    override fun downloadHtmlWebPage(webPageUrl: String): Observable<Response<ResponseBody>> {
        return epubServerHelper.downloadHtmlWebPage(webPageUrl)
    }

    override fun downloadEpubFile(fileUrl: String): Observable<Response<ResponseBody>> {
        return epubServerHelper.downloadEpubFile(fileUrl)
    }
}