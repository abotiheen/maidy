package com.example.maidy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.maidy.R

val RobotoFontFamily = FontFamily(
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_semi_bold, FontWeight.SemiBold),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_extra_bold, FontWeight.ExtraBold)
)

private val defaultTypography = Typography()

val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = RobotoFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = RobotoFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = RobotoFontFamily),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = RobotoFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = RobotoFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = RobotoFontFamily),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = RobotoFontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = RobotoFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = RobotoFontFamily),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = RobotoFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = RobotoFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = RobotoFontFamily),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = RobotoFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = RobotoFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = RobotoFontFamily)
)
