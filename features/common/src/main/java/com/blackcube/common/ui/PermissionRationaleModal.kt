package com.blackcube.common.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcube.common.R

/**
 * Displays a modal bottom sheet to explain why a certain permission is required.
 *
 * This composable displays a modal bottom sheet that informs the user about the need for a specific permission.
 * It includes a title, a detailed message, and two buttons: a positive action button (e.g., "Go to Settings") and a
 * negative action button (e.g., "Cancel").
 *
 * @param isVisible A boolean flag indicating whether the modal bottom sheet should be visible.
 * @param title The title of the dialog (e.g., "Permission Required").
 * @param message A detailed explanation of why the permission is needed.
 * @param positiveButtonText The text for the positive action button (e.g., "Go to Settings").
 * @param negativeButtonText The text for the negative action button (e.g., "Cancel").
 * @param onPositiveClick A callback function invoked when the positive action button is clicked.
 * @param onNegativeClick A callback function invoked when the negative action button is clicked.
 * @param onDismiss A callback function invoked when the modal bottom sheet is dismissed (e.g., by tapping outside the dialog).
 **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionRationaleModal(
    isVisible: Boolean,
    title: String,
    message: String,
    positiveButtonText: String = stringResource(R.string.open_settings),
    negativeButtonText: String = stringResource(R.string.cancel),
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = colorResource(id = R.color.white),
            windowInsets = WindowInsets(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.white))
                    .padding(horizontal = 20.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = title,
                    color = colorResource(id = R.color.title_color),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
                Text(
                    text = message,
                    color = colorResource(id = R.color.description_color),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomActionButton(
                    onClick = onPositiveClick,
                    backgroundColor = colorResource(id = R.color.purple),
                    textColor = colorResource(id = R.color.white),
                    text = positiveButtonText
                )
                Spacer(modifier = Modifier.height(6.dp))
                CustomActionButton(
                    onClick = onNegativeClick,
                    backgroundColor = colorResource(id = R.color.white),
                    textColor = colorResource(id = R.color.black),
                    text = negativeButtonText
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}
