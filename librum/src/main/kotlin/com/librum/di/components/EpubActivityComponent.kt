package com.librum.di.components

import com.librum.data.model.event.AnchorIdEvent
import com.librum.di.modules.EpubActivityModule
import com.librum.di.scopes.ActivityScope
import com.librum.ui.pages.EpubPageFragment
import com.librum.ui.reader.EpubReaderActivity
import dagger.Component
import io.reactivex.subjects.PublishSubject
import javax.inject.Named

/**
 * @author lusinabrian on 05/09/17.
 * @Notes activity component
 */
@ActivityScope
@Component(modules = [(EpubActivityModule::class)], dependencies = [(EpubComponent::class)])
interface EpubActivityComponent {

    /***
     * gets the anchor subject for subscription and posting
     */
    @Named("AnchorSubject")
    fun getAnchorSubject(): PublishSubject<AnchorIdEvent>

    fun inject(epubReaderActivity: EpubReaderActivity)

    fun inject(pageFragment: EpubPageFragment)
}