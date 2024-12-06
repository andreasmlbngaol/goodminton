package com.mightsana.goodminton.features.main.notifications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.Loader
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.StackedAvatar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onOpenDrawer: suspend () -> Unit,
    onNavigateToLeague: (String) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    BackHandler { onBack() }
    val invitationsReceived by viewModel.invitationsReceived.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Loader(viewModel.isLoading.collectAsState().value) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Notifications") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { onOpenDrawer() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = null,
                                modifier = Modifier.onTap {
                                    scope.launch {
                                        onOpenDrawer()
                                    }
                                }
                            )
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
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val invitationsCount = invitationsReceived.size

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Text(
                            text = "Invitations (${invitationsCount})",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = Size.smallPadding)
                        )
                        HorizontalDivider()
                    }
                }

                if(invitationsCount > 0) {
                    items(invitationsReceived.sortedBy { it.invitedAt }) {
                        val profilePictures = it.participants.map { it.user.profilePhotoUrl!! }
                            .filter { pict -> pict != it.league.createdBy.profilePhotoUrl }
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.clickable { onNavigateToLeague(it.league.id) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(Size.smallPadding)
                            ) {
                                val imageSize = 50.dp
                                Text(
                                    text = it.league.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(Size.smallPadding),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MyImage(model = it.sender.profilePhotoUrl, modifier = Modifier.clip(CircleShape).size(imageSize))
                                    Text(it.sender.name)
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Participants:")
                                    StackedAvatar(
                                        avatars = profilePictures,
                                        size = imageSize
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(Size.padding),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        enabled = !viewModel.isProcessing.collectAsState().value,
                                        onClick = { viewModel.acceptInvitation(it.id, it.league.id) },
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
                                        onClick = { viewModel.declineInvitation(it.id) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            text = "Decline"
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "No Invitations.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Size.padding),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}