/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.base.billing

import android.app.Application
import com.qonversion.android.sdk.QUserProperties
import com.qonversion.android.sdk.Qonversion
import javax.inject.Inject
import com.alashow.base.inititializer.AppInitializer
import com.alashow.base.util.extensions.androidId

class SubscriptionsInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        if (Subscriptions.KEY.isNotBlank()) {
            Qonversion.launch(application, Subscriptions.KEY, false)
            Qonversion.setProperty(QUserProperties.CustomUserId, application.androidId())
        }
    }
}
