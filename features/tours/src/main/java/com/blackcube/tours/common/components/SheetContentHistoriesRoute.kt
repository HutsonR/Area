package com.blackcube.tours.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcube.common.ui.CustomActionButton
import com.blackcube.models.tours.HistoryModel
import com.blackcube.tours.R
import com.blackcube.tours.common.models.HistoryRouteModel

@Composable
fun SheetContentHistoriesRoute(
    historyRouteModel: HistoryRouteModel,
    isTourStarted: Boolean = false,
    onStartTourClick: () -> Unit,
    onHistoryItemClick: (HistoryModel) -> Unit,
    onOptionsClick: (HistoryModel) -> Unit
) {
    Box {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(
                    bottom = if (isTourStarted) 0.dp else 80.dp
                )
        ) {
            item {
                // todo добавить расстояние и прогресс прохождения
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(id = com.blackcube.common.R.color.white))
                ) {
                    Text(
                        text = stringResource(id = R.string.history_route_title),
                        color = colorResource(id = com.blackcube.common.R.color.title_color),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }
            }
            itemsIndexed(historyRouteModel.histories, key = { _, item -> item.id }) { index, item ->
                HistoryItem(
                    number = index + 1,
                    title = item.title,
                    description = item.description,
                    onHistoryClick = { onHistoryItemClick.invoke(item) },
                    onOptionsClick = { onOptionsClick.invoke(item) }
                )
            }
        }
        if (!isTourStarted) {
            CustomActionButton(
                onClick = onStartTourClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 30.dp),
                backgroundColor = colorResource(com.blackcube.common.R.color.purple),
                textColor = Color.White,
                text = stringResource(id = R.string.history_route_start_button)
            )
        }
    }
}

@Composable
fun HistoryItem(
    number: Int,
    title: String,
    description: String,
    onHistoryClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(com.blackcube.common.R.color.white))
            .padding(
                vertical = 3.dp
            )
            .clickable { onHistoryClick.invoke() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "$number",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = colorResource(id = com.blackcube.common.R.color.dark_purple),
                        shape = CircleShape
                    )
                    .wrapContentSize(Alignment.Center),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = com.blackcube.common.R.color.title_color)
            )
            Column(
                modifier = Modifier.padding(start = 10.dp).weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(com.blackcube.common.R.color.title_color),
                )
                Text(
                    modifier = Modifier.padding(top = 6.dp),
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(com.blackcube.common.R.color.description_color),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onOptionsClick) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Control Button",
                    tint = colorResource(id = com.blackcube.common.R.color.gray),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewScreenHistoriesRoute() {
    SheetContentHistoriesRoute(
        historyRouteModel = HistoryRouteModel(
            id = "1",
            histories = listOf(
                HistoryModel(
                    id = "1",
                    title = "История 1",
                    isCompleted = false,
                    description = "Описание истории djsdjs djsldl djskdjasl ddjd kajsdlj kldlda l 1",
                    lat = 0.0,
                    lon = 0.0
                )
            )
        ),
        onStartTourClick = {},
        onHistoryItemClick = {},
        onOptionsClick = {}
    )
}