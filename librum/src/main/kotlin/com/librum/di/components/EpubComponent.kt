package com.librum.di.components

import android.app.Application
import com.librum.data.EpubReaderDataManager
import com.librum.di.modules.EpubDatabaseModule
import com.librum.di.modules.EpubModule
import com.librum.di.modules.EpubServerModule
import dagger.Component
import javax.inject.Singleton

/**
 * @author lusinabrian on 05/09/17.
 * @Notes component to inject dependencies into classes
 */
@Singleton
@Component(modules = arrayOf(EpubModule::class, EpubDatabaseModule::class,
        EpubServerModule::class))
interface EpubComponent {
    fun application(): Application

    val epubReaderDataManager: EpubReaderDataManager
}