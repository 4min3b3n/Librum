package com.librum.ui.widgets

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.webkit.WebView
import com.librum.ui.base.PageFragmentCallback
import com.librum.ui.pages.EpubPageFragment
import com.librum.ui.reader.EpubReaderActivity

class ObservableWebView : WebView {

    private var mEpubPageFragment: EpubPageFragment? = null
    private var mActivityCallback: PageFragmentCallback? = null
    private var mDownPosX = 0f
    private var mDownPosY = 0f
    private var mScrollListener: ScrollListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet,
                defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    fun setScrollListener(listener: ScrollListener) {
        mScrollListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mActivityCallback = context as EpubReaderActivity

        val action = event.action
        val MOVE_THRESHOLD_DP = 20 * resources.displayMetrics.density

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mDownPosX = event.x
                mDownPosY = event.y
                mEpubPageFragment!!.fadeInSeekBarIfInvisible()
            }
            MotionEvent.ACTION_UP -> if (Math.abs(event.x - mDownPosX) < MOVE_THRESHOLD_DP || Math.abs(event.y - mDownPosY) < MOVE_THRESHOLD_DP) {
                mActivityCallback!!.hideOrShowToolBar()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        mActivityCallback = context as EpubReaderActivity
        mActivityCallback!!.hideToolBarIfVisible()
        if (mScrollListener != null) {
            mScrollListener!!.onScrollChange(t)
        }
        super.onScrollChanged(l, t, oldl, oldt)
    }

    val contentHeightVal: Int
        get() = Math.floor((this.contentHeight * this.scale).toDouble()).toInt()

    val webViewHeight: Int
        get() = this.measuredHeight

    override fun startActionMode(callback: ActionMode.Callback, type: Int): ActionMode {
        return this.dummyActionMode()
    }

    override fun startActionMode(callback: ActionMode.Callback): ActionMode {
        return this.dummyActionMode()
    }

    private fun dummyActionMode(): ActionMode {
        return object : ActionMode() {
            override fun setTitle(title: CharSequence) {}

            override fun setSubtitle(subtitle: CharSequence) {}

            override fun invalidate() {}

            override fun finish() {}

            override fun getMenu(): Menu? {
                return null
            }

            override fun getTitle(): CharSequence? {
                return null
            }

            override fun setTitle(resId: Int) {}

            override fun getSubtitle(): CharSequence? {
                return null
            }

            override fun setSubtitle(resId: Int) {}

            override fun getCustomView(): View? {
                return null
            }

            override fun setCustomView(view: View) {}

            override fun getMenuInflater(): MenuInflater? {
                return null
            }
        }
    }

    fun setFragment(epubPageFragment: EpubPageFragment) {
        mEpubPageFragment = epubPageFragment
    }

    interface ScrollListener {

        fun onScrollChange(percent: Int)
    }

}
