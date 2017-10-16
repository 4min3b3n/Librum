package com.librum.di.modules

import com.brck.moja.base.di.modules.BaseAppModule
import com.librum.EPUB_READER_PREF_FILE_NAME
import com.librum.data.EpubReaderDataManager
import com.librum.data.EpubReaderDataManagerImpl
import com.librum.data.files.EpubFileHelper
import com.librum.data.files.EpubFileHelperImpl
import com.librum.data.prefs.EpubReaderPrefs
import com.librum.data.prefs.EpubReaderPrefsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author lusinabrian on 05/09/17.
 * @Notes Module for epub reader
 */
@Module(includes = arrayOf(BaseAppModule::class))
class EpubModule {

    @Provides
    @Singleton
    fun provideEpubDataManager(epubReaderDataManager: EpubReaderDataManagerImpl): EpubReaderDataManager {
        return epubReaderDataManager
    }

    @Provides
    @Singleton
    fun provideEpubFileHelper(epubFileHelper: EpubFileHelperImpl): EpubFileHelper {
        return epubFileHelper
    }

    @Provides
    @Named("EpubPrefs")
    fun providePreferenceName(): String {
        return EPUB_READER_PREF_FILE_NAME
    }

    @Provides
    @Singleton
    fun provideEpubReaderPrefs(epubReaderPrefs: EpubReaderPrefsImpl): EpubReaderPrefs {
        return epubReaderPrefs
    }

}