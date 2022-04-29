/*
 * Copyright (C) 2018, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import com.alashow.base.billing.SubscriptionsInitializer
import com.alashow.base.imageloading.CoilAppInitializer
import com.alashow.base.inititializer.AppInitializers
import com.alashow.base.inititializer.ThreeTenAbpInitializer
import com.alashow.base.inititializer.TimberInitializer
import com.alashow.base.migrator.AppMigrator
import com.alashow.base.util.CoroutineDispatchers
import com.alashow.data.PreferencesStore
import com.alashow.datmusic.data.migrators.AudiosFtsAppMigration
import com.alashow.datmusic.fcm.FcmTokenRegistrator
import com.alashow.datmusic.notifications.NotificationsInitializer
import com.alashow.datmusic.util.RemoteConfigInitializer

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun coroutineDispatchers() = CoroutineDispatchers(
        network = Dispatchers.IO,
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main
    )

    @Provides
    fun appInitializers(
        notifications: NotificationsInitializer,
        timberManager: TimberInitializer,
        threeTen: ThreeTenAbpInitializer,
        coilAppInitializer: CoilAppInitializer,
        fcmTokenRegistrator: FcmTokenRegistrator,
        remoteConfigInitializer: RemoteConfigInitializer,
        subscriptionsInitializer: SubscriptionsInitializer,
    ): AppInitializers {
        return AppInitializers(
            notifications,
            timberManager,
            threeTen,
            coilAppInitializer,
            fcmTokenRegistrator,
            remoteConfigInitializer,
            subscriptionsInitializer,
        )
    }

    @Provides
    @Singleton
    fun appMigrator(
        dispatchers: CoroutineDispatchers,
        preferencesStore: PreferencesStore,
        audiosFtsAppMigration: AudiosFtsAppMigration
    ) = AppMigrator(dispatchers, preferencesStore, setOf(audiosFtsAppMigration))
}
