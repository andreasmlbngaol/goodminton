package com.mightsana.goodminton.features.main.social

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.repository.users.MyUser
import com.mightsana.goodminton.view.MyImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    onBack: () -> Unit,
    onOpenDrawer: suspend () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToOtherProfile: (String) -> Unit,
    viewModel: SocialViewModel = hiltViewModel()
) {
    BackHandler { onBack() }
    val allUsers by viewModel.allUsers.collectAsState()
    val user by viewModel.user.collectAsState()
    val requestReceived by viewModel.friendRequestReceived.collectAsState()
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
                                                    onOpenDrawer()
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
                                    IconButton(onNavigateToProfile) {
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
                    AnimatedContent(
                        targetState = searchQuery,
                        label = ""
                    ) { query ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if(query.isNotEmpty()) {
                                val userFiltered = allUsers
                                    .filter {
                                        (it.name.contains(query, ignoreCase = true)
                                                || it.username.contains(query, ignoreCase = true)
                                        ) && it.uid != user.uid
                                    }
                                    .sortedWith(
                                        compareBy<MyUser> {
                                            val indexInUsername = it.username.indexOf(query, ignoreCase = true)
                                            if (indexInUsername == -1) Int.MAX_VALUE else indexInUsername
                                        }.thenBy { it.username }
                                            .thenBy {
                                                val indexInName = it.name.indexOf(query, ignoreCase = true)
                                                if (indexInName == -1) Int.MAX_VALUE else indexInName
                                            }
                                    )
                                if(userFiltered.isEmpty())
                                    item {
                                        Text(
                                            text = "No User Found",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 32.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                else
                                    items(userFiltered) {
                                        ListItem(
                                            modifier = Modifier
                                                .clickable {
                                                    viewModel.collapseSearch()
                                                    onNavigateToOtherProfile(it.uid)
                                                },
                                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                            headlineContent = { Text(it.name) },
                                            supportingContent = { Text(it.username) }
                                        )
                                    }
                            }
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