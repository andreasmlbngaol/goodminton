package com.mightsana.goodminton.features.main.home.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mightsana.goodminton.R
import com.mightsana.goodminton.features.main.home.HomeViewModel
import com.mightsana.goodminton.model.ext.onTap
import com.mightsana.goodminton.model.values.Size
import com.mightsana.goodminton.view.ErrorSupportingText
import com.mightsana.goodminton.view.MyIcon
import com.mightsana.goodminton.view.MyIcons
import com.mightsana.goodminton.view.MyTextField
import kotlinx.coroutines.launch

@Composable
fun NewLeagueFab(
    expanded: Boolean,
    onClick: suspend () -> Unit
) {
    val scope = rememberCoroutineScope()
    AnimatedVisibility(!expanded) {
        ExtendedFloatingActionButton(
            text = { Text(stringResource(R.string.new_league_fab_label)) },
            icon = { MyIcon(MyIcons.Plus) },
            onClick = { scope.launch { onClick() } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLeagueSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    viewModel: HomeViewModel
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
                onDismiss()
            }
        },
        sheetState = sheetState
    ) { NewLeagueSheetContent(sheetState, viewModel) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewLeagueSheetContent(
    sheetState: SheetState,
    viewModel: HomeViewModel
) {
    val scope = rememberCoroutineScope()
    val isDouble by viewModel.isDouble.collectAsState()
    val matchPoints by viewModel.matchPoints.collectAsState()
    val isPrivate by viewModel.isPrivate.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(Size.padding)
                .widthIn(max = 350.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Size.smallPadding)
        ) {

            // Title
            Text(
                text = stringResource(R.string.add_new_league),
                style = MaterialTheme.typography.titleLarge,
                textDecoration = TextDecoration.Underline
            )

            // League Name
            val nameErrorMessage by viewModel.nameErrorMessage.collectAsState()
            MyTextField(
                isError = nameErrorMessage != null,
                label = { Text(stringResource(R.string.league_name_label)) },
                value = viewModel.leagueName.collectAsState().value,
                onValueChange = { viewModel.updateLeagueName(it) },
                modifier = Modifier.fillMaxWidth(),
                supportingText = nameErrorMessage?.let {
                    { ErrorSupportingText(message = it) }
                }
            )

            // Match Points
            val matchPointsErrorMessage by viewModel.matchPointsErrorMessage.collectAsState()
            MyTextField(
                isError = matchPointsErrorMessage != null,
                label = { Text(stringResource(R.string.match_points_label)) },
                value = if (matchPoints == 0) "" else matchPoints.toString(),
                onValueChange = { viewModel.updateMatchPoints(it) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Number
                ),
                placeholder = { Text(stringResource(R.string.match_points_placeholder)) },
                supportingText = matchPointsErrorMessage?.let {
                    { ErrorSupportingText(message = it) }
                }
            )

            // Visibility
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = Size.smallPadding)
                    .onTap { viewModel.togglePrivate() }
            ) {
                Text(stringResource(R.string.public_text))
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isPrivate,
                    onCheckedChange = { viewModel.togglePrivate() }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(stringResource(R.string.private_league_label))
            }

            // Deuce
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.enabled_deuce_label))
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
                    .padding(horizontal = Size.smallPadding)
                    .onTap { viewModel.toggleDouble() }
            ) {
                Text(stringResource(R.string.single_label))
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isDouble,
                    onCheckedChange = { viewModel.toggleDouble() }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(stringResource(R.string.double_label))
            }

            // Fixed Double?
            AnimatedVisibility(isDouble) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Size.smallPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.fixed_double_label))
                    Switch(
                        checked = viewModel.isFixedDouble.collectAsState().value,
                        onCheckedChange = { viewModel.toggleFixedDouble() }
                    )
                }
            }

            // Confirm Button
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.spacedBy(Size.smallPadding)
            ) {
                OutlinedButton({ viewModel.resetForm() }) { Text(stringResource(R.string.reset_button_label)) }
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
                ) { Text(stringResource(R.string.save_button_label)) }
            }
        }
    }
}