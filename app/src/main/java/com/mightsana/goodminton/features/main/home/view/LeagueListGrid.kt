package com.mightsana.goodminton.features.main.home.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.model.ext.showDate

@Composable
fun LeagueListGrid(
    leagues: List<LeagueJoint>,
    onNavigateToLeague: (leagueId: String) -> Unit
) {
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
                modifier = Modifier.clickable { onNavigateToLeague(it.id) }
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