package com.librum.ui.pages

import android.os.Bundle
import android.view.View
import com.librum.data.EpubReaderDataManager
import com.librum.data.io.EpubSchedulerProvider
import com.brck.moja.epubreader.data.model.Highlight
import com.librum.ui.base.BaseReaderPresenterImpl
import com.librum.*
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * @author lusinabrian on 06/09/17.
 * @Notes presenter implementation for page presenter
 */
class EpubPageFragmentPresenterImpl<V : EpubPageFragmentView>
@Inject
constructor(mEpubReaderDataManager: EpubReaderDataManager, mCompositeDisposable: CompositeDisposable, mSchedulerProvider: EpubSchedulerProvider) : EpubPageFragmentPresenter<V>, BaseReaderPresenterImpl<V>(mEpubReaderDataManager, mCompositeDisposable, mSchedulerProvider) {

    override fun onAttach(mBaseView: V) {
        super.onAttach(mBaseView)
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        baseView.receiveBundleForPage(savedInstanceState)
    }

    override fun onInitializeAnimations() {
        baseView.initializeAnimations()
    }

    override fun onInitializeObservableWebView() {
        baseView.initializeWebView()
    }

    override fun onScrollStateChanged(percent: Int) {
        baseView.updatePagesLeft(percent)
    }

    override fun onSetWebViewPosition(position: Int) {
        baseView.setWebViewPosition(position)
    }

    override fun onGetPreviousBookStateWebViewPosition(bookTitle: String): Int {
        val webViewPos = epubReaderDataManager.getPreviousBookStateWebViewPosition(bookTitle)
        baseView.setWebViewPosition(webViewPos)
        return webViewPos
    }

    override fun onHighlightActionItemClicked(mSelectedText: String, actionId: Int, view: View,
                                              isCreated: Boolean) {
        when (actionId) {
            ACTION_ID_HIGHLIGHT_COLOR -> {
                baseView.highlightColorAction(view, isCreated)
            }
            ACTION_ID_SHARE -> {
                baseView.shareAction(mSelectedText)
            }
            ACTION_ID_DELETE -> {
                baseView.removeHighlightAction()
            }
        }
    }

    override fun onHighlightColorsActionItemClicked(actionId: Int, view: View, isCreated: Boolean) {
        when (actionId) {
            ACTION_ID_HIGHLIGHT_YELLOW -> {
                baseView.highlight(Highlight.HighlightStyle.Yellow, isCreated)
            }
            ACTION_ID_HIGHLIGHT_GREEN -> {
                baseView.highlight(Highlight.HighlightStyle.Green, isCreated)
            }
            ACTION_ID_HIGHLIGHT_BLUE -> {
                baseView.highlight(Highlight.HighlightStyle.Blue, isCreated)
            }
            ACTION_ID_HIGHLIGHT_PINK -> {
                baseView.highlight(Highlight.HighlightStyle.Pink, isCreated)
            }
            ACTION_ID_HIGHLIGHT_UNDERLINE -> {
                baseView.highlight(Highlight.HighlightStyle.Underline, isCreated)
            }
        }
    }

    override fun onTextSelectionActionItemClicked(mSelectedText: String, actionId: Int, view: View,
                                                  width: Int, height: Int) {
        when (actionId) {
            ACTION_ID_COPY -> {
                baseView.copyToClipBoardAction(mSelectedText)
            }
            ACTION_ID_SHARE -> {
                baseView.shareAction(mSelectedText)
            }
            ACTION_ID_DEFINE -> {
                baseView.showDefineActionDialog(mSelectedText)
            }
            ACTION_ID_HIGHLIGHT -> {
                baseView.onHighlightView(view, width, height, true)
            }
        }
    }

    // todo: issue which schedulers and updating the web page
    // fixme: android.os.NetworkOnMainThreadException
    override fun onDownloadHtmlWebPage(webViewUrl: String) {
        mCompositeDisposable.add(
                epubReaderDataManager.downloadHtmlWebPage(webViewUrl)
                        .subscribeOn(mSchedulerProvider.newThread())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe({
                            baseView.onReceiveHtml(it.body()?.string()!!)
                        }, {
                            // error
                            error("Download failed with error ${it.message}", it)
                            baseView.onError()
                        })
        )
    }

    override fun onDetach() {
        mCompositeDisposable.dispose()
        super.onDetach()
    }
}