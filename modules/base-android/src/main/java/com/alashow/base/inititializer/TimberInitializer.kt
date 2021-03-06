/*
 * Copyright (C) 2018, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.base.inititializer

import android.app.Application
import javax.inject.Inject
import timber.log.Timber
import com.alashow.baseAndroid.BuildConfig

class TimberInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
