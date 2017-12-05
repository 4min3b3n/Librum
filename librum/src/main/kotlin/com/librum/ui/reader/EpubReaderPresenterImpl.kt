package com.librum.ui.reader

import android.os.Bundle
import com.librum.data.EpubReaderDataManager
import com.librum.data.io.EpubSchedulerProvider
import com.librum.data.model.TOCLinkWrapper
import com.librum.ui.base.BasePresenterImpl
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.error
import org.readium.r2_streamer.model.publication.link.Link
import org.readium.r2_streamer.model.tableofcontents.TOCLink
import java.util.*
import javax.inject.Inject

/**
 * @author lusinabrian on 05/09/17.
 * @Notes presenter implementation for EpubReader
 */
class EpubReaderPresenterImpl<V : EpubReaderView>
@Inject
constructor(mEpubReaderDataManager: EpubReaderDataManager, mCompositeDisposable: CompositeDisposable,
            mSchedulerProvider: EpubSchedulerProvider) : EpubReaderPresenter<V>,
        BasePresenterImpl<V>(mEpubReaderDataManager, mCompositeDisposable, mSchedulerProvider) {

    override fun onAttach(mBaseView: V) {
        super.onAttach(mBaseView)
        baseView.displayLoadingProgress()
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        baseView.receiveEpubIntent(savedInstanceState)
        baseView.setupToolbarAndDrawer()
    }

    override fun doesBookExist(epubId: String): Boolean {
        val epubLocation = epubReaderDataManager.getEpubFileLocation(epubId)
        return !epubLocation.isEmpty()
    }

    override fun getEpubFileLocation(epubId: String): String? {
        return epubReaderDataManager.getEpubFileLocation(epubId)
    }

    override fun onDownloadBook(epubUrl: String) {
        baseView.hideErrorPage()
        baseView.displayLoadingProgress()
//
//        val epubTitle = epubBundle.title
//        val epubId = epubBundle.id
//        val sourceUrl = epubBundle.data!!.srcUrls!![0].srcUrl
        var errorEncountered = false

        mCompositeDisposable.add(
                epubReaderDataManager.downloadEpubFile(epubUrl)
                        .subscribeOn(mSchedulerProvider.newThread())
                        .subscribe({
                            errorEncountered = if (it.isSuccessful) {
                                // success, on next
                                val fileLocation = epubReaderDataManager.saveResponseBodyToDisk(
                                        "epubId", it.body())

                                // save to shared prefs
                                epubReaderDataManager.saveEpubFileLocation("epubId", fileLocation)
                                false
                                // now we can initialize the book
//                                baseView.setupNavigationDrawerRecycler()
//                                baseView.updateNavigationDrawerHeader()
//                                baseView.initializeAndRenderBook(epubTitle, fileLocation)
//                                baseView.hideErrorPage()
//                                baseView.dismissLoadingProgress()
                            } else {
                                error("Encountered error getting response msg[${it.message()}]\n," +
                                        "body => ${it.errorBody()}")
                                true
                            }
                        }, {
                            // error
                            error("Error downloading book ${it.message}", it)
                            errorEncountered = true
                            //baseView.dismissLoadingProgress()
                            //baseView.displayErrorPage()
                        })
        )

        if (errorEncountered) {
            baseView.displayErrorPage()
            baseView.dismissLoadingProgress()
        } else {
            // now we can initialize the book
            baseView.setupNavigationDrawerRecycler()
            baseView.updateNavigationDrawerHeader()
            val epubFileLocation = epubReaderDataManager.getEpubFileLocation("epubId")
            baseView.initializeAndRenderBook("epubTitle", epubFileLocation)
            baseView.hideErrorPage()
            baseView.dismissLoadingProgress()
        }
    }

    override fun onInitializeBook(mEpubFileName: String, epubFilePath: String) {
        // start up the epub server
        epubReaderDataManager.startEpubServer()

        // add epub to container
        epubReaderDataManager.addEpubToContainer(epubFilePath, mEpubFileName)

        // parse manifest
        mCompositeDisposable.add(
                epubReaderDataManager.parseManifest(mEpubFileName)
                        .subscribeOn(mSchedulerProvider.newThread())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe({
                            baseView.onLoadPublication(it)
                        }, {
                            error("Failed to get manifest with error ${it.message}", it)
                            baseView.displayErrorPage()
                        })
        )
    }

    override fun onPublicationLoaded(mSpineReferenceList: ArrayList<Link>, epubTitle: String) {
        baseView.setupDirectionalViewPagerAdapter(mSpineReferenceList, epubTitle)
    }

    override fun onBackPressedSaveBookState(bookTitle: String, pageViewPagerPosition: Int, webViewScrollPosition: Int) {
        epubReaderDataManager.saveBookState(bookTitle, pageViewPagerPosition, webViewScrollPosition)
    }

    override fun queryPreviousBookState(epubTitle: String): Boolean {
        return epubReaderDataManager.checkPreviousBookStateExist(epubTitle)
    }

    override fun getPreviousBookState(epubTitle: String): Int {
        return epubReaderDataManager.getPreviousBookStatePosition(epubTitle)
    }

    override fun onTableOfContentsLoaded(tocLinks: MutableList<TOCLink>) {
        val tocLinkWrappers = tocLinks.mapTo(arrayListOf(), { createTocLinkWrapper(it, 0) })
        baseView.updateNavigationTableOfContents(tocLinkWrappers)
    }

    override fun onTableOfContentsLoadFromSpine(spineList: List<Link>) {
        baseView.updateNavigationTableOfContents(createTOCFromSpine(spineList))
    }

    /**
     * function generates list of [TOCLinkWrapper] of TOC list from publication manifest
     *
     * @param tocLink table of content elements
     * @param indentation level of hierarchy of the child elements
     * @return generated [TOCLinkWrapper] list
     */
    private fun createTocLinkWrapper(tocLink: TOCLink, indentation: Int): TOCLinkWrapper {
        val tocLinkWrapper = TOCLinkWrapper(tocLink, indentation)
        if (tocLink.getTocLinks() != null && !tocLink.getTocLinks().isEmpty()) {
            tocLink.getTocLinks()
                    .asSequence()
                    .map { createTocLinkWrapper(it, indentation + 1) }
                    .filter { it.indentation != 3 }
                    .forEach { tocLinkWrapper.addChild(it) }
        }
        return tocLinkWrapper
    }

    private fun createTOCFromSpine(spine: List<Link>): ArrayList<TOCLinkWrapper> {
        val tocLinkWrappers = ArrayList<TOCLinkWrapper>()
        for (link in spine) {
            val tocLink = TOCLink()
            tocLink.bookTitle = link.bookTitle
            tocLink.href = link.href
            tocLinkWrappers.add(TOCLinkWrapper(tocLink, 0))
        }
        return tocLinkWrappers
    }

    override fun onStop() {
        epubReaderDataManager.stopEpubServerIfRunning()
    }

    override fun onDetach() {
        epubReaderDataManager.stopEpubServerIfRunning()
        mCompositeDisposable.dispose()
        super.onDetach()
    }
}