package com.mightsana.goodminton.features.auth.register

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mightsana.goodminton.R
import com.mightsana.goodminton.REGISTER
import com.mightsana.goodminton.SIGN_IN
import com.mightsana.goodminton.model.ext.navigateAndPopUp
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.ErrorSupportingText
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.MyTextField

@Composable
fun RegisterScreen(
    navController: NavHostController,
    mainRoute: String,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val isFullNameError by viewModel.isFullNameError.collectAsState()
    val isNicknameError by viewModel.isNicknameError.collectAsState()
    val isUsernameError by viewModel.isUsernameError.collectAsState()

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
                    painter = painterResource(R.drawable.ic_launcher_foreground),
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

//                        title()
                        Text(
                            stringResource(R.string.register_title),
                            style = MaterialTheme.typography.headlineMedium,
                        )
//                        subtitle()
                        Text(
                            stringResource(R.string.register_subtitle),
                            style = MaterialTheme.typography.titleMedium
                        )
//                        nameTextField()
                        MyTextField(
                            isError = isFullNameError,
                            value = viewModel.fullName.collectAsState().value,
                            onValueChange = { viewModel.updateFullName(it) },
                            label = { Text(stringResource(R.string.name_label)) },
                            supportingText = {
                                ErrorSupportingText(
                                    isFullNameError,
                                    viewModel.fullNameErrorMessage.collectAsState().value ?: ""
                                )
                            },
                            modifier = Modifier.fillMaxSize(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words
                            )
                        )
//                        nicknameTextField()
                        MyTextField(
                            isError = isNicknameError,
                            value = viewModel.nickname.collectAsState().value,
                            onValueChange = { viewModel.updateNickname(it) },
                            label = { Text(stringResource(R.string.nickname_label)) },
                            supportingText = {
                                ErrorSupportingText(
                                    isNicknameError,
                                    viewModel.nicknameErrorMessage.collectAsState().value ?: ""
                                )
                            },
                            modifier = Modifier.fillMaxSize(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words
                            )
                        )
//                        usernameTextField()
                        MyTextField(
                            isError = isUsernameError,
                            value = viewModel.username.collectAsState().value,
                            onValueChange = { viewModel.updateUsername(it) },
                            label = { Text(stringResource(R.string.username_label)) },
                            supportingText = {
                                AnimatedContent(isUsernameError, label = "") {
                                    if(it)
                                        Text(viewModel.usernameErrorMessage.collectAsState().value ?: "")
                                    else if(viewModel.username.collectAsState().value.isNotBlank())
                                        Text(
                                            stringResource(R.string.username_available),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                }
                            },
                            modifier = Modifier.fillMaxSize(),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None
                            )
                        )
//                        registerButton()
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.validateRegisterForm {
                                    viewModel.register {
                                        navController.navigateAndPopUp(mainRoute, REGISTER)
                                    }
                                }
                            }
                        ) {
                            Text(stringResource(R.string.register_title))
                        }

//                        additionalContent()
                        TextButton(
                            onClick = {
                                viewModel.onSignOut {
                                    navController.navigateAndPopUp(SIGN_IN, REGISTER)
                                }
                            }
                        ) { Text(stringResource(R.string.back_to_sign_in)) }

                    }
                }
            }
        }
    }
}
