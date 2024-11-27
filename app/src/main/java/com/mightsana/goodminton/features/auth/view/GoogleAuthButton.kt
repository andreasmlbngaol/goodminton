package com.mightsana.goodminton.features.auth.view

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.mightsana.goodminton.R
import com.mightsana.goodminton.view.ImageButton
import kotlinx.coroutines.launch

@Composable
fun GoogleAuthButton(
    defaultWebClientId: String,
    onGetCredentialResponse: (Credential) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    ImageButton(
        onClick = {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(defaultWebClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            coroutineScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context
                    )
                    onGetCredentialResponse(result.credential)

                } catch (e: Exception) {
                    Log.e("GoogleAuthButton", "Error getting credential", e)
                }
            }

        },
        painterResource(R.drawable.google_logo),
        width = 40.dp,
        height = 40.dp
    )

}