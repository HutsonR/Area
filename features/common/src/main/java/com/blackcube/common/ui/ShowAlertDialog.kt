package com.blackcube.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
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
                    colors = ButtonColors(
                        containerColor = colorResource(R.color.main_background),
                        contentColor = colorResource(R.color.purple),
                        disabledContainerColor = colorResource(R.color.main_background),
                        disabledContentColor = colorResource(R.color.purple)
                    ),
                    content = {
                        Text(
                            text = stringResource(id = R.string.understand),
                            modifier = Modifier
                        )
                    }
                )
            }
        },
        onDismissRequest = onButtonClick,
        containerColor = colorResource(R.color.main_background),
        modifier = modifier.testTag("debugTag:InfoDialog"),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        shape = MaterialTheme.shapes.medium,
    )
}