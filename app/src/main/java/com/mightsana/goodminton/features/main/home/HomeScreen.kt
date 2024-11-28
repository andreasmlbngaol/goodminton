package com.mightsana.goodminton.features.main.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mightsana.goodminton.MAIN
import com.mightsana.goodminton.SIGN_IN
import com.mightsana.goodminton.model.ext.navigateAndPopUp
import com.mightsana.goodminton.model.ext.onGesture
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.view.MyImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    drawerState: DrawerState,
    appNavController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val expanded by viewModel.searchExpanded.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val horizontalPadding by animateDpAsState(
        if (expanded) 0.dp else 16.dp,
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
                            query = query,
                            onQueryChange = { viewModel.onSearchQueryChange(it) },
                            onSearch = { viewModel.collapseSearch() },
                            expanded = expanded,
                            onExpandedChange = { viewModel.onSearchExpandedChange(it) },
                            placeholder = { Text("Search...") },
                            leadingIcon = {
                                AnimatedContent(
                                    expanded,
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
                            trailingIcon = if (!expanded) {
                                {
                                    IconButton(
                                        {}
                                    ) {
                                        MyImage(
                                            user.profilePhotoUrl,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .onGesture(
                                                    onLongPress = {
                                                        viewModel.onSignOut {
                                                            appNavController.navigateAndPopUp(
                                                                SIGN_IN,
                                                                MAIN
                                                            )
                                                        }
                                                    }
                                                )
                                        )
                                    }
                                }
                            } else null,
                        )
                    },
                    expanded = expanded,
                    onExpandedChange = { viewModel.onSearchExpandedChange(it) },
                ) {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        repeat(20) { idx ->
                            val resultText = "Suggestion $idx"
                            ListItem(
                                headlineContent = { Text(resultText) },
                                supportingContent = { Text("Additional info") },
                                leadingContent = {
                                    Icon(
                                        Icons.Filled.Star,
                                        contentDescription = null
                                    )
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                modifier =
                                Modifier
                                    .clickable {
                                        viewModel.collapseSearch()
                                    }
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(100) {
                Card(
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                ) {
                    Text("Test $it", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }

}