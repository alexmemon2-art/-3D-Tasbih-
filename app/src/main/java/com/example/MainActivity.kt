package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.notifications.TasbihReminderReceiver
import com.example.ui.TasbihScreen
import com.example.ui.theme.TasbihTheme
import com.example.viewmodel.TasbihViewModel
import com.example.widget.TasbihWidgetUpdater

class MainActivity : ComponentActivity() {
    private val viewModel: TasbihViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        TasbihReminderReceiver.ensureChannels(this)
        setContent {
            TasbihTheme {
                TasbihScreen(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.syncFromPreferences(resetSessionCount = false)
        TasbihWidgetUpdater.updateAllWidgets(this)
    }
}
