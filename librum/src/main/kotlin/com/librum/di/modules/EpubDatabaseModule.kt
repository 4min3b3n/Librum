package com.librum.di.modules

import com.librum.EPUB_READER_DATABASE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * @author lusinabrian on 05/09/17.
 * @Notes module for database for epubs
 */
@Module
class EpubDatabaseModule {

    @Provides
    @Named("EpubDatabase")
    fun provideEpubDatabaseName(): String {
        return EPUB_READER_DATABASE_NAME
    }
}