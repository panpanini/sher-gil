package com.kinnerapriyap.sugar.mediagallery.cell

import android.net.Uri
import com.kinnerapriyap.sugar.choice.MimeType
import java.io.Serializable

data class MediaCellDisplayModel(
    val position: Int,
    val id: Long,
    val mediaUri: Uri,
    val isChecked: Boolean = false,
    val isEnabled: Boolean,
    val bucketDisplayName: String,
    val mimeType: MimeType?
) : Serializable
