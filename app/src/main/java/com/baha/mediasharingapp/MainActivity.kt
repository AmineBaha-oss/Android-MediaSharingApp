package com.baha.mediasharingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.baha.mediasharingapp.ui.theme.MediaSharingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaSharingAppTheme {
                AppNavigation()
            }
        }
    }
}
