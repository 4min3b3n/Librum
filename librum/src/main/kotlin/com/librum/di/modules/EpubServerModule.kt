package com.librum.di.modules

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.librum.utils.LOCALHOST
import com.librum.utils.PORT_NUMBER
import com.librum.data.server.EpubRetrofitService
import com.librum.data.server.EpubServerHelper
import com.librum.data.server.EpubServerHelperImpl
import com.librum.di.qualifiers.AppContextQualifier
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.readium.r2_streamer.server.EpubServer
import org.readium.r2_streamer.server.EpubServerSingleton
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author lusinabrian on 06/09/17.
 * @Notes epub server module
 */
@Module
class EpubServerModule {

    @Provides
    @Singleton
    fun provideEpubServerHelper(epubServerHelper: EpubServerHelperImpl): EpubServerHelper {
        return epubServerHelper
    }

    @Provides
    @Named("EpubServerInstance")
    fun provideEpubServerInstance(): EpubServer {
        return EpubServerSingleton.getEpubServerInstance(PORT_NUMBER)
    }

    @Provides
    @Singleton
    fun provideEpubGson(): Gson {
        return GsonBuilder().setLenient().setPrettyPrinting().create()
    }

    @Provides
    @Singleton
    @Named("epubBaseUrl")
    fun provideEpubBaseUrl(): String {
        return LOCALHOST
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
                .cache(cache)
                .build()
    }

    @Provides
    @Singleton
    fun provideEpubRetrofit(@Named("epubBaseUrl") baseUrl: String, gson: Gson,
                            okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    fun provideEpubOkHttpCache(@AppContextQualifier context: Context): Cache {
        val cacheSize = 30 * 1024 * 1024 // 30 MB
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideEpubRetrofitService(retrofit: Retrofit): EpubRetrofitService {
        return retrofit.create(EpubRetrofitService::class.java)
    }
}