package com.mightsana.goodminton.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mightsana.goodminton.R
import com.mightsana.goodminton.model.values.Size

@Composable
fun Loader(
    isLoading: Boolean,
    alpha: Float = 1f,
    content: @Composable () -> Unit
) {
    content()
    AnimatedVisibility(
        visible = isLoading,
        enter = EnterTransition.None,
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = alpha)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LinearProgressIndicator()
            Spacer(modifier = Modifier.height(Size.padding))
            Text(stringResource(R.string.loading_screen_text))
        }
    }
}