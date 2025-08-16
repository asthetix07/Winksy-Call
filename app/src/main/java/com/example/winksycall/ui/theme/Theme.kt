package com.example.winksycall.ui.theme

// Import your custom colors
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DeepBlue,
    secondary = SoftBlue,
    background = Color.Black,
    surface = DeepBlue,
    onPrimary = OnPrimaryDark,
    onSecondary = OnPrimaryDark,
    onBackground = OffWhite,
    onSurface = OffWhite
)

private val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    secondary = SoftBlue,
    background = OffWhite,
    surface = Sand,
    onPrimary = OnPrimaryLight,
    onSecondary = OnPrimaryLight,
    onBackground = DeepBlue,
    onSurface = DeepBlue
)

@Composable
fun WinksyCallTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // IMPORTANT: disable dynamicColor
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
