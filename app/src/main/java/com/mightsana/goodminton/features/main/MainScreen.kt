package com.mightsana.goodminton.features.main

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MyTopBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = Size.padding)
                    ) {
                        IconButton(
                            onClick = {
//                                navController.navigate(SEARCH)
                            }
                        ) {
                            MyIcon(MyIcons.SearchExpanded)
                        }


                        BadgedBox(
                            badge = {
                                Badge {
                                    Text("${viewModel.friendRequestReceivedCount.collectAsState().value}")
                                }
                            },
                            modifier = Modifier.onTap {
//                                navController.navigate(NOTIFICATIONS)
                            }
                        ) { MyIcon(MyIcons.Notification) }

                        Spacer(modifier = Modifier.width(Size.padding))

                        MyImage(
                            user.profilePhotoUrl,
                            modifier = Modifier
                                .width(40.dp)
                                .clip(CircleShape)
                                .onGesture(
                                    onTap = {
//                                        navController.navigateSingleTop(PROFILE)
                                    },
                                    onLongPress = {
                                        viewModel.onSignOut {
                                            navController.navigateAndPopUp(SIGN_IN, MAIN)
                                        }
                                    }
                                )
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                modifier = Modifier.clip(MyTopBarDefaults.shape)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {
            items(50) {
                Text(
                    text = "Item ${it + 1}"
                )
            }
        }
    }
}