package com.mightsana.goodminton.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun MyIcon(
    imageVector: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}

@Suppress("unused")
object MyIcons {
    val Invitation: ImageVector = Icons.Default.Email
    val SocialSelected: ImageVector = Icons.Filled.PeopleAlt
    val SocialUnselected: ImageVector = Icons.Outlined.PeopleAlt
    val Delete: ImageVector = Icons.Filled.Delete
    val Flip: ImageVector = Icons.Default.FlipCameraAndroid
    val Copy: ImageVector = Icons.Default.CopyAll
    val Anonymous: ImageVector = Icons.Default.NoAccounts
    val Gallery: ImageVector = Icons.Default.PhotoAlbum
    val Camera: ImageVector = Icons.Default.Camera
    val Edit: ImageVector = Icons.Outlined.Edit
    val Cancel: ImageVector = Icons.Default.Close
    val Back: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val Plus: ImageVector = Icons.Default.Add
    val Minus: ImageVector = Icons.Default.Remove
    val DashboardSelected: ImageVector = Icons.Filled.Home
    val DashboardUnselected: ImageVector = Icons.Outlined.Home
    val TransactionsSelected: ImageVector = Icons.Filled.Analytics
    val TransactionsUnselected: ImageVector = Icons.Outlined.Analytics
    val WalletsSelected: ImageVector = Icons.Filled.Savings
    val WalletsUnselected: ImageVector = Icons.Outlined.Savings
    val DebtsSelected: ImageVector = Icons.Filled.MonetizationOn
    val DebtsUnselected: ImageVector = Icons.Outlined.MonetizationOn
    val Profile: ImageVector = Icons.Filled.Person
    val Email: ImageVector = Icons.Default.AlternateEmail
    val Logout: ImageVector = Icons.AutoMirrored.Filled.Logout
    val Password: ImageVector = Icons.Default.Password
    val ConfirmPassword: ImageVector = Icons.Default.Password
    val PasswordVisible: ImageVector = Icons.Filled.Visibility
    val PasswordNotVisible: ImageVector = Icons.Filled.VisibilityOff
    val EmailVerification: ImageVector = Icons.Default.MarkEmailUnread
    val Name: ImageVector = Icons.Default.Person2
    val Gender: ImageVector = Icons.Default.Wc
    val Male: ImageVector = Icons.Default.Man
    val Female: ImageVector = Icons.Default.Woman
    val DropdownExpanded: ImageVector = Icons.Default.ArrowDropUp
    val DropdownCollapsed: ImageVector = Icons.Default.ArrowDropDown
    fun dropdown(expanded: Boolean): ImageVector = if (expanded) DropdownExpanded else DropdownCollapsed
    val DatePicker: ImageVector = Icons.Default.DateRange
    val Secret: ImageVector = Icons.Default.QuestionMark
    val SearchExpanded: ImageVector = Icons.Filled.Search
    val SearchCollapsed: ImageVector = Icons.Outlined.SearchOff
    val Notification: ImageVector = Icons.Outlined.Notifications
}