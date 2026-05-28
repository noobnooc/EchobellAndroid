package one.echobell.echobellandroid.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EchobellOrange = Color(0xFFF97316)
private val EchobellBlue = Color(0xFF2563EB)
private val LightBackground = Color(0xFFFCFAF8)
private val LightSurface = Color(0xFFFFFCFB)
private val LightSurfaceVariant = Color(0xFFF5EBE5)
private val DarkBackground = Color(0xFF110E0C)
private val DarkSurface = Color(0xFF191512)
private val DarkSurfaceVariant = Color(0xFF52443C)

private val LightColors = lightColorScheme(
    primary = EchobellOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEDE5),
    onPrimaryContainer = Color(0xFF331400),
    secondary = Color(0xFF725C4E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF7EBE4),
    onSecondaryContainer = Color(0xFF28180E),
    tertiary = EchobellBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE6EFFF),
    onTertiaryContainer = Color(0xFF002266),
    background = LightBackground,
    onBackground = Color(0xFF1E1B19),
    surface = LightSurface,
    onSurface = Color(0xFF1E1B19),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF53453E),
    outline = Color(0xFF85756C),
    outlineVariant = Color(0xFFD8C4B8),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFFDAD6),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFA726),
    onPrimary = Color(0xFF4E2600),
    primaryContainer = Color(0xFF8C4700),
    onPrimaryContainer = Color(0xFFFFE1C4),
    secondary = Color(0xFFDEC3B1),
    onSecondary = Color(0xFF3F2E22),
    secondaryContainer = Color(0xFF574437),
    onSecondaryContainer = Color(0xFFFBECE4),
    tertiary = Color(0xFF9EBEFF),
    onTertiary = Color(0xFF002E80),
    tertiaryContainer = Color(0xFF0043B3),
    onTertiaryContainer = Color(0xFFE6EFFF),
    background = DarkBackground,
    onBackground = Color(0xFFEFE6E0),
    surface = DarkSurface,
    onSurface = Color(0xFFEFE6E0),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD6C3B7),
    outline = Color(0xFF9F8D83),
    outlineVariant = Color(0xFF52443C),
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
