package com.blackcube.tours.route.components

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
import com.blackcube.common.ui.GradientLinearProgressIndicator
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

                    Box(modifier = Modifier.padding(start = 20.dp, bottom = 10.dp, end = 20.dp)) {
                        GradientLinearProgressIndicator(historyRouteModel.progress)
                    }
                }
            }
            itemsIndexed(historyRouteModel.histories, key = { _, item -> item.id }) { _, item ->
                HistoryItem(
                    number = item.ordinalNumber,
                    title = item.title,
                    description = item.description,
                    isCompleted = item.isCompleted,
                    onHistoryClick = { onHistoryItemClick.invoke(item) },
                    onOptionsClick = { onOptionsClick.invoke(item) }
                )
            }
        }
        if (!isTourStarted) {
            val buttonTitle = if (historyRouteModel.isTourStartedBefore) {
                stringResource(id = R.string.history_continue_button)
            } else {
                stringResource(id = R.string.history_route_start_button)
            }
            CustomActionButton(
                onClick = onStartTourClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 30.dp),
                backgroundColor = colorResource(com.blackcube.common.R.color.purple),
                textColor = Color.White,
                text = buttonTitle
            )
        }
    }
}

@Composable
fun HistoryItem(
    number: Int,
    title: String,
    description: String,
    isCompleted: Boolean,
    onHistoryClick: () -> Unit,
    onOptionsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(com.blackcube.common.R.color.white))
            .padding(
                vertical = 3.dp
            )
            .clickable { onHistoryClick.invoke() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp)
        ) {
            // 1 - текст, 2 - задний фон, 3 - обводка
            val colors: List<Color> = if (isCompleted) {
                listOf(
                    colorResource(com.blackcube.common.R.color.tour_status_green_text),
                    colorResource(com.blackcube.common.R.color.tour_status_green_background),
                    colorResource(com.blackcube.common.R.color.tour_status_green_background)
                )
            } else {
                listOf(
                    colorResource(com.blackcube.common.R.color.title_color),
                    colorResource(com.blackcube.common.R.color.white),
                    colorResource(com.blackcube.common.R.color.dark_purple)
                )
            }
            Text(
                text = "$number",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = colors[2],
                        shape = CircleShape
                    )
                    .background(colors[1])
                    .wrapContentSize(Alignment.Center),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors[0]
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
            progress = 0.5f,
            isTourStartedBefore = true,
            histories = listOf(
                HistoryModel(
                    id = "1",
                    ordinalNumber = 1,
                    title = "История 1",
                    isCompleted = true,
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