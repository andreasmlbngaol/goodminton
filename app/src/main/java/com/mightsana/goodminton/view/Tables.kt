package com.mightsana.goodminton.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.breens.beetablescompose.components.TableHeaderComponent
import com.breens.beetablescompose.components.TableHeaderComponentWithoutColumnDividers
import com.breens.beetablescompose.utils.extractMembers

@Composable
inline fun <reified T : Any> Tables(
    data: List<T>,
    enableTableHeaderTitles: Boolean = true,
    headerTableTitles: List<String>,
    headerTitlesBorderColor: Color = MaterialTheme.colorScheme.surface,
    headerTitlesTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    headerTitlesBackGroundColor: Color = MaterialTheme.colorScheme.background,
    tableRowColors: List<Color> = listOf(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.background,
    ),
    rowBorderColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    rowTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    tableElevation: Dp = 0.dp,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp),
    borderStroke: BorderStroke = BorderStroke(
        width = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ),
    disableVerticalDividers: Boolean = false,
    dividerThickness: Dp = 1.dp,
    horizontalDividerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentAlignment: Alignment = Alignment.Center,
    textAlign: TextAlign = TextAlign.Center,
    tablePadding: Dp = 0.dp,
    columnToIndexIncreaseWidth: Int? = null,
    columnToColorModified: Map<Int, Color> = emptyMap(),
    columnToFontWeightModified: Map<Int, FontWeight> = emptyMap(),
//    headerScrollableHide: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = tableElevation),
        shape = shape,
        border = borderStroke,
        modifier = modifier
    ) {
        Column {
            if (enableTableHeaderTitles) {
                if (disableVerticalDividers) {
                    TableHeaderComponentWithoutColumnDividers(
                        headerTableTitles = headerTableTitles,
                        headerTitlesTextStyle = headerTitlesTextStyle,
                        headerTitlesBackGroundColor = headerTitlesBackGroundColor,
                        dividerThickness = dividerThickness,
                        contentAlignment = contentAlignment,
                        textAlign = textAlign,
                        tablePadding = tablePadding,
                        columnToIndexIncreaseWidth = columnToIndexIncreaseWidth,
                    )
                } else {
                    TableHeaderComponent(
                        headerTableTitles = headerTableTitles,
                        headerTitlesBorderColor = headerTitlesBorderColor,
                        headerTitlesTextStyle = headerTitlesTextStyle,
                        headerTitlesBackGroundColor = headerTitlesBackGroundColor,
                        contentAlignment = contentAlignment,
                        textAlign = textAlign,
                        tablePadding = tablePadding,
                        dividerThickness = dividerThickness,
                        columnToIndexIncreaseWidth = columnToIndexIncreaseWidth,
                    )
                }
            }

            Column {
                data.forEachIndexed { index, data ->
                    val rowData = extractMembers(data).map {
                        it.second // getting the value from the returned Pair
                    }

                    // alternate background colors between rows
                    val tableRowBackgroundColor = if (index % 2 == 0) {
                        tableRowColors[0]
                    } else {
                        tableRowColors[1]
                    }

                    if (disableVerticalDividers) {
                        TableRowComponentWithoutDividers(
                            data = rowData,
                            rowTextStyle = rowTextStyle,
                            rowBackGroundColor = tableRowBackgroundColor,
                            dividerThickness = dividerThickness,
                            horizontalDividerColor = horizontalDividerColor,
                            contentAlignment = contentAlignment,
                            textAlign = textAlign,
                            tablePadding = tablePadding,
                            columnToColorModified = columnToColorModified,
                            columnToIndexIncreaseWidth = columnToIndexIncreaseWidth,
                            columnToFontWeightModified = columnToFontWeightModified
                        )
                    } else {
                        TableRowComponent(
                            data = rowData,
                            rowBorderColor = rowBorderColor,
                            dividerThickness = dividerThickness,
                            rowTextStyle = rowTextStyle,
                            rowBackGroundColor = tableRowBackgroundColor,
                            contentAlignment = contentAlignment,
                            textAlign = textAlign,
                            tablePadding = tablePadding,
                            columnToColorModified = columnToColorModified,
                            columnToIndexIncreaseWidth = columnToIndexIncreaseWidth,
                            columnToFontWeightModified = columnToFontWeightModified
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableRowComponentWithoutDividers(
    data: List<String>,
    rowTextStyle: TextStyle,
    rowBackGroundColor: Color,
    dividerThickness: Dp,
    horizontalDividerColor: Color,
    contentAlignment: Alignment,
    textAlign: TextAlign,
    tablePadding: Dp,
    columnToIndexIncreaseWidth: Int?,
    columnToColorModified: Map<Int, Color> = emptyMap(),
    columnToFontWeightModified: Map<Int, FontWeight> = emptyMap()
) {
    Column(
        modifier = Modifier.padding(horizontal = tablePadding),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(rowBackGroundColor),
        ) {
            data.forEachIndexed { index, title ->
                val weight = if (index == columnToIndexIncreaseWidth) 8f else 2f
                Box(
                    modifier = Modifier
                        .weight(weight),
                    contentAlignment = contentAlignment,
                ) {
                    Text(
                        text = title,
                        style = rowTextStyle,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .height(38.dp)
                            .wrapContentHeight()
                            .padding(end = 8.dp),
                        textAlign = textAlign,
                        color = columnToColorModified[index] ?: Color.Unspecified,
                        fontWeight = columnToFontWeightModified[index] ?: FontWeight.Normal
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(dividerThickness)
                .background(horizontalDividerColor),
        )
    }
}

@Composable
fun TableRowComponent(
    data: List<String>,
    rowBorderColor: Color,
    rowTextStyle: TextStyle,
    rowBackGroundColor: Color,
    contentAlignment: Alignment,
    textAlign: TextAlign,
    tablePadding: Dp,
    columnToIndexIncreaseWidth: Int?,
    dividerThickness: Dp,
    columnToColorModified: Map<Int, Color> = emptyMap(),
    columnToFontWeightModified: Map<Int, FontWeight> = emptyMap(),
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(rowBackGroundColor)
            .padding(tablePadding),
    ) {
        data.forEachIndexed { index, title ->
            val weight = if (index == columnToIndexIncreaseWidth) 8f else 2f
            Box(
                modifier = Modifier
                    .weight(weight)
                    .border(
                        width = dividerThickness,
                        color = rowBorderColor,
                    ),
                contentAlignment = contentAlignment,
            ) {
                Text(
                    text = title,
                    style = rowTextStyle,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .height(38.dp)
                        .wrapContentHeight(),
                    textAlign = textAlign,
                    color = columnToColorModified[index] ?: Color.Unspecified,
                    fontWeight = columnToFontWeightModified[index] ?: FontWeight.Normal
                )
            }
        }
    }
}
