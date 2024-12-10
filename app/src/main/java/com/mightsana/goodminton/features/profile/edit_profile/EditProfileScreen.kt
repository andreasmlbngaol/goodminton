package com.mightsana.goodminton.features.profile.edit_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.ErrorSupportingText
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.MyTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        content = { MyIcon(MyIcons.Back) }
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Size.largePadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Size.extraSmallPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    MyImage(
                        model = user.profilePhotoUrl,
                        modifier = Modifier
                            .size(150.dp)
                    )
                    IconButton(
                        onClick = { viewModel.comingSoon() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(0.4f))
                            .align(Alignment.BottomCenter)
                    ) {
                        MyIcon(MyIcons.Edit)
                    }
                }
                val name by viewModel.name.collectAsState()
                MyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = if(name == null) user.name else name.orEmpty(),
                    placeholder = { Text(user.name) },
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Name") },
                    supportingText = {}
                )
                val nickname by viewModel.nickname.collectAsState()
                MyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = if(nickname == null) user.nickname else nickname.orEmpty(),
                    placeholder = { Text(user.nickname) },
                    onValueChange = { viewModel.updateNickname(it) },
                    label = { Text("Nickname") },
                    supportingText = {}
                )
                val username by viewModel.username.collectAsState()
                val usernameErrorMessage by viewModel.usernameErrorMessage.collectAsState()
                MyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = if(username == null) user.username else username.orEmpty(),
                    placeholder = { Text(user.username) },
                    onValueChange = { viewModel.updateUsername(it) },
                    label = { Text("Username") },
                    isError = usernameErrorMessage != null,
                    supportingText = {
                        ErrorSupportingText(
                            usernameErrorMessage != null,
                            usernameErrorMessage.orEmpty()
                        )
                    }
                )
                val bio by viewModel.bio.collectAsState()
                MyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    singleLine = false,
                    value = if(bio == null) user.bio.orEmpty() else bio.orEmpty(),
                    placeholder = { Text(user.bio.orEmpty()) },
                    onValueChange = { viewModel.updateBio(it) },
                    label = { Text("Bio") },
                    supportingText = {}
                )
                Button(
                    onClick = { viewModel.saveChanges(onBack) },
                    enabled = usernameErrorMessage == null && viewModel.isEditing.collectAsState().value
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}