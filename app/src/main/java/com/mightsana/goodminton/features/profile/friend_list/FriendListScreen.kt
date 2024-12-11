package com.mightsana.goodminton.features.profile.friend_list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.R
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.repository.friends.FriendJoint
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.Loader
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(
    uid: String,
    onBack: () -> Unit,
    onNavigateToOtherProfile: (String) -> Unit,
    viewModel: FriendListViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.observeOther(uid) }

    val user by viewModel.user.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val friendRequestReceived by viewModel.friendRequestReceived.collectAsState()
    val friendRequestSent by viewModel.friendRequestSent.collectAsState()
    val otherUser by viewModel.otherUser.collectAsState()
    val otherFriends by viewModel.otherFriends.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isProcessing by viewModel.isProcessing.collectAsState()
    val dialogVisible by viewModel.unfriendDialogVisible.collectAsState()
    val unfriendUserId by viewModel.unfriendUserId.collectAsState()

    Loader(viewModel.isLoading.collectAsState().value) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.friend_list_title, otherUser.username))
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            MyIcon(MyIcons.Back)
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(350.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(top = Size.padding)
                    .padding(horizontal = Size.padding),
                horizontalArrangement = Arrangement.spacedBy(Size.padding),
                verticalArrangement = Arrangement.spacedBy(Size.padding)
            ) {
                val sortedFriends = otherFriends
                    .sortedWith(
                        compareBy<FriendJoint> {
                            it.user.uid != user.uid
                        }.thenBy { it.user.name }
                    )

                sortedFriends.forEach {
                    val me = it.user.uid == user.uid
                    item(
                        span = if(me || sortedFriends.size == 1) {
                            { GridItemSpan(maxLineSpan) }
                        } else null
                    ) {
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(Size.padding),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Size.padding)
                            ) {
                                MyImage(
                                    model = it.user.profilePhotoUrl,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(70.dp)
                                        .onTap {
                                            if (it.user.uid != user.uid)
                                                onNavigateToOtherProfile(it.user.uid)
                                        }
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    val name = "${it.user.name}${if(me) " (${stringResource(R.string.me)})" else ""}"
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                            .onTap {
                                                if(it.user.uid != user.uid)
                                                    onNavigateToOtherProfile(it.user.uid)
                                            }
                                    )
                                    Text(
                                        text = it.user.username,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier
                                            .onTap {
                                                if(it.user.uid != user.uid)
                                                    onNavigateToOtherProfile(it.user.uid)
                                            }
                                    )
                                    val isNotFriend = !friends.any { friend -> friend.user.uid == it.user.uid } && it.user.uid != user.uid
                                    if(isNotFriend) {
                                        Spacer(modifier = Modifier.height(Size.smallPadding))
                                        val isFriendRequestedReceived = friendRequestReceived.any { request -> request.sender.uid == it.user.uid }
                                        val isFriendRequestedSent = friendRequestSent.any { request -> request.receiver.uid == it.user.uid }
                                        if(isFriendRequestedReceived) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(Size.smallPadding)
                                            ) {
                                                Button(
                                                    enabled = !isProcessing,
                                                    onClick = { viewModel.acceptFriendRequest(it.user.uid) },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        text = stringResource(R.string.accept_button_label)
                                                    )
                                                }
                                                OutlinedButton(
                                                    enabled = !isProcessing,
                                                    onClick = { viewModel.declineFriendRequest(it.user.uid) },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        text = stringResource(R.string.decline_button_label)
                                                    )
                                                }
                                            }
                                        } else {
                                            AnimatedContent(isFriendRequestedSent, label = "") { requestSent ->
                                                if (requestSent)
                                                    Button(
                                                        onClick = { viewModel.cancelFriendRequest(it.user.uid) },
                                                        colors = ButtonDefaults.buttonColors().copy(
                                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                                            disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                                                alpha = 0.5f
                                                            ),
                                                            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(
                                                                alpha = 0.5f
                                                            )
                                                        ),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(
                                                                Size.smallPadding
                                                            ),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            MyIcon(MyIcons.Cancel)
                                                            Text(text = stringResource(R.string.cancel))
                                                        }
                                                    }
                                                else
                                                    Button(
                                                        onClick = { viewModel.sendFriendRequest(it.user.uid) },
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(
                                                                Size.smallPadding
                                                            ),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            MyIcon(MyIcons.Plus)
                                                            Text(text = stringResource(R.string.request_friend))
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                    else if(it.user.uid != user.uid) {
                                        Spacer(modifier = Modifier.height(Size.smallPadding))
                                        Button(
                                            onClick = {
                                                viewModel.setUnfriendUser(it.user)
                                                viewModel.showDialog()
                                            },
                                            colors = ButtonDefaults.buttonColors().copy(
                                                containerColor = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.onError,
                                                disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                                                disabledContentColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.5f)
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(Size.smallPadding),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                MyIcon(MyIcons.Delete)
                                                Text(text = stringResource(R.string.unfriend))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(dialogVisible) {
            AlertDialog(
                modifier = Modifier
                    .padding(horizontal = Size.padding),
                onDismissRequest = { viewModel.dismissDialog() },
                text = {
                    Text(
                        stringResource(R.string.unfrined_description, unfriendUserId?.username.orEmpty())
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            unfriendUserId?.let { viewModel.unfriend(it.uid) }
                            viewModel.dismissDialog()
                        },
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = stringResource(R.string.unfriend))
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.dismissDialog() }
                    ) {
                        Text(maxLines = 1, overflow = TextOverflow.Ellipsis, text = stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}