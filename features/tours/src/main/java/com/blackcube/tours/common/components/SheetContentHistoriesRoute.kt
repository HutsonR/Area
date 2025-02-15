package com.blackcube.tours.common.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import com.blackcube.common.ui.CustomActionButtonWithIcon
import com.blackcube.tours.R
import com.blackcube.tours.common.models.HistoryModel
import com.blackcube.tours.common.models.HistoryRouteModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SheetContentHistoriesRoute(
    historyRouteModel: HistoryRouteModel
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .navigationBarsPadding(),
    ) {
        stickyHeader {
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
                onClick = { },
                number = index + 1,
                title = item.title,
                description = item.description
            )
        }
    }
}

@Composable
fun HistoryItem(
    onClick: () -> Unit,
    number: Int,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 3.dp
            )
            .clickable { onClick.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "${number}.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(com.blackcube.common.R.color.title_color),
            )
            Column(
                modifier = Modifier.padding(start = 10.dp)
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
                    description = "Описание истории 1",
                    lat = 0.0,
                    lon = 0.0
                )
            )
        )
    )
}