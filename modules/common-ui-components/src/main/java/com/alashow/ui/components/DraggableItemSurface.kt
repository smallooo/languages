/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.ui.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.io.Serializable

data class DraggableItemKey(val any: Any) : Serializable

@Composable
fun DraggableItemSurface(
    offset: Float?,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Vertical,
    content: @Composable () -> Unit,
) {
    val draggingBackground = MaterialTheme.colors.background
    Surface(
        color = offset?.let { draggingBackground } ?: Color.Transparent,
        elevation = offset?.let { 8.dp } ?: 0.dp,
        modifier = modifier
            .zIndex(offset?.let { 1f } ?: 0f)
            .graphicsLayer {
                with(offset ?: 0f) {
                    if (orientation == Orientation.Vertical) {
                        translationY = this
                    } else {
                        translationX = this
                    }
                }
            }
    ) {
        content()
    }
}
