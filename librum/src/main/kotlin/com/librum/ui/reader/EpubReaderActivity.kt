package com.librum.ui.reader

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import com.brck.moja.base.data.repo.local.db.entity.BaseEntity
import com.brck.moja.base.utils.DOCSURIKEY
import com.brck.moja.base.utils.loadImageFromUrl
import com.brck.moja.epubreader.R
import com.brck.moja.epubreader.data.db.DbAdapter
import com.brck.moja.epubreader.data.model.Highlight
import com.brck.moja.epubreader.data.model.TOCLinkWrapper
import com.brck.moja.epubreader.data.model.event.AnchorIdEvent
import com.librum.ui.base.BaseReaderActivity
import com.librum.ui.base.PageFragmentCallback
import com.librum.ui.pages.PageFragmentAdapter
import com.brck.moja.epubreader.ui.widgets.DirectionalViewpager
import com.librum.ui.widgets.DirectionalViewpager
import org.jetbrains.anko.info
import org.readium.r2_streamer.model.publication.EpubPublication
import org.readium.r2_streamer.model.publication.link.Link
import java.util.*
import javax.inject.Inject

/**
 * @author lusinabrian on 05/09/17.
 * @Notes EPUB reader activity, This is a re-write of the FolioActivity that allows for easier
 * modularisation and testing, the previous library did not allow for easy use with low level APIs
 * and also was too bloated to test and expand on.
 */
class EpubReaderActivity : BaseReaderActivity(), EpubReaderView, PageFragmentCallback, NavigationView.OnNavigationItemSelectedListener, DirectionalViewpager.OnPageChangeListener, EpubReaderChapterCallback {

    @Inject
    lateinit var epubReaderPresenter: EpubReaderPresenter<EpubReaderView>

    @Inject
    lateinit var epubChapterAdapter: EpubReaderChapterAdapter

    @Inject
    lateinit var pageFragmentAdapter: PageFragmentAdapter

    private lateinit var epubTitle: String
    private lateinit var epubBookCoverUrl: String

    lateinit var epubTitleView: TextView
    lateinit var epubAuthorTxtView: TextView
    var mIsActionBarVisible: Boolean = false
    private var mWebViewScrollPosition: Int = 0

    private val mSpineReferenceList = ArrayList<Link>()

    private var mChapterPosition: Int = 0
    lateinit var epubBundle: BaseEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epubreader)

        epubActivityComponent.inject(this)

        epubReaderPresenter.onAttach(this)

        epubReaderPresenter.onViewCreated(savedInstanceState)

        // set chapter callback
        epubChapterAdapter.setCallback(this)
    }

    override fun onResume() {
        super.onResume()

        if(epubReaderPresenter.doesBookExist(epubBundle.id)){
            val fileLocation = epubReaderPresenter.getEpubFileLocation(epubBundle.id)
            initializeAndRenderBook(epubBundle.title, fileLocation!!)
            // epubReaderPresenter.onInitializeBook()
        } else {
            // trigger download which will then initialize the book once done
            epubReaderPresenter.onDownloadBook(epubBundle)
        }

        // initialize the database
        DbAdapter(this)
    }

    /**
     * receives epub bundle from view
     * */
    override fun receiveEpubIntent(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            epubBundle = savedInstanceState.getParcelable(DOCSURIKEY)
        } else if (intent != null && intent.extras != null) {
            epubBundle = intent.extras.getParcelable(DOCSURIKEY)
        }

        epubTitle = epubBundle.title
        epubBookCoverUrl = epubBundle.data!!.thumbnailUrls!![0]
    }

    override fun initializeAndRenderBook(epubTitle: String, fileLocation: String) {
        epubReaderPresenter.onInitializeBook(epubTitle, fileLocation)
    }

    /**
     * save instance state of the epub document
     * */
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(DOCSURIKEY, epubBundle)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            epubBundle = savedInstanceState.getParcelable(DOCSURIKEY)
        }
    }

    override fun displayLoadingProgress() {
        epubProgressBar.visibility = View.VISIBLE
        directionalPageViewPager.visibility = View.GONE
    }

    override fun dismissLoadingProgress() {
        epubProgressBar.visibility = View.GONE
        directionalPageViewPager.visibility = View.VISIBLE
    }

    override fun displayErrorPage() {
        directionalPageViewPager.visibility = View.GONE
        epubErrorPage.visibility = View.VISIBLE

        epubErrorPage.findViewById<Button>(R.id.errorViewBtnRetry).setOnClickListener {
            epubReaderPresenter.onDownloadBook(epubBundle)
        }
    }

    override fun hideErrorPage() {
        epubErrorPage.visibility = View.GONE
    }

    override fun setupToolbarAndDrawer() {
        setSupportActionBar(epubReaderToolbar)
        // remove the default toolbar title
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(this, epubDrawerLayout, epubReaderToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        epubDrawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        //epubNavigationView = find(R.id.epubNavigationView)
        //epubNavigationRecycler = find(R.id.epubNavigationRecycler)

        epubNavigationView.setNavigationItemSelectedListener(this)
    }

    override fun setupNavigationDrawerRecycler() {
        val linearLayoutMgr = LinearLayoutManager(this)
        linearLayoutMgr.orientation = LinearLayoutManager.VERTICAL
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 500
        epubNavigationRecycler.itemAnimator = itemAnimator
        epubNavigationRecycler.layoutManager = linearLayoutMgr
        epubNavigationRecycler.adapter = epubChapterAdapter
    }

    override fun updateNavigationDrawerHeader() {
        val ctx = this
        with(epubNavigationHeader) {
            epubTitleView = epubBookTitle
            epubAuthorTxtView = epubBookAuthor

            epubBookCover.loadImageFromUrl(ctx, epubBookCoverUrl)
            epubBookTitle.text = epubTitle
            // epubBookAuthor.text = epubAuthor
        }
    }

    override fun setupDirectionalViewPagerAdapter(mSpineReferenceList: ArrayList<Link>, epubTitle: String) {
        directionalPageViewPager.addOnPageChangeListener(this)
        pageFragmentAdapter.spineReferences = mSpineReferenceList
        pageFragmentAdapter.epubFileName = epubTitle
        directionalPageViewPager.adapter = pageFragmentAdapter

        // if there is a previous book state, load that and display
        if (epubReaderPresenter.queryPreviousBookState(epubTitle)) {
            directionalPageViewPager.currentItem = epubReaderPresenter.getPreviousBookState(epubTitle)
        }
    }

    // *********************** page navigation ****************************
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {

    }

    override fun onPageScrollStateChanged(state: Int) {
        if (state == DirectionalViewpager.SCROLL_STATE_IDLE) {

        }
    }

    // ******************* Chapter item callback******************

    override fun onChapterClicked(position: Int) {
        val tocLinkWrapper = epubChapterAdapter.getItemAt(position)
        val selectedChapterHref = tocLinkWrapper.tocLink.href
        for (spine in mSpineReferenceList) {
            if (selectedChapterHref.contains(spine.href)) {
                mChapterPosition = mSpineReferenceList.indexOf(spine)
                directionalPageViewPager.currentItem = mChapterPosition

                // post event to be subscribed to, which will be an anchor id
                // this will by subscribed to by EpubPageFragment jumpToAnchorId function*/
                epubActivityComponent.getAnchorSubject().onNext(AnchorIdEvent(selectedChapterHref))
                epubDrawerLayout.closeDrawer(GravityCompat.START)
                break
            }
        }
    }

    override fun onChapterExpanded(position: Int) {

    }

    // *********************NAVIGATION item selected listener ******************
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

        }

        epubDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * On back pressed we save the book state
     * */
    override fun onBackPressed() {
        epubReaderPresenter.onBackPressedSaveBookState(epubTitle,
                directionalPageViewPager.currentItem, mWebViewScrollPosition)
        super.onBackPressed()
    }

    // ********************** menu configs *************************

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_menu_search -> {
                // todo: implement searching through epub document
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setCurrentPagerPosition(highlight: Highlight): Highlight {
        highlight.currentPagerPostion = directionalPageViewPager.currentItem
        return highlight
    }

    //******************** base view ********************
    override fun onError() {
    }

    //************************* epub reader view  *************************
    override fun onLoadPublication(publication: EpubPublication) {
        mSpineReferenceList.addAll(publication.spines)
        if (publication.metadata.title != null) {
            epubTitleView.text = publication.metadata.title
        }

        if (publication.metadata.creators.isNotEmpty()) {
            val author = publication.metadata.creators[0]
            epubAuthorTxtView.text = author.getName()
        }

        info("Publication $publication")

        // set the adapter to the page fragment adapter
        epubReaderPresenter.onPublicationLoaded(mSpineReferenceList, epubTitle)

        // update navigation drawer with the chapters
        if (publication.tableOfContents.isNotEmpty()) {
            if (publication.tableOfContents != null) {
                epubReaderPresenter.onTableOfContentsLoaded(publication.tableOfContents)
            } else {
                epubReaderPresenter.onTableOfContentsLoadFromSpine(publication.spines)
            }
        }
    }

    override fun updateNavigationTableOfContents(tocLinkWrappers: ArrayList<TOCLinkWrapper>) {
        epubChapterAdapter.addItems(tocLinkWrappers)
    }

    override fun toolBarAnimateHide() {
        mIsActionBarVisible = false
        epubReaderAppBar.animate().translationY((-epubReaderAppBar.height).toFloat())
                .setInterpolator(AccelerateInterpolator(2f)).start()
        epubReaderToolbar.animate().translationY((-epubReaderAppBar.height).toFloat())
                .setInterpolator(AccelerateInterpolator(2f)).start()
    }

    override fun toolbarAnimateShow() {
        if (!mIsActionBarVisible) {
            epubReaderAppBar.animate().translationY(0f).setInterpolator(
                    DecelerateInterpolator(2f)).start()
            epubReaderToolbar.animate().translationY(0f).setInterpolator(
                    DecelerateInterpolator(2f)).start()
            mIsActionBarVisible = true
        }
    }

    // ************** page fragment callback *************************
    override fun hideOrShowToolBar() {
        if (mIsActionBarVisible) {
            toolBarAnimateHide()
        } else {
            toolbarAnimateShow()
        }
    }

    override fun hideToolBarIfVisible() {
        if (mIsActionBarVisible) {
            toolBarAnimateHide()
        }
    }

    override fun setPagerToPosition(href: String) {
    }

    override fun setLastWebViewPosition(position: Int) {
        mWebViewScrollPosition = position
    }

    override fun goToChapter(href: String) {
        val href_ = href.substring(href.indexOf(epubTitle + "/") + epubTitle.length + 1)
        for (spine in mSpineReferenceList) {
            if (spine.href.contains(href_)) {
                mChapterPosition = mSpineReferenceList.indexOf(spine)
                directionalPageViewPager.currentItem = mChapterPosition

                break
            }
        }
    }

    fun getChapterPosition(): Int {
        return mChapterPosition
    }

    // *********************** LIFE CYCLE *************************

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        epubReaderPresenter.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        epubReaderPresenter.onDetach()
        super.onDestroy()
    }


}