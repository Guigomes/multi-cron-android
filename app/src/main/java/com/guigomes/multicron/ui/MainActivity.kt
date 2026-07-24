package com.guigomes.multicron.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guigomes.multicron.ui.theme.MultiCronTheme
import com.guigomes.multicron.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiCronTheme {
                val mainViewModel: MainViewModel = viewModel()
                MultiCronScreen(viewModel = mainViewModel)
            }
        }
    }
}
