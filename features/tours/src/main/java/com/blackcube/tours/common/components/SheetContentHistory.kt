package com.blackcube.tours.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcube.common.ui.CustomActionButtonWithIcon
import com.blackcube.tours.R
import com.blackcube.tours.common.components.player.AudioPlayer
import com.blackcube.tours.common.models.HistoryModel
import com.blackcube.tours.common.models.Track

@Composable
fun SheetContentHistory(
    historyModel: HistoryModel,
    onClickShowMap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = com.blackcube.common.R.color.white))
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
    ) {
        val path = "android.resource://" + "com.blackcube.area" + "/" + R.raw.track
        AudioPlayer(
            track = Track(
                name = historyModel.title,
                url = path
            )
        )
        Text(
            text = historyModel.title,
            color = colorResource(id = com.blackcube.common.R.color.title_color),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top= 20.dp, bottom = 20.dp)
        )
        Text(
            text = historyModel.description,
            color = colorResource(id = com.blackcube.common.R.color.description_color),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
        )
        Spacer(modifier = Modifier.height(50.dp))
        CustomActionButtonWithIcon(
            onClick = { onClickShowMap.invoke() },
            backgroundColor = colorResource(id = com.blackcube.common.R.color.button_background_gray),
            textColor = colorResource(id = com.blackcube.common.R.color.black),
            iconColor = colorResource(id = com.blackcube.common.R.color.black),
            text = stringResource(id = R.string.history_detail_show_map),
            icon = Icons.Default.Map,
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}


@Preview
@Composable
fun PreviewScreenContentHistory() {
    SheetContentHistory(
        historyModel = HistoryModel(
            id = "1",
            title = "Какой-то заголовок истории",
            description = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum",
            lat = 0.0,
            lon = 0.0
        ),
        onClickShowMap = {}
    )
}