package com.librum.di.modules

import android.support.v7.app.AppCompatActivity
import com.librum.data.io.EpubSchedulerProvider
import com.librum.data.io.EpubSchedulerProviderImpl
import com.librum.data.model.event.AnchorIdEvent
import com.librum.di.scopes.ActivityScope
import com.librum.ui.pages.EpubPageFragmentPresenter
import com.librum.ui.pages.EpubPageFragmentPresenterImpl
import com.librum.ui.pages.EpubPageFragmentView
import com.librum.ui.pages.PageFragmentAdapter
import com.librum.ui.reader.EpubReaderChapterAdapter
import com.librum.ui.reader.EpubReaderPresenter
import com.librum.ui.reader.EpubReaderPresenterImpl
import com.librum.ui.reader.EpubReaderView
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import javax.inject.Named

/**
 * @author lusinabrian on 05/09/17.
 * @Notes activity module for the epub
 */
@Module(includes = arrayOf(BaseActivityModule::class))
class EpubActivityModule {

    /**
     * This will be used to publish subjects when a chapter is selected
     * which will enable the epub fragment to jump to the correct webview position
     * */
    @Provides
    @ActivityScope
    @Named("AnchorSubject")
    fun provideAnchorSubject(): PublishSubject<AnchorIdEvent> {
        return PublishSubject.create()
    }

    @Provides
    fun provideEpubSchedulerProvider(): EpubSchedulerProvider {
        return EpubSchedulerProviderImpl()
    }

    @Provides
    @ActivityScope
    fun provideEpubReaderPresenter(epubReaderPresenter: EpubReaderPresenterImpl<EpubReaderView>): EpubReaderPresenter<EpubReaderView> {
        return epubReaderPresenter
    }

    @Provides
    fun provideEpubReaderChapterAdapter(): EpubReaderChapterAdapter {
        return EpubReaderChapterAdapter(ArrayList())
    }

    @Provides
    @ActivityScope
    fun providePageFragmentPresenter(epubPageFragmentPresenter: EpubPageFragmentPresenterImpl<EpubPageFragmentView>): EpubPageFragmentPresenter<EpubPageFragmentView> {
        return epubPageFragmentPresenter
    }

    @Provides
    fun providePageFragmentAdapter(appCompatActivity: AppCompatActivity): PageFragmentAdapter {
        return PageFragmentAdapter(appCompatActivity.supportFragmentManager,
                listOf(), "")
    }

}