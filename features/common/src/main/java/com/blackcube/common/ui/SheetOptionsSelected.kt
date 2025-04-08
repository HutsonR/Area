package com.blackcube.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcube.common.R

@Composable
fun SheetOptionsSelected(
    items: List<OptionsModel>,
    onClick: (item: OptionsModel) -> Unit,
) {
    Box {
        LazyColumn(
            modifier = Modifier
                .navigationBarsPadding()
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.dialog_options_title),
                    color = colorResource(id = R.color.title_color),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(top = 16.dp, bottom = 2.dp),
                    thickness = 1.dp,
                    color = colorResource(id = R.color.divider_color)
                )
            }
            itemsIndexed(items, key = { _, item -> item.id }) { _, item ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClick(item) }
                        .padding(vertical = 14.dp, horizontal = 32.dp),
                    text = item.title,
                    color = colorResource(R.color.title_color),
                    fontSize = 16.sp
                )
            }
            item {
                Spacer(modifier = Modifier.padding(bottom = 20.dp))
            }
        }
    }
}

data class OptionsModel(
    val id: String,
    val value: String = "",
    val title: String
)