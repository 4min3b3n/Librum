package com.librum.ui.base

import com.librum.data.EpubReaderDataManager
import com.librum.data.io.EpubSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * @author lusinabrian on 05/09/17.
 * @Notes Base reader presenter implementation
 */
abstract class BasePresenterImpl<V : BaseView>
@Inject
constructor(val epubReaderDataManager: EpubReaderDataManager,
            val mCompositeDisposable: CompositeDisposable,
            val mSchedulerProvider: EpubSchedulerProvider) : BasePresenter<V> {

    /**
     * Gets the base view
     * @return [BaseView]
     */
    lateinit var baseView: V
        private set

    override fun onAttach(mBaseView: V) {
        this.baseView = mBaseView
    }

    override fun onDetach() {

    }

    /**
     * Checks if the view has been attached */
    open val isViewAttached: Boolean
        get() = baseView != null

    /**
     * Checks if the view has been attached
     * @throws BaseViewNotAttachedException error that is thrown when the view is not attached.
     * *
     */
    fun checkViewAttached() {
        check(!isViewAttached) { BaseViewNotAttachedException() }
    }

    /**
     * Custom runtime exception that is thrown when an a request of data is made to the presenter before
     * attaching the view.
     */
    class BaseViewNotAttachedException : RuntimeException("Please call Presenter.onAttach(AppBaseView) before requesting data to Presenter")
}