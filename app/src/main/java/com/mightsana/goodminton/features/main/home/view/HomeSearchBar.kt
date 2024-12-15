package com.mightsana.goodminton.features.main.home.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCollapseSearch: () -> Unit,
    onOpenDrawer: suspend () -> Unit,
    profilePhotoModel: Any?,
    onNavigateToProfile: () -> Unit,
    leagues: List<LeagueJoint>,
    onNavigateToLeague: (leagueId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        SearchBar(
            modifier = Modifier
                .widthIn(min = 450.dp)
                .fillMaxWidth(),
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = { onQueryChange(it) },
                    onSearch = { onCollapseSearch() },
                    expanded = expanded,
                    onExpandedChange = { onExpandedChange(it) },
                    placeholder = { Text(stringResource(R.string.league_search_placeholder)) },
                    leadingIcon = {
                        AnimatedContent(expanded, label = "") {
                            if (!it)
                                Icon(
                                    MyIcons.Menu,
                                    contentDescription = null,
                                    modifier = Modifier.onTap { scope.launch { onOpenDrawer() } }
                                )
                            else
                                Icon(
                                    MyIcons.Back,
                                    contentDescription = null,
                                    modifier = Modifier.onTap { onCollapseSearch ()}
                                )
                        }
                    },
                    trailingIcon = if (!expanded) {
                        {
                            IconButton(onNavigateToProfile) {
                                MyImage(
                                    profilePhotoModel,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                )
                            }
                        }
                    } else null,
                )
            },
            expanded = expanded,
            onExpandedChange = { onExpandedChange(it) },
        ) { HomeSearchContent(searchQuery, leagues) { onNavigateToLeague(it) } }
    }
}

@Composable
fun HomeSearchContent(
    searchQuery: String,
    leagues: List<LeagueJoint>,
    onNavigateToLeague: (leagueId: String) -> Unit
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
                        .clickable { onNavigateToLeague(it.id) }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}