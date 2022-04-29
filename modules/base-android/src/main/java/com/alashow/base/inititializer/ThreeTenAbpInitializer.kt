/*
 * Copyright (C) 2018, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.base.inititializer

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import javax.inject.Inject

class ThreeTenAbpInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) = AndroidThreeTen.init(application)
}
