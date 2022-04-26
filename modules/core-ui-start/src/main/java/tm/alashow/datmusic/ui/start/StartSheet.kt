package tm.alashow.datmusic.ui.start

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tm.alashow.base.ui.ColorPalettePreference
import tm.alashow.base.ui.ThemeState

import tm.alashow.navigation.LocalNavigator
import tm.alashow.navigation.Navigator
import tm.alashow.ui.theme.AppTheme
import tm.alashow.ui.theme.LocalThemeState

@Composable
fun StartSheet(
    sheetTheme: ThemeState = LocalThemeState.current.copy(colorPalettePreference = ColorPalettePreference.Black),
    navigator: Navigator = LocalNavigator.current,
) {
        AppTheme(theme = sheetTheme, changeSystemBar = false) {
            Text("hello", modifier = Modifier.fillMaxSize())
        }

}

