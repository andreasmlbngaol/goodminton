package com.mightsana.goodminton.features.main.social

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mightsana.goodminton.LEAGUE_DETAIL
import com.mightsana.goodminton.features.profile.model.PROFILE
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.view.MyImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    appNavController: NavHostController,
    drawerState: DrawerState,
    viewModel: SocialViewModel = hiltViewModel()
) {
    val requestReceived by viewModel.friendRequestReceived.collectAsState()
    val user by viewModel.user.collectAsState()
    val searchExpanded by viewModel.searchExpanded.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val horizontalPadding by animateDpAsState(
        if (searchExpanded) 0.dp else 16.dp,
        animationSpec = tween(300),
        label = ""
    )

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                contentAlignment = Alignment.Center
            ) {
                SearchBar(
                    modifier = Modifier
                        .widthIn(min = 450.dp)
                        .fillMaxWidth(),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = { viewModel.onSearchQueryChange(it) },
                            onSearch = { viewModel.collapseSearch() },
                            expanded = searchExpanded,
                            onExpandedChange = { viewModel.onSearchExpandedChange(it) },
                            placeholder = { Text("Search...") },
                            leadingIcon = {
                                AnimatedContent(
                                    searchExpanded,
                                    label = ""
                                ) {
                                    if (!it)
                                        Icon(
                                            Icons.Default.Menu,
                                            contentDescription = null,
                                            modifier = Modifier.onTap {
                                                scope.launch {
                                                    drawerState.open()
                                                }
                                            }
                                        )
                                    else
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            modifier = Modifier.onTap {
                                                viewModel.collapseSearch()
                                            }
                                        )
                                }
                            },
                            trailingIcon = if (!searchExpanded) {
                                {
                                    IconButton(
                                        { appNavController.navigate(PROFILE) }
                                    ) {
                                        MyImage(
                                            user.profilePhotoUrl,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                        )
                                    }
                                }
                            } else null,

                        )
                    },
                    expanded = searchExpanded,
                    onExpandedChange = { viewModel.onSearchExpandedChange(it) },
                ) {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        if (searchQuery.isNotEmpty()) {
//                            leagues.filter {it.name.contains(searchQuery, true)}.forEach {
//                                ListItem(
//                                    headlineContent = { Text(it.name) },
//                                    supportingContent = { Text(it.createdById) },
//                                    leadingContent = {
//                                        Icon(
//                                            Icons.Filled.Star,
//                                            contentDescription = null
//                                        )
//                                    },
//                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
//                                    modifier = Modifier
//                                        .clickable {
//                                            viewModel.collapseSearch()
//                                            appNavController.navigate("$LEAGUE_DETAIL/${it.id}")
//                                        }
//                                        .fillMaxWidth()
//                                        .padding(horizontal = 16.dp, vertical = 4.dp)
//                                )
//                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            requestReceived.forEach {
                ListItem(
                    headlineContent = { Text(it.sender.name) },
                    supportingContent = { Text("Friend Request") }
                )
            }
        }
    }
}