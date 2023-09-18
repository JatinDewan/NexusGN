package app.database.nexusgn.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
        color = navigationBarColour,
        darkIcons = false
    )
    systemUiController.setNavigationBarColor(
        color = navigationBarColour,
        darkIcons = false
    )

    val colorScheme = colourScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}