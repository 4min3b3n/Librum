package com.librum.ui.reader

import android.os.Bundle
import com.librum.data.model.TOCLinkWrapper
import com.librum.ui.base.BaseView
import org.readium.r2_streamer.model.publication.EpubPublication
import org.readium.r2_streamer.model.publication.link.Link
import java.util.*

/**
 * @author lusinabrian on 05/09/17.
 * @Notes View interface for epub reader
 */
interface EpubReaderView : BaseView {

    /**
     * displays a loading progress bar
     * */
    fun displayLoadingProgress()

    /**
     * dismisses loading progres
     * */
    fun dismissLoadingProgress()

    /**
     * displays error page
     * */
    fun displayErrorPage()

    /**
     * hides the error page
     * */
    fun hideErrorPage()

    /**
     * sets up the toolbar
     * */
    fun setupToolbarAndDrawer()

    /**
     * sets up the navigation drawer recycler for TOC
     * */
    fun setupNavigationDrawerRecycler()

    /**
     * sets up the directional view pager adapter
     * */
    fun setupDirectionalViewPagerAdapter(mSpineReferenceList: ArrayList<Link>, epubTitle: String)

    /**
     * updates the navigation drawer Header
     * with the book cover and the book title, and author
     * */
    fun updateNavigationDrawerHeader()

    /**
     * updates the table of contents for the wrapper
     * @param tocLinkWrappers TOC link wrappers for the epub
     * */
    fun updateNavigationTableOfContents(tocLinkWrappers: ArrayList<TOCLinkWrapper>)

    /**
     * On load publication, will be used to load the epub publication
     * @param publication Epub publication to load
     * */
    fun onLoadPublication(publication: EpubPublication)

    /**
     * this receives bundle from previous calling activity,
     * This will be used to load the document and get more information on the epub
     * @param savedInstanceState, this is the bundle state of the view.
     * This will be used to load the Bundle from the saved state instead of from the received
     * bundle all the time, this will be from the previous saved state of the view
     * */
    fun receiveEpubIntent(savedInstanceState: Bundle?)

    /**
     * animate the toolbar and hide it
     * */
    fun toolBarAnimateHide()

    /**
     * Animates and shows the toolbar
     * */
    fun toolbarAnimateShow()

    /**
     * Initializes and renders the book
     * @param epubTitle Title of book
     * @param fileLocation file location of book
     * */
    fun initializeAndRenderBook(epubTitle: String, fileLocation: String)
}