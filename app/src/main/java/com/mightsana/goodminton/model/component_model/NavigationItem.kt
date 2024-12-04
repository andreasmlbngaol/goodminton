package com.mightsana.goodminton.model.component_model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector,
    val label: String,
    val route: Any,
    val badgeCount: Int? = null,
    val fab: @Composable (() -> Unit)? = null,
    val content: @Composable () -> Unit = {}
)