package one.echobell.echobellandroid.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EchobellOrange = Color(0xFFF97316)
private val EchobellOrangeDark = Color(0xFF9A3412)
private val EchobellBlue = Color(0xFF2563EB)
private val LightBackground = Color(0xFFFFFBFF)
private val LightSurface = Color(0xFFFFFBFF)
private val LightSurfaceVariant = Color(0xFFF4E5DC)
private val DarkBackground = Color(0xFF18120F)
private val DarkSurface = Color(0xFF211A17)
private val DarkSurfaceVariant = Color(0xFF54433B)

private val LightColors = lightColorScheme(
    primary = EchobellOrangeDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCA),
    onPrimaryContainer = Color(0xFF331100),
    secondary = Color(0xFF79574A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBCE),
    onSecondaryContainer = Color(0xFF2F150D),
    tertiary = EchobellBlue,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFDCE2FF),
    onTertiaryContainer = Color(0xFF06164A),
    background = LightBackground,
    onBackground = Color(0xFF241915),
    surface = LightSurface,
    onSurface = Color(0xFF241915),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF594139),
    outline = Color(0xFF8C7066),
    outlineVariant = Color(0xFFD9C2B8),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFFDAD6),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB692),
    onPrimary = Color(0xFF552000),
    primaryContainer = Color(0xFF763000),
    onPrimaryContainer = Color(0xFFFFDBCA),
    secondary = Color(0xFFE8BEAD),
    onSecondary = Color(0xFF462A20),
    secondaryContainer = Color(0xFF604036),
    onSecondaryContainer = Color(0xFFFFDBCE),
    tertiary = Color(0xFFB8C4FF),
    onTertiary = Color(0xFF10246B),
    tertiaryContainer = Color(0xFF2B3D84),
    onTertiaryContainer = Color(0xFFDCE2FF),
    background = DarkBackground,
    onBackground = Color(0xFFF7DED5),
    surface = DarkSurface,
    onSurface = Color(0xFFF7DED5),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD9C2B8),
    outline = Color(0xFFA88A7F),
    outlineVariant = Color(0xFF54433B),
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
