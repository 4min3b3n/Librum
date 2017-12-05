package com.librum.ui.pages

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.*
import android.widget.TextView
import com.librum.*
import com.librum.R
import com.librum.data.model.Highlight
import com.librum.data.model.event.WebViewPosition
import com.librum.data.model.quickaction.ActionItem
import com.librum.data.model.quickaction.QuickAction
import com.librum.ui.base.BaseFragment
import com.librum.ui.base.PageFragmentCallback
import com.librum.ui.reader.EpubReaderActivity
import com.librum.ui.widgets.ObservableWebView
import com.librum.ui.widgets.VerticalSeekbar
import com.librum.utils.convertDpToPixel
import com.librum.utils.copyToClipboard
import com.librum.utils.getHtmlContent
import com.librum.webviewmarker.TextSelectionSupport
import kotlinx.android.synthetic.main.fragment_epubpage.view.*
import org.jetbrains.anko.debug
import org.jetbrains.anko.error
import org.jetbrains.anko.share
import org.jetbrains.anko.toast
import org.readium.r2_streamer.model.publication.link.Link
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * @author lusinabrian on 05/09/17.
 * @Notes EpubPageFragment which will be a single page on the epub
 */
class EpubPageFragment : BaseFragment(), EpubPageFragmentView, ObservableWebView.ScrollListener {

    private var mPos: Int = 0
    private var mPosition = -1
    private lateinit var mBookTitle: String
    private lateinit var mEpubFileName: String
    private lateinit var spineItem: Link
    private lateinit var highlightStyle: String
    private lateinit var mScrollSeekbar: VerticalSeekbar
    private lateinit var mFadeInAnimation: Animation
    private lateinit var mFadeOutAnimation: Animation
    private lateinit var mObservableWebView: ObservableWebView
    private lateinit var mPagesLeftTextView: TextView
    private lateinit var mMinutesLeftTextView: TextView

    private var mIsPageReloaded: Boolean = false
    private var mTotalMinutes: Int = 0
    private var mScrollY: Int = 0
    private var mAnchorId: String? = null
    private val mWebViewPosition: WebViewPosition? = null
    private var mLastWebViewScrollPos: Int = 0
    private var hasMediaOverlay = false
    private var mSelectedText: String? = null
    private lateinit var mTextSelectionSupport: TextSelectionSupport
    private var mHtmlString: String? = null
    private var mHighlightMap: Map<String, String>? = null

    private lateinit var mActivityCallback: PageFragmentCallback

    lateinit var mEpubPageJsInterface: EpubPageJSInterface

    @Inject
    lateinit var epubPageFragmentPresenter: EpubPageFragmentPresenter<EpubPageFragmentView>

    companion object {
        fun newInstance(position: Int, bookTitle: String, spineRef: Link): EpubPageFragment {
            val fragment = EpubPageFragment()
            val args = Bundle()
            args.putInt(KEY_FRAGMENT_PAGE_POSITION, position)
            args.putString(KEY_FRAGMENT_PAGE_BOOK_TITLE, bookTitle)
            args.putSerializable(SPINE_ITEM, spineRef)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jumpToAnchorPoint()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_epubpage, container, false)

        epubActivityComponent.inject(this)

        epubPageFragmentPresenter.onAttach(this)

        epubPageFragmentPresenter.onViewCreated(savedInstanceState)

        setUp(rootView)

        if (spineItem != null) {
            if (spineItem.properties.contains("media-overlay")) {
                //mediaController = MediaController(activity, MediaController.MediaType.SMIL, this)
                hasMediaOverlay = true
            } else {
                // mediaController = MediaController(activity, MediaController.MediaType.TTS, this)
                // mediaController.setTextToSpeech(activity)
            }
        }
        highlightStyle = Highlight.HighlightStyle.classForStyle(Highlight.HighlightStyle.Normal)

        if (activity is PageFragmentCallback) {
            mActivityCallback = activity as PageFragmentCallback
        }

        return rootView
    }

    override fun receiveBundleForPage(savedInstanceState: Bundle?) {
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FRAGMENT_PAGE_POSITION)
                && savedInstanceState.containsKey(KEY_FRAGMENT_PAGE_BOOK_TITLE)) {
            mPosition = savedInstanceState.getInt(KEY_FRAGMENT_PAGE_POSITION)
            mBookTitle = savedInstanceState.getString(KEY_FRAGMENT_PAGE_BOOK_TITLE)
            mEpubFileName = savedInstanceState.getString(KEY_FRAGMENT_EPUB_FILE_NAME)
            spineItem = savedInstanceState.getSerializable(SPINE_ITEM) as Link
        } else {
            mPosition = arguments.getInt(KEY_FRAGMENT_PAGE_POSITION)
            mBookTitle = arguments.getString(KEY_FRAGMENT_PAGE_BOOK_TITLE)
            mEpubFileName = arguments.getString(KEY_FRAGMENT_EPUB_FILE_NAME, "")
            spineItem = arguments.getSerializable(SPINE_ITEM) as Link
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_FRAGMENT_PAGE_POSITION, mPosition)
        outState.putString(KEY_FRAGMENT_PAGE_BOOK_TITLE, mBookTitle)
        outState.putString(KEY_FRAGMENT_EPUB_FILE_NAME, mEpubFileName)
        outState.putSerializable(SPINE_ITEM, spineItem)
    }

    override fun setUp(view: View) {
        with(view) {
            // initialize the views
            mScrollSeekbar = epubPageScrollSeekbar
            mObservableWebView = epubPageObservableWebView
            mPagesLeftTextView = epubPagesLeft
            mMinutesLeftTextView = epubPageMinutesLeft

            mScrollSeekbar.progressDrawable.setColorFilter(ContextCompat.getColor(
                    activity, R.color.yellow_scrollbar_color), PorterDuff.Mode.SRC_IN)
        }

        // initialize animations
        epubPageFragmentPresenter.onInitializeAnimations()

        // initialize the web view
        epubPageFragmentPresenter.onInitializeObservableWebView()
    }

    override fun initializeAnimations() {
        mFadeInAnimation = AnimationUtils.loadAnimation(activity, R.anim.fadein)
        mFadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                mScrollSeekbar.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                if (mScrollSeekbar.visibility == View.VISIBLE) {
                    mScrollSeekbar.startAnimation(mFadeOutAnimation)
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        mFadeOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.fadeout)
        mFadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                mScrollSeekbar.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    fun fadeInSeekBarIfInvisible() {
        if (mScrollSeekbar.visibility == View.INVISIBLE || mScrollSeekbar.visibility == View.GONE) {
            mScrollSeekbar.startAnimation(mFadeInAnimation)
        }
    }

    override fun initializeWebView() {
        mObservableWebView.setFragment(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        mObservableWebView.viewTreeObserver.addOnGlobalLayoutListener({
            val height = Math.floor((mObservableWebView.contentHeight * mObservableWebView.scale)
                    .toDouble()).toInt()
            val webViewHeight = mObservableWebView.measuredHeight
            mScrollSeekbar.maximum = height - webViewHeight
        })

        mObservableWebView.settings.javaScriptEnabled = true
        mObservableWebView.isVerticalScrollBarEnabled = false
        mObservableWebView.settings.allowFileAccess = true

        mObservableWebView.isHorizontalScrollBarEnabled = false

        // initialize JS interface
        mEpubPageJsInterface = EpubPageJSInterface(activity, mObservableWebView,
                mHighlightMap, mBookTitle, mPosition)

        mObservableWebView.addJavascriptInterface(mEpubPageJsInterface, EPUB_JS_INTERFACE_NAME)
        mObservableWebView.setScrollListener(this)

        // set the web view client and web chrome clients
        mObservableWebView.webViewClient = webViewClient
        mObservableWebView.webChromeClient = webChromeClient

        mTextSelectionSupport = TextSelectionSupport.support(activity, mObservableWebView)
        mTextSelectionSupport.setSelectionListener(object : TextSelectionSupport.SelectionListener {
            override fun startSelection() {}

            override fun selectionChanged(text: String) {
                mSelectedText = text
                activity.runOnUiThread {
                    mObservableWebView.loadUrl(
                            "javascript:alert(getRectForSelectedText())")
                }
            }

            override fun endSelection() {}
        })

        mObservableWebView.settings.defaultTextEncodingName = "utf-8"

        // TODO: load from presenter
        // HtmlTask(this).execute("$LOCALHOST$mBookTitle/${spineItem.href}")
        epubPageFragmentPresenter.onDownloadHtmlWebPage("$mBookTitle/${spineItem.href}")
    }

    override fun onScrollChange(percent: Int) {
        if (mObservableWebView.scrollY != 0) {
            mScrollY = mObservableWebView.scrollY
            (activity as EpubReaderActivity).setLastWebViewPosition(mScrollY)
        }
        mScrollSeekbar.setProgressAndThumb(percent)

        epubPageFragmentPresenter.onScrollStateChanged(percent)
    }

    override fun updatePagesLeft(percent: Int) {
        try {
            val currentPage = (Math.ceil(percent.toDouble() / mObservableWebView.webViewHeight) + 1).toInt()
            val totalPages = Math.ceil(mObservableWebView.contentHeightVal.toDouble() / mObservableWebView.webViewHeight).toInt()
            val pagesRemaining = totalPages - currentPage
            val pagesRemainingStrFormat = if (pagesRemaining > 1)
                getString(R.string.pages_left)
            else
                getString(R.string.page_left)
            val pagesRemainingStr = String.format(Locale.US,
                    pagesRemainingStrFormat, pagesRemaining)

            val minutesRemaining = Math.ceil((pagesRemaining * mTotalMinutes).toDouble() / totalPages).toInt()
            val minutesRemainingStr: String
            minutesRemainingStr = when {
                minutesRemaining > 1 ->
                    String.format(Locale.US, getString(R.string.minutes_left), minutesRemaining)
                minutesRemaining == 1 ->
                    String.format(Locale.US, getString(R.string.minute_left), minutesRemaining)
                else -> getString(R.string.less_than_minute)
            }

            mMinutesLeftTextView.text = minutesRemainingStr
            mPagesLeftTextView.text = pagesRemainingStr
        } catch (exp: ArithmeticException) {
            error("Division error ${exp.message}", exp)
        }
    }

    override fun setWebViewPosition(position: Int) {

    }

    /**
     * Web view client for the web page currently in view
     * */
    private val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            if (isAdded) {
                if (mAnchorId != null) {
                    view.loadUrl("javascript:document.getElementById(\"$mAnchorId\").scrollIntoView()")
                }
                view.loadUrl("javascript:alert(getReadingTime())")
                if (!hasMediaOverlay) {
                    view.loadUrl("javascript:alert(wrappingSentencesWithinPTags())")
                }
                view.loadUrl(String.format(getString(R.string.setmediaoverlaystyle),
                        Highlight.HighlightStyle.classForStyle(Highlight.HighlightStyle.Normal))
                )
                when {
                    mWebViewPosition != null -> {
                        epubPageFragmentPresenter.onSetWebViewPosition(mWebViewPosition.webViewPos)
                    }
                    isCurrentFragment() -> {
                        epubPageFragmentPresenter.onSetWebViewPosition(
                                epubPageFragmentPresenter.onGetPreviousBookStateWebViewPosition(mBookTitle)
                        )
                    }
                    mIsPageReloaded -> {
                        epubPageFragmentPresenter.onSetWebViewPosition(mLastWebViewScrollPos)
                        mIsPageReloaded = false
                    }
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
            val uri = request.url
            val url = uri.toString()
            if (!url.isEmpty() && url.isNotEmpty()) {
                if (uri.scheme.startsWith("highlight")) {
                    val pattern = Pattern.compile(getString(R.string.pattern))
                    try {
                        val htmlDecode = URLDecoder.decode(url, "UTF-8")
                        val matcher = pattern.matcher(htmlDecode.substring(12))
                        if (matcher.matches()) {
                            val left = matcher.group(1).toDouble()
                            val top = matcher.group(2).toDouble()
                            val width = matcher.group(3).toDouble()
                            val height = matcher.group(4).toDouble()
                            onHighlightAction(
                                    convertDpToPixel(left.toFloat(), activity).toInt(),
                                    convertDpToPixel(top.toFloat(), context!!).toInt(),
                                    convertDpToPixel(width.toFloat(), activity).toInt(),
                                    convertDpToPixel(height.toFloat(), activity).toInt()
                            )
                        }
                    } catch (uee: UnsupportedEncodingException) {
                        error("Error ${uee.message}", uee.cause)
                    }

                } else {
                    if (url.contains("storage")) {
                        mActivityCallback.setPagerToPosition(url)
                    } else if (url.endsWith(".xhtml") || url.endsWith(".html")) {
                        mActivityCallback.goToChapter(url)
                    } else {
                        // Otherwise, give the default behavior (open in browser)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                }
            }
            return true
        }
    }

    override fun onHighlightAction(x: Int, y: Int, width: Int, height: Int) {
        val view = View(context)
        view.layoutParams = ViewGroup.LayoutParams(width, height)
        view.setBackgroundColor(Color.TRANSPARENT)
        view.x = x.toFloat()
        view.y = y.toFloat()
        onHighlightView(view, width, height, false)
    }

    /**
     * Web chrome client for this web page
     * */
    private val webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, progress: Int) {
            if (view.progress == 100) {
                mObservableWebView.postDelayed({
                    debug("Scroll Y $mScrollY")
                    mObservableWebView.scrollTo(0, mScrollY)
                }, 100)
            }
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            if (isVisible) {
                if (TextUtils.isDigitsOnly(message)) {
                    mTotalMinutes = Integer.parseInt(message)
                } else {
                    val pattern = Pattern.compile(getString(R.string.pattern))
                    val matcher = pattern.matcher(message)
                    if (matcher.matches()) {
                        val left = matcher.group(1).toDouble()
                        val top = matcher.group(2).toDouble()
                        val width = matcher.group(3).toDouble()
                        val height = matcher.group(4).toDouble()
                        onShowTextSelectionMenu(
                                convertDpToPixel(left.toFloat(), activity).toInt(),
                                convertDpToPixel(top.toFloat(), activity).toInt(),
                                convertDpToPixel(width.toFloat(), activity).toInt(),
                                convertDpToPixel(height.toFloat(), activity).toInt()
                        )
                    } else {
                        // to handle TTS playback when highlight is deleted.
                        val p = Pattern.compile(
                                "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
                        if (!p.matcher(message).matches() && message != "undefined") {
                            if (isCurrentFragment()) {
                                // mediaController.speakAudio(message)
                            }
                        }
                    }
                }
                result.confirm()
            }
            return true
        }
    }

    private fun onShowTextSelectionMenu(x: Int, y: Int, width: Int, height: Int) {
        val root = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val view = View(activity)
        view.layoutParams = ViewGroup.LayoutParams(width, height)
        view.setBackgroundColor(Color.TRANSPARENT)

        root.addView(view)

        view.x = x.toFloat()
        view.y = y.toFloat()
        val quickAction = QuickAction(activity, QuickAction.HORIZONTAL)
        with(quickAction) {
            addActionItem(ActionItem(ACTION_ID_COPY, activity.getString(R.string.copy)))
            //addActionItem(ActionItem(ACTION_ID_HIGHLIGHT, activity.getString(R.string.highlight)))

            if (!mSelectedText?.trim({ it <= ' ' })?.contains(" ")!!) {
                addActionItem(ActionItem(ACTION_ID_DEFINE, activity.getString(R.string.define)))
            }

            addActionItem(ActionItem(ACTION_ID_SHARE, activity.getString(R.string.share)))

            setOnActionItemClickListener { _, _, actionId ->
                dismiss()
                root.removeView(view)
                epubPageFragmentPresenter.onTextSelectionActionItemClicked(
                        mSelectedText!!, actionId, view, width, height)
            }

            show(view, width, height)
        }
    }

    override fun onHighlightView(view: View, width: Int, height: Int, isCreated: Boolean) {
        val root = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val parent = try {
            view.parent as ViewGroup
        } catch (tce: TypeCastException) {
            View(activity) as ViewGroup
        }
        if (parent == null) {
            root.addView(view)
        } else {
            val index = parent.indexOfChild(view)
            parent.removeView(view)
            parent.addView(view, index)
        }

        val quickAction = QuickAction(activity, QuickAction.HORIZONTAL)
        with(quickAction) {
            //            addActionItem(ActionItem(ACTION_ID_HIGHLIGHT_COLOR, ContextCompat.getDrawable(activity,
//                    R.drawable.colors_marker)))
            addActionItem(ActionItem(ACTION_ID_DELETE, icon =
            ContextCompat.getDrawable(context!!, R.drawable.ic_action_discard)))
            addActionItem(ActionItem(ACTION_ID_SHARE, icon =
            ContextCompat.getDrawable(context!!, R.drawable.ic_action_share)))
            setOnActionItemClickListener { _, _, actionId ->
                dismiss()
                root.removeView(view)
                epubPageFragmentPresenter.onHighlightActionItemClicked(mSelectedText!!, actionId, view, isCreated)
            }
            show(view, width, height)
        }
    }

    override fun highlightColorAction(view: View, isCreated: Boolean) {
        val root = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val parent = view.parent as ViewGroup
        if (parent == null) {
            root.addView(view)
        } else {
            val index = parent.indexOfChild(view)
            parent.removeView(view)
            parent.addView(view, index)
        }

        val quickAction = QuickAction(activity, QuickAction.HORIZONTAL)

        with(quickAction) {
            addActionItem(ActionItem(ACTION_ID_HIGHLIGHT_YELLOW, icon = ContextCompat.getDrawable(activity,
                    R.drawable.ic_yellow_marker)))
            addActionItem(ActionItem(ACTION_ID_HIGHLIGHT_GREEN, icon = ContextCompat.getDrawable(activity,
                    R.drawable.ic_green_marker)))
            addActionItem(ActionItem(ACTION_ID_HIGHLIGHT_BLUE, icon = ContextCompat.getDrawable(activity,
                    R.drawable.ic_blue_marker)))
            addActionItem(ActionItem(ACTION_ID_HIGHLIGHT_PINK, icon = ContextCompat.getDrawable(activity,
                    R.drawable.ic_pink_marker)))
            addActionItem(ActionItem(ACTION_ID_HIGHLIGHT_UNDERLINE, icon = ContextCompat.getDrawable(activity,
                    R.drawable.ic_underline_marker)))

            setOnActionItemClickListener { _, _, actionId ->
                dismiss()
                root.removeView(view)
                epubPageFragmentPresenter.onHighlightColorsActionItemClicked(actionId, view, isCreated)
            }

            show(view)
        }
    }

    override fun highlight(style: Highlight.HighlightStyle, isCreated: Boolean) {
        if (isCreated) {
            mObservableWebView.loadUrl(String.format(getString(R.string.getHighlightString),
                    Highlight.HighlightStyle.classForStyle(style)))
        } else {
            mObservableWebView.loadUrl(String.format(getString(R.string.sethighlightstyle),
                    Highlight.HighlightStyle.classForStyle(style)))
        }
    }

    override fun removeHighlightAction() {
        mObservableWebView.loadUrl("javascript:alert(removeThisHighlight())")
    }

    override fun shareAction(mSelectedText: String) {
        context.share(mSelectedText)
//        val sendIntent = Intent()
//        sendIntent.action = Intent.ACTION_SEND
//        sendIntent.putExtra(Intent.EXTRA_TEXT, mSelectedText)
//        sendIntent.type = "text/plain"
//        context.startActivity(Intent.createChooser(sendIntent,
//                context.resources.getText(R.string.send_to)))
    }

    override fun showDefineActionDialog(mSelectedText: String) {
//        val dictionaryFragment = DictionaryFragment()
//        val b = Bundle()
//        b.putString(SELECTED_WORD, mSelectedText)
//        dictionaryFragment.arguments = b
//        dictionaryFragment.show(fragmentManager, DictionaryFragment::class.java.name)
    }

    override fun copyToClipBoardAction(mSelectedText: String) {
        copyToClipboard(activity, mSelectedText)
        activity.toast(getString(R.string.copied))
    }

    /**
     * checks if this is the current fragment on display
     * @return [Boolean]
     * */
    private fun isCurrentFragment(): Boolean {
        return isAdded && (activity as EpubReaderActivity).getChapterPosition() == mPos
    }

    fun setFragmentPos(pos: Int) {
        mPos = pos
    }

    override fun onReceiveHtml(html: String) {
        if (isAdded) {
            mHtmlString = html
            setHtmlToObservableWebView(false)
        }
    }

    override fun setHtmlToObservableWebView(reloaded: Boolean) {
        if (spineItem != null) {
            val ref = spineItem.href
            if (!reloaded) {
                if (spineItem.properties.contains("media-overlay")) {
                    //mediaController.setSMILItems(SMILParser.parseSMIL(mHtmlString))
//                    mediaController.setUpMediaPlayer(spineItem.mediaOverlay,
//                            spineItem.mediaOverlay.getAudioPath(spineItem.href), mBookTitle)
                }
            }
            val path = ref.substring(0, ref.lastIndexOf("/"))
            mObservableWebView.loadDataWithBaseURL("${LOCALHOST}$mBookTitle/$path/",
                    getHtmlContent(activity, mHtmlString!!, mBookTitle),
                    "text/html",
                    "UTF-8", null)
        }
    }

    /**
     * [EVENT BUS FUNCTION] Function triggered from [EpubReaderActivity.onChapterClicked]
     * when any item in toc clicked.
     */
    override fun jumpToAnchorPoint() {
        if (isAdded) {
            epubActivityComponent.getAnchorSubject().subscribe({
                val href = it.href
                if (href.indexOf('#') != -1) {
                    if (spineItem.href == href.substring(0, href.lastIndexOf('#'))) {
                        mAnchorId = href.substring(href.lastIndexOf('#') + 1)
                    }
                }
            })
        }
    }

    override fun onError() {
        super.onError()
    }

    override fun onDestroyView() {
        mFadeInAnimation.setAnimationListener(null)
        mFadeOutAnimation.setAnimationListener(null)
        super.onDestroyView()
    }

    override fun onDestroy() {
        epubPageFragmentPresenter.onDetach()
        if (mObservableWebView != null) {
            mObservableWebView.destroy()
        }
        super.onDestroy()
    }
}