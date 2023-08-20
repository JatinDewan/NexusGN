package app.database.nexusgn.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val colourScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Text,
    onTertiary = TextHeaders,
    onSecondary = SubText,
    background = Background,
    tertiaryContainer = TextBody,
    onPrimary = Tag
)

@Composable
fun NexusGNTheme(
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val navigationBarColour = Color(0xFF141619)

    systemUiController.setStatusBarColor(
        color = navigationBarColour
    )
    systemUiController.setNavigationBarColor(
        color = navigationBarColour,
    )

    val colorScheme = colourScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}