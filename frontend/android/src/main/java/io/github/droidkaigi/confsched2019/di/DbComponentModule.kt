package io.github.droidkaigi.confsched2019.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.db.DbComponent
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import javax.inject.Singleton

@Module
object DbComponentModule {
    @JvmStatic @Provides @Singleton fun provideItemStore(
        application: Application,
        appCoroutineDispatchers: AppCoroutineDispatchers
    ): SessionDatabase {
        return DbComponent.builder()
            .context(application)
            .coroutineContext(appCoroutineDispatchers.IO)
            .filename("droidkaigi.db")
            .build()
            .sessionDatabase()
    }

    @JvmStatic @Provides @Singleton fun provideSponsorStore(
        application: Application,
        appCoroutineDispatchers: AppCoroutineDispatchers
    ): SponsorDatabase {
        return DbComponent.builder()
            .context(application)
            .coroutineContext(appCoroutineDispatchers.IO)
            .filename("droidkaigi.db")
            .build()
            .sponsorDatabase()
    }
}
