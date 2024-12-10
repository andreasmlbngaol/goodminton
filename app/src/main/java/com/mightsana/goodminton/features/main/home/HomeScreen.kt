package com.mightsana.goodminton.features.main.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.ext.showDate
import com.mightsana.goodminton.view.ErrorSupportingText
import com.mightsana.goodminton.view.Loader
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.MyTextField
import com.mightsana.goodminton.view.PullToRefreshScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToLeague: (String) -> Unit, // League ID
    onOpenDrawer: suspend () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val user by viewModel.user.collectAsState()
    val leagues by viewModel.leagues.collectAsState()
    val searchExpanded by viewModel.searchExpanded.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val horizontalPadding by animateDpAsState(
        if (searchExpanded) 0.dp else 16.dp,
        animationSpec = tween(300),
        label = ""
    )
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Loader(viewModel.isLoading.collectAsState().value) {
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
                                placeholder = { Text("Search league name...") },
                                leadingIcon = {
                                    AnimatedContent(
                                        searchExpanded,
                                        label = ""
                                    ) {
                                        if (!it)
                                            Icon(
                                                MyIcons.Menu,
                                                contentDescription = null,
                                                modifier = Modifier.onTap {
                                                    scope.launch {
                                                        onOpenDrawer()
                                                    }
                                                }
                                            )
                                        else
                                            Icon(
                                                MyIcons.Back,
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
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            if (searchQuery.isNotEmpty()) {
                                leagues.filter {it.name.contains(searchQuery, true)}.forEach {
                                    ListItem(
                                        headlineContent = { Text(it.name) },
                                        supportingContent = { Text(it.createdBy.name) },
                                        leadingContent = {
                                            Icon(
                                                Icons.Filled.Star,
                                                contentDescription = null
                                            )
                                        },
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.collapseSearch()
                                                onNavigateToLeague(it.id)
                                            }
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                AnimatedVisibility(!searchExpanded) {
                    ExtendedFloatingActionButton(
                        text = { Text("New League") },
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        onClick = {
                            scope.launch {
                                sheetState.show()
                                viewModel.onBottomSheetExpandedChange(true)
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            PullToRefreshScreen( { viewModel.loadLeagues() }, Modifier.padding(innerPadding) ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(350.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(leagues.sortedByDescending { it.createdAt }) {
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.clickable {
                                onNavigateToLeague(it.id)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        it.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 1,
                                        overflow = Ellipsis
                                    )
                                    Text(
                                        it.createdAt.showDate(),
                                        maxLines = 1,
                                        overflow = Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Bottom Drawer Sheet
    if(viewModel.bottomSheetExpanded.collectAsState().value) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    viewModel.onBottomSheetExpandedChange(false)
                    viewModel.resetErrors()
                }
            },
            sheetState = sheetState
        ) {
            val isDouble by viewModel.isDouble.collectAsState()
            val matchPoints by viewModel.matchPoints.collectAsState()
            val isPrivate by viewModel.isPrivate.collectAsState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .widthIn(max = 350.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    Text(
                        text = "Add New League",
                        style = MaterialTheme.typography.titleLarge,
                        textDecoration = TextDecoration.Underline
                    )

                    // League Name
                    val nameErrorMessage by viewModel.nameErrorMessage.collectAsState()
                    MyTextField(
                        isError = nameErrorMessage != null,
                        label = { Text("League Name") },
                        value = viewModel.leagueName.collectAsState().value,
                        onValueChange = { viewModel.updateLeagueName(it) },
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = nameErrorMessage?.let {
                            {
                                ErrorSupportingText(message = it)
                            }
                        }
                    )

                    // Match Points
                    val matchPointsErrorMessage by viewModel.matchPointsErrorMessage.collectAsState()
                    MyTextField(
                        isError = matchPointsErrorMessage != null,
                        label = { Text("Match Points") },
                        value = if(matchPoints == 0) "" else matchPoints.toString() ,
                        onValueChange = { viewModel.updateMatchPoints(it)},
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Number
                        ),
                        placeholder = { Text("21") },
                        supportingText = matchPointsErrorMessage?.let {
                            {
                                ErrorSupportingText(message = it)
                            }
                        }
                    )

                    // Visibility
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .onTap { viewModel.togglePrivate() }
                    ) {
                        Text(
                            text = "Public"
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = isPrivate,
                            onCheckedChange = { viewModel.togglePrivate() }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Private"
                        )
                    }

                    // Deuce
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Enable Deuce?"
                        )
                        Switch(
                            checked = viewModel.deuceEnabled.collectAsState().value,
                            onCheckedChange = { viewModel.toggleDeuce() }
                        )
                    }

                    // Single or Double
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .onTap { viewModel.toggleDouble() }
                    ) {
                        Text(
                            text = "Single"
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = isDouble,
                            onCheckedChange = { viewModel.toggleDouble() }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Double"
                        )
                    }

                    // Fixed Double?
                    AnimatedVisibility(isDouble) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Fixed Double? (Experimental)"
                            )
                            Switch(
                                checked = viewModel.isFixedDouble.collectAsState().value,
                                onCheckedChange = {
                                    viewModel.toggleFixedDouble()
                                }
                            )
                        }
                    }

                    // Confirm Button
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.resetForm() }
                        ) { Text("Reset") }
                        Button(
                            onClick = {
                                viewModel.addLeague {
                                    scope.launch {
                                        sheetState.hide()
                                        viewModel.onBottomSheetExpandedChange(false)
                                        viewModel.resetForm()
                                    }
                                }
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}