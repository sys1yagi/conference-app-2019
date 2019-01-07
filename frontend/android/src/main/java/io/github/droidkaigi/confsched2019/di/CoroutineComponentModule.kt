package io.github.droidkaigi.confsched2019.di

import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.ext.android.AppCoroutineDispatchers
import javax.inject.Singleton

@Module
object CoroutineComponentModule {
    @JvmStatic @Provides @Singleton fun provideAppCoroutineDispatchers(
    ): AppCoroutineDispatchers {
        return AppCoroutineDispatchers()
    }
}
