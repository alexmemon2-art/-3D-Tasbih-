package com.example

import android.os.Bundle
import android.view.KeyEvent
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (viewModel.uiState.value.settings.volumeKeyCount) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    viewModel.increment()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
