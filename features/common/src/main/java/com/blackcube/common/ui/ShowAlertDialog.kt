package com.blackcube.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.blackcube.common.R

@Composable
fun ShowAlertDialog(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.alert_base_title),
    description: String = stringResource(id = R.string.alert_base_description),
    onButtonClick: () -> Unit = {},
) {
    AlertDialog(
        title = {
            Text(
                text = title,
                modifier = Modifier
            )
        },
        text = {
            Text(
                text = description,
                modifier = Modifier
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onButtonClick,
                    modifier = Modifier,
                    shape = MaterialTheme.shapes.medium,
                    content = {
                        Text(
                            text = "Понятно",
                            modifier = Modifier
                        )
                    }
                )
            }
        },
        onDismissRequest = onButtonClick,
        modifier = modifier.testTag("debugTag:InfoDialog"),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        shape = MaterialTheme.shapes.medium,
    )
}