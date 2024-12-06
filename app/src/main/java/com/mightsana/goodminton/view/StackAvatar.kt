package com.mightsana.goodminton.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Avatar(
    strokeWidth: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val stroke = remember(strokeWidth) {
        Stroke(width = strokeWidth)
    }
    Box(
        modifier = modifier
            .drawWithContent {
                drawContent()
                drawCircle(
                    Color.Black,
                    size.minDimension / 2,
                    size.center,
                    style = stroke,
                    blendMode = BlendMode.Clear
                )
            }
            .clip(CircleShape)
    ) {
        content()
    }
}

@Composable
fun StackedAvatar(
    avatars: List<Any>,
    size: Dp,
    maxStacked: Int = 4,
    strokeWidth: Float = 10.0f,
    modifier: Modifier = Modifier
) {
    val boxWidth = if(avatars.size <= maxStacked) size / 2 * (avatars.size + 1) else size / 2 * (maxStacked + 2)
    Box(
        modifier = modifier.width(boxWidth),
        contentAlignment = Alignment.CenterStart
    ) {
        var offset = 0.dp
        avatars.forEachIndexed { index, avatar ->
            if(index > maxStacked) return@forEachIndexed
            Avatar(
                strokeWidth = strokeWidth,
                modifier = Modifier
                    .offset(offset)
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
            ) {
                if(index >= maxStacked) {
                    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                    Box(
                        modifier = Modifier
                            .size(size)
                            .aspectRatio(1f)
                            .background(backgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(avatars.size - maxStacked)}+",
                            style = MaterialTheme.typography.titleLarge,
                            color = contentColorFor(backgroundColor)
                        )
                    }
                    return@Avatar
                } else {
                    MyImage(
                        model = avatar,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(size)
                            .aspectRatio(1f)
                    )
                }
            }
            offset += size / 2
        }
    }
}