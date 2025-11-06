package com.example.appbarbearia.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta Laranja, Preto e Cinza (High Contrast)

// Cores Base
val PrimaryOrange = Color(0xFFFF9800) // Laranja Vibrante (Primary)
val PrimaryDark = Color(0xFFE65100)  // Laranja Escuro para variação
val BackgroundBlack = Color(0xFF1C1C1C) // Fundo Preto Quase Total
val SurfaceDark = Color(0xFF2C2C2C)    // Superfície Cinza Escura
val TextWhite = Color(0xFFFFFFFF)      // Texto Branco
val TextGray = Color(0xFFB0BEC5)      // Texto Cinza Claro (Secondary)
val ErrorRed = Color(0xFFCF6679)     // Erro

// --- Tema Escuro (Foco no Preto e Laranja) ---
val md_theme_dark_primary = PrimaryOrange
val md_theme_dark_onPrimary = Color.Black // Texto sobre o laranja
val md_theme_dark_secondary = TextGray
val md_theme_dark_onSecondary = TextWhite
val md_theme_dark_background = BackgroundBlack // Fundo principal
val md_theme_dark_surface = SurfaceDark       // Cards e caixas
val md_theme_dark_onSurface = TextWhite       // Texto sobre a surface
val md_theme_dark_error = ErrorRed

// --- Tema Claro (Foco no Branco e Laranja, mantendo o contraste) ---
val md_theme_light_primary = PrimaryDark
val md_theme_light_onPrimary = Color.White
val md_theme_light_secondary = Color(0xFF757575) // Cinza Escuro
val md_theme_light_onSecondary = Color.Black
val md_theme_light_background = Color.White // Fundo principal
val md_theme_light_surface = Color.White
val md_theme_light_onSurface = Color.Black
val md_theme_light_error = Color(0xFFB00020)