package com.mightsana.goodminton.features.profile.other_profile

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.R
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.Loader
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.MyTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherProfileScreen(
    uid: String,
    onBack: () -> Unit,
    onNavigateToFriendList: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OtherProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.observeOther(uid) }

    val otherUser by viewModel.otherUser.collectAsState()
    val friendsJoint by viewModel.friends.collectAsState()
    val currentUser by viewModel.user.collectAsState()
    val friendRequestsReceived by viewModel.friendRequestReceived.collectAsState()
    val friendRequestsSent by viewModel.friendRequestSent.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val isFriend = friendsJoint.any { it.user.uid == currentUser.uid }
    val isFriendRequested = friendRequestsSent.any { it.receiver.uid == otherUser.uid }
    val isFriendRequestReceived = friendRequestsReceived.any { it.sender.uid == otherUser.uid }


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val profilePictureExpanded by viewModel.profilePictureExpanded.collectAsState()
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

    Loader(viewModel.isLoading.collectAsState().value) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .fillMaxSize()
                .blur(radius = blurRadius),
            topBar = {
                MyTopBar(
                    title = {
                        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = otherUser.username)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack
                        ) { MyIcon(MyIcons.Back) }
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
                        if(otherUser.profilePhotoUrl == null)
                            MyImage(
                                painter = painterResource(R.drawable.google_logo),
                                modifier = Modifier
                                    .width(imageMinWidth)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                            )
                        else
                            MyImage(
                                model = otherUser.profilePhotoUrl,
                                modifier = Modifier
                                    .width(imageMinWidth)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .onTap { viewModel.expandProfilePicture() }
                                    .alpha(imageAlpha)
                            )
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.onTap {
                                    if (friendsJoint.isNotEmpty())
                                        onNavigateToFriendList(otherUser.uid)
                                }
                            ) {
                                Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = "${friendsJoint.size}")
                                Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = if(friendsJoint.size == 1) "Friend" else "Friends")
                            }
                        }
                    }

                }

                item {
                    Column {
                        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = otherUser.name)
                        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = "~ ${otherUser.nickname} ~", style = MaterialTheme.typography.titleMedium, fontStyle = FontStyle.Italic)
                    }
                }
                item {
                    if (isFriend) {
                        Button(
                            onClick = { viewModel.showDialog() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors().copy(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            MyIcon(MyIcons.Delete)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = "Unfriend")
                        }
                    } else if (isFriendRequested) {
                        Button(
                            enabled = !isProcessing,
                            onClick = { viewModel.cancelFriendRequest() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors().copy(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            MyIcon(MyIcons.Cancel)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = "Cancel Request")
                        }
                    } else if (isFriendRequestReceived) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                enabled = !viewModel.isProcessing.collectAsState().value,
                                onClick = { viewModel.acceptFriendRequest() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    text = "Accept"
                                )
                            }
                            OutlinedButton(
                                enabled = !viewModel.isProcessing.collectAsState().value,
                                onClick = { viewModel.declineFriendRequest() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    text = "Decline"
                                )
                            }
                        }
                    } else {
                        Button(
                            enabled = !viewModel.isProcessing.collectAsState().value,
                            onClick = { viewModel.sendFriendRequest() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MyIcon(MyIcons.Plus)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = "Request Friend")
                        }

                    }
                }
                item {
                    val containerColor = MaterialTheme.colorScheme.surfaceVariant
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors().copy(
                            containerColor = containerColor,
                            contentColor = contentColorFor(containerColor)
                        )
                    ) {
                        Text(
                            text = otherUser.bio ?: "No bio yet. 😊",
                            minLines = 3,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = Size.padding)
                                .padding(top = Size.smallPadding),
                            lineHeight = MaterialTheme.typography.titleLarge.lineHeight
                        )
                    }
                }

            }
            if(viewModel.dialogVisible.collectAsState().value) {
                AlertDialog(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    onDismissRequest = {
                        viewModel.hideDialog()
                    },
                    text = {
                        Text("Are you sure you want to unfriend ${otherUser.username}?")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.unfriend()
                                viewModel.hideDialog()
                            },
                            colors = ButtonDefaults.buttonColors().copy(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = "Unfriend")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                viewModel.hideDialog()
                            }
                        ) {
                            Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = "Cancel")
                        }
                    }
                )
            }
        }
        AnimatedVisibility(
            profilePictureExpanded,
            enter = fadeIn(
                animationSpec = tween(durationMillis = imageExpandedDuration)
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = imageExpandedDuration)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onTap {
                        viewModel.dismissProfilePicture()
                    },
                contentAlignment = Alignment.Center
            ) {
                MyImage(
                    model = otherUser.profilePhotoUrl,
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