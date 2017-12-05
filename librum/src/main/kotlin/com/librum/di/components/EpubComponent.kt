package com.librum.di.components

import android.app.Application
import android.content.Context
import com.librum.data.EpubReaderDataManager
import com.librum.di.modules.EpubDatabaseModule
import com.librum.di.modules.EpubModule
import com.librum.di.modules.EpubServerModule
import com.librum.di.qualifiers.AppContextQualifier
import dagger.Component
import dagger.Provides
import javax.inject.Singleton

/**
 * @author lusinabrian on 05/09/17.
 * @Notes component to inject dependencies into classes
 */
@Singleton
@Component(modules = [(EpubModule::class), (EpubDatabaseModule::class), (EpubServerModule::class)])
interface EpubComponent {

    @AppContextQualifier
    fun context(): Context

    fun application(): Application

    val epubReaderDataManager: EpubReaderDataManager
}