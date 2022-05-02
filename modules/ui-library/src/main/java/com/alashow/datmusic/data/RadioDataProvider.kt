package com.alashow.datmusic.data

import androidx.compose.ui.graphics.Color

object RadioDataProvider {
    fun radioSurfaceGradient(isDark: Boolean) =
        if (isDark) listOf(Color(0xFF2A2A2A), Color.Black) else listOf(Color.White, Color.LightGray)
}
