package com.mightsana.goodminton.features.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mightsana.goodminton.MAIN
import com.mightsana.goodminton.R
import com.mightsana.goodminton.SIGN_IN
import com.mightsana.goodminton.model.ext.navigateAndPopUp
import com.mightsana.goodminton.model.ext.onGesture
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import com.mightsana.goodminton.view.MyTopBar
import com.mightsana.goodminton.view.MyTopBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val textFieldState = rememberTextFieldState()
    var expanded by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Hinted search text") },
                    leadingIcon = {
                        AnimatedContent(
                            expanded,
                            label = ""
                        ) {
                        if(!it)
                            Icon(Icons.Default.Menu, contentDescription = null)
                        else
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.onTap {
                                    expanded = false
                                }
                            )
                        }
                    },
                    trailingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                repeat(4) { idx ->
                    val resultText = "Suggestion $idx"
                    ListItem(
                        headlineContent = { Text(resultText) },
                        supportingContent = { Text("Additional info") },
                        leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier =
                        Modifier.clickable {
                            textFieldState.setTextAndPlaceCursorAtEnd(resultText)
                            expanded = false
                        }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
//    Scaffold(
//        modifier = Modifier
//            .fillMaxSize()
//            .nestedScroll(scrollBehavior.nestedScrollConnection),
//        topBar = {
//            MyTopBar(
//                title = { Text(stringResource(R.string.app_name)) },
//                actions = {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.padding(end = Size.padding)
//                    ) {
//                        IconButton(
//                            onClick = {
////                                navController.navigate(SEARCH)
//                            }
//                        ) {
//                            MyIcon(MyIcons.SearchExpanded)
//                        }
//
//
//                        BadgedBox(
//                            badge = {
//                                Badge {
//                                    Text("${viewModel.friendRequestReceivedCount.collectAsState().value}")
//                                }
//                            },
//                            modifier = Modifier.onTap {
////                                navController.navigate(NOTIFICATIONS)
//                            }
//                        ) { MyIcon(MyIcons.Notification) }
//
//                        Spacer(modifier = Modifier.width(Size.padding))
//
//                        MyImage(
//                            user.profilePhotoUrl,
//                            modifier = Modifier
//                                .width(40.dp)
//                                .clip(CircleShape)
//                                .onGesture(
//                                    onTap = {
////                                        navController.navigateSingleTop(PROFILE)
//                                    },
//                                    onLongPress = {
//                                        viewModel.onSignOut {
//                                            navController.navigateAndPopUp(SIGN_IN, MAIN)
//                                        }
//                                    }
//                                )
//                        )
//                    }
//                },
//                scrollBehavior = scrollBehavior,
//                modifier = Modifier.clip(MyTopBarDefaults.shape)
//            )
//        }
//    ) { innerPadding ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxWidth()
//        ) {
//            items(50) {
//                Text(
//                    text = "Item ${it + 1}"
//                )
//            }
//        }
//    }
}