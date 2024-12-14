package com.mightsana.goodminton.features.maintenance.update

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.R
import com.mightsana.goodminton.model.values.Size

@Composable
fun UpdateScreen(viewModel: UpdateViewModel = hiltViewModel()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val currentVersionName = viewModel.currentVersionName
        val latestVersionName = viewModel.latestVersionName.collectAsState().value

        Column(
            modifier = Modifier.padding(horizontal = Size.padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Size.padding)
        ) {
            Text(
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(
                    R.string.update_screen_text,
                    currentVersionName.orEmpty(),
                    latestVersionName,
                    stringResource(R.string.app_name)
                )
            )
            Button(onClick = { viewModel.openUrl() }) {
                Text(stringResource(R.string.update_screen_button))
            }
        }
    }
}