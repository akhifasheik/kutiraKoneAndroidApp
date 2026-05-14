package com.kutira.kone.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kutira.kone.ui.theme.KutiraGradientEnd
import com.kutira.kone.ui.theme.KutiraGradientMid
import com.kutira.kone.ui.theme.KutiraGradientStart

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "bg")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shift"
    )
    val brush = if (animate) {
        Brush.linearGradient(
            colors = listOf(KutiraGradientStart, KutiraGradientMid, KutiraGradientEnd, Color(0xFFE1F5FE)),
            start = Offset(0f, 0f + shift * 400f),
            end = Offset(1200f, 1200f - shift * 400f)
        )
    } else {
        Brush.verticalGradient(
            listOf(KutiraGradientStart.copy(alpha = 0.85f), KutiraGradientEnd.copy(alpha = 0.9f))
        )
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush)
    ) {
        content()
    }
}
