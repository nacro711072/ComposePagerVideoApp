package com.nacro.video.model.domain

import android.net.Uri

data class VideoData(
    val id: String,
    val title: String,
) {

    companion object {
//        private const val VIDEO_PATH = "http://storage.googleapis.com/pst-framy/vdo/%s.mp4"
        private const val SCREENSHOT_PATH = "http://storage.googleapis.com/pst-framy/stk/%s.jpg"
    }

//    val videoUri: Uri = Uri.parse(VIDEO_PATH.format(id))
    val videoUri: Uri = Uri.parse("file:///android_asset/$id.mp4");

    val screenshotPath = SCREENSHOT_PATH.format(id)
}
