package com.mightsana.goodminton.features.profile.self_profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mightsana.goodminton.R
import com.mightsana.goodminton.SIGN_IN
import com.mightsana.goodminton.model.ext.navigateAndPopAll
import com.mightsana.goodminton.model.ext.onLongPress
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.view.Loader
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.MyTextField
import com.mightsana.goodminton.view.MyTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SelfProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val friendsJoint by viewModel.friendsJoint.collectAsState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val profilePictureExpanded by viewModel.profilePictureExpanded.collectAsState()
    val imagePosition = remember { mutableStateOf(IntOffset(0, 0)) }
    val imageExpandedDuration = 600
    val imageMinWidth = 100.dp
    val imageMaxWidth = 400.dp
    val expandedImageWidth by animateDpAsState(
        targetValue = if (profilePictureExpanded) imageMaxWidth else imageMinWidth,
        animationSpec = tween(durationMillis = imageExpandedDuration),
        label = ""
    )
    val blurRadius by animateDpAsState(
        targetValue = if(profilePictureExpanded) 15.dp else 0.dp,
        animationSpec = tween(durationMillis = imageExpandedDuration),
        label = ""
    )
    val imageAlpha by animateFloatAsState(
        targetValue = if (profilePictureExpanded) 0f else 1f,
        animationSpec = tween(durationMillis = imageExpandedDuration),
        label = ""
    )

//    LaunchedEffect(Unit) {
//        viewModel.refreshUserListener()
//    }

    Loader(viewModel.isLoading.collectAsState().value) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
                .blur(radius = blurRadius),
            topBar = {
                MyTopBar(
                    title = {
                        Text(user.username)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            MyIcon(MyIcons.Back)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.onSignOut {
                                    navController.navigateAndPopAll(SIGN_IN)
                                }
                            }
                        ) { MyIcon(MyIcons.Logout) }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if(user.profilePhotoUrl == null)
                            MyImage(
                                painter = painterResource(R.drawable.google_logo),
                                modifier = Modifier
                                    .width(imageMinWidth)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                            )
                        else
                            MyImage(
                                model = user.profilePhotoUrl,
                                modifier = Modifier
                                    .width(imageMinWidth)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .onGloballyPositioned { coordinates ->
                                        imagePosition.value = IntOffset(
                                            x = coordinates.positionInRoot().x.toInt(),
                                            y = coordinates.positionInRoot().y.toInt()
                                        )
                                    }
                                    .onLongPress { viewModel.expandProfilePicture() }
                                    .alpha(imageAlpha)
                            )
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .onTap {
//                                        navController.navigateSingleTop("$FRIEND_LIST/${user.uid}/${user.username}")
                                        viewModel.comingSoon()
                                    }
                            ) {
                                Text(friendsJoint.size.toString())
                                Text("Friends")
                            }
                        }
                    }
                }
                item {
                    Column {
                        Text(user.name)
                        Text("~ ${user.nickname} ~", style = MaterialTheme.typography.titleMedium, fontStyle = FontStyle.Italic)
                    }
                }
                item {
                    Button(
                        {
//                            navController.navigateSingleTop("$PROFILE/$SETTINGS")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MyIcon(MyIcons.Edit)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Profile")
                    }
                }
                item {
                    MyTextField(
                        value = user.bio ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        placeholder = { Text("No bio yet. 😊") },
                        minLines = 3,
                        singleLine = false
                    )
                }
            }
        }
        AnimatedVisibility(
            profilePictureExpanded,
            enter = fadeIn(animationSpec = tween(durationMillis = imageExpandedDuration)),
            exit = fadeOut(animationSpec = tween(durationMillis = imageExpandedDuration))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onTap { viewModel.dismissProfilePicture() },
                contentAlignment = Alignment.Center
            ) {
                MyImage(
                    model = user.profilePhotoUrl,
                    modifier = Modifier
                        .width(expandedImageWidth)
                        .padding(horizontal = 16.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .clickable(false) {}
                )
            }
        }
    }
}
