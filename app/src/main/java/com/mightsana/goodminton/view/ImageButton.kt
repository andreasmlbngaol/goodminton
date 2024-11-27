package com.mightsana.goodminton.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ImageButton(
    onClick: () -> Unit,
    painter: Painter,
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 40.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .width(width)
            .height(height)
    ) {
        MyImage(
            painter = painter,
        )
    }
}

@Composable
fun MyImage(
    painter: Painter,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter
    )
}
