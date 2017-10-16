package com.librum.ui.base

import com.brck.moja.base.ui.base.BasePresenterImpl
import com.librum.data.EpubReaderDataManager
import com.librum.data.io.EpubSchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * @author lusinabrian on 05/09/17.
 * @Notes Base reader presenter implementation
 */
open class BaseReaderPresenterImpl<V : BaseReaderView>
@Inject
constructor(val epubReaderDataManager: EpubReaderDataManager,
            val mCompositeDisposable: CompositeDisposable,
            val mSchedulerProvider: EpubSchedulerProvider)
    : BaseReaderPresenter<V>, BasePresenterImpl<V>(mCompositeDisposable) {

}