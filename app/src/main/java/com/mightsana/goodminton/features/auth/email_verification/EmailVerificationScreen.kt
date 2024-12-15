package com.mightsana.goodminton.features.auth.email_verification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.R
import com.mightsana.goodminton.model.ext.censoredEmail
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons

@Composable
fun EmailVerificationScreen(
    onSignOut: () -> Unit,
    onEmailVerified: () -> Unit,
    viewModel: EmailVerificationViewModel = hiltViewModel()
) {
    viewModel.checkEmailVerification { onEmailVerified() }

    Scaffold { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Size.padding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Size.smallPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                MyIcon(
                    MyIcons.EmailVerification,
                    modifier = Modifier
                        .width(100.dp)
                        .aspectRatio(1f)
                )

                // Description
                Text(
                    text = stringResource(R.string.email_verification_message, viewModel.authEmail.censoredEmail()),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                // Buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button( { viewModel.openEmailApp()}) { Text(text = stringResource(R.string.open_email_app)) }
                    TextButton( { viewModel.onSignOut { onSignOut() } }) { Text(text = stringResource(R.string.back_to_sign_in)) }
                }
            }
        }
    }
}