package one.echobell.echobellandroid.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1565C0),
    secondary = Color(0xFF00695C),
    tertiary = Color(0xFFB26A00),
    surface = Color(0xFFFBFCFE),
    surfaceVariant = Color(0xFFE7EBF2),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    secondary = Color(0xFF80CBC4),
    tertiary = Color(0xFFFFCC80),
    surface = Color(0xFF111418),
    surfaceVariant = Color(0xFF30343B),
)

@Composable
fun EchobellTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
