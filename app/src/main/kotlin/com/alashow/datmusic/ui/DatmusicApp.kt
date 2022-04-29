/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.alashow.common.compose.LocalAnalytics
import com.alashow.common.compose.LocalScaffoldState
import com.alashow.common.compose.rememberFlowWithLifecycle
import com.alashow.datmusic.BuildConfig
import com.alashow.datmusic.ui.audios.LocalAudioActionHandler
import com.alashow.datmusic.ui.audios.audioActionHandler
import com.alashow.datmusic.ui.downloader.DownloaderHost
import com.alashow.datmusic.ui.downloads.audio.LocalAudioDownloadItemActionHandler
import com.alashow.datmusic.ui.downloads.audio.audioDownloadItemActionHandler
import com.alashow.datmusic.ui.home.Home
import com.alashow.datmusic.ui.playback.PlaybackHost
import com.alashow.datmusic.ui.settings.LocalAppVersion
import com.alashow.datmusic.ui.snackbar.SnackbarMessagesListener
import com.alashow.navigation.NavigatorHost
import com.alashow.navigation.rememberBottomSheetNavigator
import com.alashow.ui.ThemeViewModel
import com.alashow.ui.theme.AppTheme

@OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
@Composable
fun DatmusicApp(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberAnimatedNavController(),
    analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(LocalContext.current),
) {
    CompositionLocalProvider(
        LocalScaffoldState provides scaffoldState,
        LocalAnalytics provides analytics,
        LocalAppVersion provides BuildConfig.VERSION_NAME
    ) {
        ProvideWindowInsets(consumeWindowInsets = false) {
            DatmusicCore {
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                navController.navigatorProvider += bottomSheetNavigator
                ModalBottomSheetLayout(bottomSheetNavigator) {
                    Home(navController)

                }
            }
        }
    }
}

@Composable
private fun DatmusicCore(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    SnackbarMessagesListener()
    val themeState by rememberFlowWithLifecycle(themeViewModel.themeState)
    AppTheme(themeState) {
        NavigatorHost {
            DownloaderHost {
                PlaybackHost {
                    DatmusicActionHandlers {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
private fun DatmusicActionHandlers(content: @Composable () -> Unit) {
    val audioActionHandler = audioActionHandler()
    val audioDownloadItemActionHandler = audioDownloadItemActionHandler()
    CompositionLocalProvider(
        LocalAudioActionHandler provides audioActionHandler,
        LocalAudioDownloadItemActionHandler provides audioDownloadItemActionHandler
    ) {
        content()
    }
}
