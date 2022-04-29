/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.alashow.base.util.extensions.muteUntil
import com.alashow.ui.components.AppBarNavigationIcon
import com.alashow.ui.components.AppTopBar

open class MediaDetailTopBar {

    @Composable
    open operator fun invoke(
        title: String,
        collapsedProgress: State<Float>,
        onGoBack: () -> Unit,
    ) {
        AppTopBar(
            title = title,
            collapsedProgress = collapsedProgress.value.muteUntil(0.9f),
            navigationIcon = { AppBarNavigationIcon(onClick = onGoBack) },
        )
    }
}
