/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.ui.Scaffold
import com.alashow.common.compose.LocalPlaybackConnection
import com.alashow.common.compose.LocalScaffoldState
import com.alashow.common.compose.rememberFlowWithLifecycle
import com.alashow.datmusic.playback.PlaybackConnection
import com.alashow.datmusic.playback.isActive
import com.alashow.datmusic.ui.AppNavigation
import com.alashow.datmusic.ui.currentScreenAsState
import com.alashow.datmusic.ui.hostNavGraph
import com.alashow.datmusic.ui.playback.PlaybackMiniControls
import com.alashow.navigation.screens.RootScreen
import com.alashow.ui.DismissableSnackbarHost
import com.alashow.ui.isWideLayout
import com.alashow.ui.theme.AppTheme

val HomeBottomNavigationHeight = 56.dp

@Composable
internal fun Home(
    navController: NavHostController,
    scaffoldState: ScaffoldState = LocalScaffoldState.current,
    playbackConnection: PlaybackConnection = LocalPlaybackConnection.current,
) {
    val selectedTab by navController.currentScreenAsState()
    val playbackState by rememberFlowWithLifecycle(playbackConnection.playbackState)
    val nowPlaying by rememberFlowWithLifecycle(playbackConnection.nowPlaying)

    val isPlayerActive = (playbackState to nowPlaying).isActive
    val bottomBarHeight = HomeBottomNavigationHeight * (if (isPlayerActive) 1.15f else 1f)
    BoxWithConstraints {
        val isWideLayout = isWideLayout()
        val maxWidth = maxWidth
        Row(Modifier.fillMaxSize()) {
            if (isWideLayout)
                ResizableHomeNavigationRail(
                    maxWidth = maxWidth,
                    selectedTab = selectedTab,
                    navController = navController
                )
            Scaffold(
                modifier = Modifier.weight(12f),
                scaffoldState = scaffoldState,
                snackbarHost = { DismissableSnackbarHost(it) },
                bottomBar = {
                    if (!isWideLayout)
                        Column {
                            PlaybackMiniControls(
                                modifier = Modifier
                                    .graphicsLayer(translationY = AppTheme.specs.padding.value)
                                    .zIndex(2f)
                            )
                            HomeBottomNavigation(
                                selectedTab = selectedTab,
                                onNavigationSelected = { selected -> navController.selectRootScreen(selected) },
                                playerActive = isPlayerActive,
                                modifier = Modifier.fillMaxWidth(),
                                height = bottomBarHeight
                            )
                        }
                    else Spacer(Modifier.navigationBarsPadding())
                }
            ) {
                AppNavigation(navController = navController)
            }
        }
    }
}

internal fun NavController.selectRootScreen(tab: RootScreen) {
    navigate(tab.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true

        val currentEntry = currentBackStackEntry
        val currentDestination = currentEntry?.destination
        val hostGraphRoute = currentDestination?.hostNavGraph?.route
        val isReselected = hostGraphRoute == tab.route
        val isRootReselected = currentDestination?.route == tab.startScreen.createRoute()

        if (isReselected && !isRootReselected) {
            navigateUp()
        }
    }
}
