@file:OptIn(ExperimentalMaterial3Api::class)

package com.mightsana.goodminton.features.main.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mightsana.goodminton.features.main.home.view.EmptyLeagueText
import com.mightsana.goodminton.features.main.home.view.HomeSearchBar
import com.mightsana.goodminton.features.main.home.view.LeagueListGrid
import com.mightsana.goodminton.features.main.home.view.NewLeagueFab
import com.mightsana.goodminton.features.main.home.view.NewLeagueSheet
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.Loader
import com.mightsana.goodminton.view.PullToRefreshScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToLeague: (leagueId: String) -> Unit,
    onOpenDrawer: suspend () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadLeagues() }
    val user by viewModel.user.collectAsState()
    val leagues by viewModel.leagues.collectAsState()
    val searchExpanded by viewModel.searchExpanded.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val horizontalPadding by animateDpAsState(
        if (searchExpanded) 0.dp else Size.padding,
        animationSpec = tween(300),
        label = ""
    )
    val sheetState = rememberModalBottomSheetState()

    Loader(viewModel.isLoading.collectAsState().value) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HomeSearchBar(
                    searchQuery = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    expanded = searchExpanded,
                    onExpandedChange = { viewModel.onSearchExpandedChange(it) },
                    onCollapseSearch = { viewModel.collapseSearch() },
                    onOpenDrawer = onOpenDrawer,
                    profilePhotoModel = user.profilePhotoUrl,
                    onNavigateToProfile = onNavigateToProfile,
                    leagues = leagues,
                    onNavigateToLeague = {
                        onNavigateToLeague(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding),
                )
            },
            floatingActionButton = {
                NewLeagueFab(searchExpanded) {
                    sheetState.show()
                    viewModel.onBottomSheetExpandedChange(true)
                }
            }
        ) { innerPadding ->
            PullToRefreshScreen( { viewModel.loadLeagues() }, Modifier.padding(innerPadding) ) {
                if(leagues.isEmpty()) EmptyLeagueText() else LeagueListGrid(leagues) { onNavigateToLeague(it) }
            }
        }
    }

    // Bottom Drawer Sheet
    if(viewModel.bottomSheetExpanded.collectAsState().value) {
        NewLeagueSheet(
            sheetState = sheetState,
            viewModel = viewModel,
            onDismiss = {
                viewModel.onBottomSheetExpandedChange(false)
                viewModel.resetErrors()
            }
        )
    }
}