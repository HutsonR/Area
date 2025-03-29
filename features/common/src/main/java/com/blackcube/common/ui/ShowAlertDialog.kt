package com.blackcube.common.ui

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
    alertData: AlertData = AlertData(),
    onCancelButtonClick: () -> Unit = {},
    onActionButtonClick: () -> Unit
) {
    val title = alertData.title.ifEmpty { stringResource(id = R.string.alert_base_title) }
    val description = alertData.message.ifEmpty { stringResource(id = R.string.alert_base_description) }
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
        dismissButton = if (alertData.isCancelable) {
            {
                TextButton(
                    onClick = onCancelButtonClick,
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
                            text = stringResource(id = R.string.cancel),
                            modifier = Modifier
                        )
                    }
                )
            }
        } else null,
        confirmButton = {
            TextButton(
                onClick = onActionButtonClick,
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
        },
        onDismissRequest = onActionButtonClick,
        containerColor = colorResource(R.color.main_background),
        modifier = modifier.testTag("debugTag:InfoDialog"),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        shape = MaterialTheme.shapes.medium,
    )
}

data class AlertData(
    val title: String = "",
    val message: String = "",
    val isCancelable: Boolean = false,
    val action: (() -> Unit)? = null
)