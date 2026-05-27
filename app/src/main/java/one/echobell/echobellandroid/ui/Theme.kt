package one.echobell.echobellandroid.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EchobellOrange = Color(0xFFF97316)
private val EchobellPrimary = Color(0xFFC2410C)
private val EchobellBlue = Color(0xFF2563EB)
private val LightBackground = Color(0xFFF5F5F4)
private val LightSurface = Color(0xFFFFFCF8)
private val LightSurfaceVariant = Color(0xFFEDE7DE)
private val DarkBackground = Color(0xFF141311)
private val DarkSurface = Color(0xFF1F1B18)
private val DarkSurfaceVariant = Color(0xFF4C463F)

private val LightColors = lightColorScheme(
    primary = EchobellPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9BD),
    onPrimaryContainer = Color(0xFF351000),
    secondary = Color(0xFF745944),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDEC5),
    onSecondaryContainer = Color(0xFF2A1709),
    tertiary = EchobellBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD9E2FF),
    onTertiaryContainer = Color(0xFF06164A),
    background = LightBackground,
    onBackground = Color(0xFF211A16),
    surface = LightSurface,
    onSurface = Color(0xFF211A16),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF5B5047),
    outline = Color(0xFF85766A),
    outlineVariant = Color(0xFFE1D4CA),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFFDAD6),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB077),
    onPrimary = Color(0xFF4A1800),
    primaryContainer = Color(0xFF8A2D00),
    onPrimaryContainer = Color(0xFFFFDBC8),
    secondary = Color(0xFFE5C0A6),
    onSecondary = Color(0xFF422B1B),
    secondaryContainer = Color(0xFF5B412F),
    onSecondaryContainer = Color(0xFFFFDEC5),
    tertiary = Color(0xFFB6C4FF),
    onTertiary = Color(0xFF10246B),
    tertiaryContainer = Color(0xFF2B3D84),
    onTertiaryContainer = Color(0xFFD9E2FF),
    background = DarkBackground,
    onBackground = Color(0xFFF4E7DD),
    surface = DarkSurface,
    onSurface = Color(0xFFF4E7DD),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD6C5B8),
    outline = Color(0xFFA29184),
    outlineVariant = Color(0xFF4C463F),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
)

@Composable
fun EchobellTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
