/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package tm.alashow.datmusic.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Keep the splash screen visible for this Activity
       // splashScreen.setKeepOnScreenCondition { true }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DatmusicApp()
        }
    }
}
