package com.perfectappstudio.scientificcalc.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Deep Space Background
val DeepSpaceTop = Color(0xFF14232D)     // Background top
val DeepSpaceBottom = Color(0xFF101B22)  // Background bottom
val DeepSpaceBase = Color(0xFF101B22)    // Base background

// Glass Panel Colors
val GlassLight = Color(0x0FFFFFFF)       // rgba(255,255,255,0.06)
val GlassMedium = Color(0x1FFFFFFF)      // rgba(255,255,255,0.12)
val GlassBorder = Color(0x1AFFFFFF)      // rgba(255,255,255,0.10)
val GlassHeavy = Color(0x33FFFFFF)       // rgba(255,255,255,0.20)

// Primary Accents
val PurpleAccent = Color(0xFF8CE4C5)     // Calculator primary
val PurpleBright = Color(0xFF8CE4C5)     // Equals button solid
val PurpleShadow = Color(0xFF36866F)     // Neo-brutalist shadow

// Feature Accent Colors
val MintGreen = Color(0xFF6EE7B7)        // Graph
val AmberAccent = Color(0xFFFCD34D)      // Solver
val AmberShadow = Color(0xFFD97706)      // Solver shadow
val CyanAccent = Color(0xFF67E8F9)       // Converter
val PinkAccent = Color(0xFFF472B6)       // Statistics/Clear
val PinkShadow = Color(0xFFDB2777)       // Pink shadow
val LimeGreen = Color(0xFF22C55E)        // Base-N
val OrangeAccent = Color(0xFFFB923C)     // Matrix

// Text
val TextPrimary = Color(0xFFF2F6F7)      // Pure white - never gray
val TextSecondary = Color(0xFFB5C3CC)    // 60% white
val TextDim = Color(0xFF94A7B4)          // 30% white

val DarkColorScheme = darkColorScheme(
    primary = PurpleAccent,
    onPrimary = DeepSpaceBase,
    primaryContainer = Color(0xFF234C40),
    onPrimaryContainer = MintGreen,
    secondary = MintGreen,
    onSecondary = DeepSpaceBase,
    secondaryContainer = Color(0xFF234C40),
    onSecondaryContainer = MintGreen,
    tertiary = PinkAccent,
    onTertiary = TextPrimary,
    background = DeepSpaceBase,
    onBackground = TextPrimary,
    surface = Color(0xFF1C2B35),
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF283A46),
    onSurfaceVariant = TextSecondary,
    surfaceContainer = Color(0xFF1C2B35),
    surfaceContainerHigh = Color(0xFF283A46),
    error = PinkAccent,
    onError = TextPrimary,
)
