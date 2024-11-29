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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mightsana.goodminton.MAIN
import com.mightsana.goodminton.SIGN_IN
import com.mightsana.goodminton.model.ext.navigateAndPopUp
import com.mightsana.goodminton.model.ext.onGesture
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.MyTextField
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
    val sheetState = rememberModalBottomSheetState()

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
        },
        floatingActionButton = {
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
                    Text("Test ${it + 1}", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }

    if(viewModel.bottomSheetExpanded.collectAsState().value) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    viewModel.onBottomSheetExpandedChange(false)
                }
            },
            sheetState = sheetState
        ) {
            val isDouble by viewModel.isDouble.collectAsState()
            val matchPoints by viewModel.matchPoints.collectAsState()
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
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
                    MyTextField(
                        label = { Text("League Name") },
                        value = viewModel.leagueName.collectAsState().value,
                        onValueChange = { viewModel.updateLeagueName(it) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Match Points
                    MyTextField(
                        label = { Text("Match Points") },
                        value = if(matchPoints == 0) "" else matchPoints.toString() ,
                        onValueChange = { viewModel.updateMatchPoints(it)},
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Number
                        ),
                        placeholder = { Text("21") }
                    )

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
                                text = "Fixed Double?"
                            )
                            Switch(
                                checked = viewModel.isFixedDouble.collectAsState().value,
                                onCheckedChange = { viewModel.toggleFixedDouble() }
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
                            onClick = { viewModel.addLeague() },
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}