package com.nacro.video.ui.page

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.nacro.video.ui.theme.VideoAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getVideo()

        setContent {

            VideoAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val videos by viewModel.video.collectAsState()
                    PurePagerFromComposeUI(videos)
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        window.apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            WindowCompat.setDecorFitsSystemWindows(window, false)
            statusBarColor = Color.Transparent.toArgb()

        }
    }
}

