package com.blackcube.common.utils.map

import android.content.Context
import android.content.Intent
import android.net.Uri

object MapUtil {
    fun navigateToMap(request: String, context: Context) {
        Uri.parse(request).let {
            val intent = Intent(Intent.ACTION_VIEW, it)
            context.startActivity(intent)
        }
    }
}