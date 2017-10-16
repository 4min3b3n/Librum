package com.librum.ui.reader

import android.os.Bundle
import com.librum.ui.base.BasePresenter
import org.readium.r2_streamer.model.publication.link.Link
import org.readium.r2_streamer.model.tableofcontents.TOCLink
import java.util.*

/**
 * @author lusinabrian on 05/09/17.
 * @Notes presenter interface for EpubReader
 */
interface EpubReaderPresenter<V : EpubReaderView> : BasePresenter<V> {

    /**
     * callback for when the view is created, This will be used to re-create the view
     * from the saved instance state if it is available there
     * @param savedInstanceState the bundle that has the previous state of the view
     * */
    fun onViewCreated(savedInstanceState: Bundle?)

    /**
     * Checks if the given book exists
     * @param epubId Id for the given epub document
     * @return [Boolean]
     * */
    fun doesBookExist(epubId: String): Boolean

    /**
     * downloads the book onto device
     * @param epubBundle the book bundle to download
     * */
    fun onDownloadBook(epubBundle: BaseEntity)

    /**
     * this starts up a server that initializes the book to display
     * @param mEpubFileName file name of the epub
     * @param
     * */
    fun onInitializeBook(mEpubFileName: String, epubFilePath: String)

    /**
     * save the book state when the back button is pressed
     * @param bookTitle Book title
     * @param pageViewPagerPosition Page view pager position
     * @param webViewScrollPosition web view scroll position
     * */
    fun onBackPressedSaveBookState(bookTitle: String, pageViewPagerPosition: Int,
                                   webViewScrollPosition: Int)

    /**
     * On publication loaded
     * This will update the adapter and set the adapter to the directional view pager
     * @param mSpineReferenceList spine reference list
     * @param epubTitle the epub title
     * */
    fun onPublicationLoaded(mSpineReferenceList: ArrayList<Link>, epubTitle: String)


    /**
     * updates the table of contents of the book for display
     * @param tocLinks TOC Links
     * */
    fun onTableOfContentsLoaded(tocLinks: MutableList<TOCLink>)

    /**
     * loads the TOC from spine if the table of contents is missing
     * @param spineList spine of document to load TOC from
     * */
    fun onTableOfContentsLoadFromSpine(spineList: List<Link>)

    /**
     * on stop we want to destroy the server
     * */
    fun onStop()

    /**
     * Query that the previous book state exists
     * @param epubTitle this will be used to load up the previous book state of the book
     * @return [Boolean] True if there is a previous book state
     * */
    fun queryPreviousBookState(epubTitle: String): Boolean

    /**
     * get the previous Book state
     * @param epubTitle The epub title to query
     * @return [Int] The previous book state
     * */
    fun getPreviousBookState(epubTitle: String): Int

    /**
     * Gets the epub file location given the id of the epub document
     * @param epubId Epub Id of the Epub document
     * @return [String] with file location
     * */
    fun getEpubFileLocation(epubId: String): String?
}