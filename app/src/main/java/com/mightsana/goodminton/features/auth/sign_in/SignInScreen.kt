package com.mightsana.goodminton.features.auth.sign_in

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.auth.view.GoogleAuthButton
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.ErrorSupportingText
import com.mightsana.goodminton.view.Loader
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.MyTextField
import com.mightsana.goodminton.view.SurfaceVariantTextHorizontalDivider

@Composable
fun SignInScreen(
    onCheckRegisterStatusAndNavigate: (Any?) -> Unit,
    onNavigateToSignUp: () -> Unit,
    defaultWebClientId: String,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val passwordVisible = viewModel.passwordVisible.collectAsState().value
    val isEmailError = viewModel.isEmailError.collectAsState().value
    val isPasswordError = viewModel.isPasswordError.collectAsState().value

    Loader(
        viewModel.isLoading.collectAsState().value,
        alpha = 0.7f
    ) {
        Scaffold { innerPadding ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Size.padding)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 350.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Size.smallPadding)
                ) {
                    // Title Image
                    MyImage(
                        painter = painterResource(R.drawable.ic_launcher_round),
                        modifier = Modifier
                            .width(100.dp)
                            .aspectRatio(1f)
                    )
                    Card {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Size.smallPadding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(Size.padding)
                                .fillMaxWidth()
                        ) {
//                            title()
                            Text(
                                stringResource(R.string.sign_in_title),
                                style = MaterialTheme.typography.headlineMedium
                            )
//                            emailTextField()
                            MyTextField(
                                isError = isEmailError,
                                leadingIcon = {
                                    MyIcon(MyIcons.Email)
                                },
                                value = viewModel.email.collectAsState().value,
                                onValueChange = { viewModel.updateEmail(it) },
                                label = { Text(stringResource(R.string.email_label)) },
                                supportingText = {
                                    ErrorSupportingText(
                                        isEmailError,
                                        viewModel.emailErrorMessage.collectAsState().value ?: ""
                                    )
                                },
                                modifier = Modifier.fillMaxSize(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email
                                )
                            )
//                            passwordTextField()
                            MyTextField(
                                isError = isPasswordError,
                                leadingIcon = {
                                    MyIcon(MyIcons.Password)
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            viewModel.togglePasswordVisibility()
                                        }
                                    ) {
                                        AnimatedContent(passwordVisible, label = "") {
                                            MyIcon(
                                                if (!it) MyIcons.PasswordVisible else MyIcons.PasswordNotVisible
                                            )
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password
                                ),
                                visualTransformation = if (!passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                                value = viewModel.password.collectAsState().value,
                                onValueChange = { viewModel.updatePassword(it) },
                                label = { Text(stringResource(R.string.password_label)) },
                                supportingText = {
                                    ErrorSupportingText(
                                        isPasswordError,
                                        viewModel.passwordErrorMessage.collectAsState().value ?: ""
                                    )
                                },
                                modifier = Modifier.fillMaxSize()
                            )
//                            signInButton()
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    viewModel.validateSignInForm {
                                        viewModel.onSignInWithEmailAndPassword { destination ->
                                            onCheckRegisterStatusAndNavigate(destination)
                                        }
                                    }
                                }
                            ) {
                                Text(stringResource(R.string.sign_in_title))
                            }
//                            authOptions()
                            SurfaceVariantTextHorizontalDivider(
                                text = stringResource(R.string.or_continue_with)
                            )

                            GoogleAuthButton(
                                defaultWebClientId = defaultWebClientId,
                                onGetCredentialResponse = { credential ->
                                    viewModel.appLoading()
                                    viewModel.onSignInWithGoogle(credential) { destination ->
                                        onCheckRegisterStatusAndNavigate(destination)
                                    }
                                }
                            )

                            TextButton(
                                onClick = onNavigateToSignUp
                            ) { Text(stringResource(R.string.dont_have_account)) }

                        }
                    }
                }
            }
        }
    }
}