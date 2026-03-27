package com.perfectappstudio.scientificcalc.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Deep Space Background
val DeepSpaceTop = Color(0xFF1A0A2E)     // Purple-tinted deep
val DeepSpaceBottom = Color(0xFF0A1A1A)  // Teal-tinted deep
val DeepSpaceBase = Color(0xFF0F0F14)    // Base background

// Glass Panel Colors
val GlassLight = Color(0x0FFFFFFF)       // rgba(255,255,255,0.06)
val GlassMedium = Color(0x1FFFFFFF)      // rgba(255,255,255,0.12)
val GlassBorder = Color(0x1AFFFFFF)      // rgba(255,255,255,0.10)
val GlassHeavy = Color(0x33FFFFFF)       // rgba(255,255,255,0.20)

// Primary Accents
val PurpleAccent = Color(0xFFA78BFA)     // Calculator primary
val PurpleBright = Color(0xFF8B5CF6)     // Equals button solid
val PurpleShadow = Color(0xFF6D28D9)     // Neo-brutalist shadow

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
val TextPrimary = Color(0xFFFFFFFF)      // Pure white - never gray
val TextSecondary = Color(0x99FFFFFF)    // 60% white
val TextDim = Color(0x4DFFFFFF)          // 30% white

val DarkColorScheme = darkColorScheme(
    primary = PurpleAccent,
    onPrimary = TextPrimary,
    secondary = MintGreen,
    onSecondary = DeepSpaceBase,
    tertiary = PinkAccent,
    onTertiary = TextPrimary,
    background = DeepSpaceBase,
    onBackground = TextPrimary,
    surface = GlassLight,
    onSurface = TextPrimary,
    surfaceVariant = GlassMedium,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = GlassLight,
    surfaceContainerHigh = GlassMedium,
    error = PinkAccent,
    onError = TextPrimary,
)
