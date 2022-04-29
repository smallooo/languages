package com.alashow.datmusic.ui.start

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alashow.base.ui.ColorPalettePreference
import com.alashow.base.ui.ThemeState

import com.alashow.navigation.LocalNavigator
import com.alashow.navigation.Navigator
import com.alashow.ui.theme.AppTheme
import com.alashow.ui.theme.LocalThemeState

@Composable
fun StartSheet(
    sheetTheme: ThemeState = LocalThemeState.current.copy(colorPalettePreference = ColorPalettePreference.Black),
    navigator: Navigator = LocalNavigator.current,
) {
        AppTheme(theme = sheetTheme, changeSystemBar = false) {
            Text("hello", modifier = Modifier.fillMaxSize())
        }

}

