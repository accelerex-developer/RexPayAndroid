@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.globalaccelerex.rexpay.R


internal val PoppinsFamily = FontFamily(
    Font(R.font.poppins_thin, FontWeight(100)),
    Font(R.font.poppins_thin_italic, FontWeight(100), FontStyle.Italic),
    Font(R.font.poppins_extra_light, FontWeight(200)),
    Font(R.font.poppins_extra_light_italic, FontWeight(200), FontStyle.Italic),
    Font(R.font.poppins_light, FontWeight(300)),
    Font(R.font.poppins_light_italic, FontWeight(300), FontStyle.Italic),
    Font(R.font.poppins_regular, FontWeight(400)),
    Font(R.font.poppins_regular, FontWeight(400), FontStyle.Italic),
    Font(R.font.poppins_medium, FontWeight(500)),
    Font(R.font.poppins_medium_italic, FontWeight(500), FontStyle.Italic),
    Font(R.font.poppins_semi_bold, FontWeight(600)),
    Font(R.font.poppins_semi_bold_italic, FontWeight(600), FontStyle.Italic),
    Font(R.font.poppins_bold, FontWeight(700)),
    Font(R.font.poppins_bold_italic, FontWeight(700), FontStyle.Italic),
    Font(R.font.poppins_extra_bold, FontWeight(800)),
    Font(R.font.poppins_extra_bold_italic, FontWeight(800), FontStyle.Italic),
    Font(R.font.poppins_black, FontWeight(900)),
    Font(R.font.poppins_black_italic, FontWeight(900), FontStyle.Italic),
)

// Set of Material typography styles to start with
internal val Typography = Typography(
    body1 = TextStyle(
        fontFamily = PoppinsFamily,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    h1 = TextStyle(
        fontFamily = PoppinsFamily,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    subtitle1 = TextStyle(
        fontFamily = PoppinsFamily,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    button = TextStyle(
        fontFamily = PoppinsFamily,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
    )
)