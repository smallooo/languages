/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.util

import android.app.Application
import javax.inject.Inject
import com.alashow.base.inititializer.AppInitializer
import com.alashow.data.RemoteConfig

class RemoteConfigInitializer @Inject constructor(remoteConfig: RemoteConfig) : AppInitializer {
    override fun init(application: Application) {}
}
