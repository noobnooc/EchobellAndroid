package one.echobell.echobellandroid.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EchobellOrange = Color(0xFFF97316)
private val EchobellBlue = Color(0xFF2563EB)
private val LightBackground = Color(0xFFF2F2F2)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceVariant = Color(0xFFEEEEEE)
private val DarkBackground = Color(0xFF0F0E0D)
private val DarkSurface = Color(0xFF1E1C1A)
private val DarkSurfaceVariant = Color(0xFF262626)

private val LightColors = lightColorScheme(
    primary = EchobellOrange,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEDE5),
    onPrimaryContainer = Color(0xFF331400),
    secondary = Color(0xFF4B5563),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3F4F6),
    onSecondaryContainer = Color(0xFF1F2937),
    tertiary = EchobellBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE6EFFF),
    onTertiaryContainer = Color(0xFF002266),
    background = LightBackground,
    onBackground = Color(0xFF1F2937),
    surface = LightSurface,
    onSurface = Color(0xFF1F2937),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF555555),
    outline = Color(0xFF9CA3AF),
    outlineVariant = Color(0xFFE5E7EB),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFFDAD6),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8C3D),
    onPrimary = Color(0xFF562000),
    primaryContainer = Color(0xFF8A2E00),
    onPrimaryContainer = Color(0xFFFFEDE5),
    secondary = Color(0xFF9CA3AF),
    onSecondary = Color(0xFF1F2937),
    secondaryContainer = Color(0xFF374151),
    onSecondaryContainer = Color(0xFFF3F4F6),
    tertiary = Color(0xFF9EBEFF),
    onTertiary = Color(0xFF002E80),
    tertiaryContainer = Color(0xFF0043B3),
    onTertiaryContainer = Color(0xFFE6EFFF),
    background = DarkBackground,
    onSurface = Color(0xFFE5E7EB),
    surface = DarkSurface,
    onBackground = Color(0xFFE5E7EB),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFBBBBBB),
    outline = Color(0xFF525252),
    outlineVariant = Color(0xFF262626),
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
