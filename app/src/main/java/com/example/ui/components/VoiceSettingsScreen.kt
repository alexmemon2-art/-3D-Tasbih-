package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VoiceManager
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText
import java.util.Locale

@Composable
fun VoiceSettingsScreen(
    wallpaperIndex: Int,
    voices: List<VoiceManager.VoiceOption>,
    savedVoiceId: String,
    savedSpeed: Float,
    hasElevenLabsKey: Boolean,
    isOnline: Boolean = false,
    cachedVoiceCount: Int = 0,
    isPreloadingCache: Boolean = false,
    onPreloadOfflineVoices: (voiceId: String) -> Unit = {},
    onPreviewVoice: (voiceId: String, speed: Float) -> Unit,
    onStopVoice: () -> Unit,
    onSaveVoice: (voiceId: String, speed: Float) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var draftVoiceId by remember(savedVoiceId) { mutableStateOf(savedVoiceId) }
    var draftSpeed by remember(savedSpeed) { mutableFloatStateOf(savedSpeed.coerceIn(0.5f, 2.0f)) }

    val selectedVoiceOption = voices.find { it.id == draftVoiceId } ?: voices.first()
    val speedLabel = when {
        draftSpeed < 0.8f -> "🐢 Dheemi"
        draftSpeed <= 1.2f -> "🚶 Normal"
        else -> "🏃 Tez"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("voice_settings_screen")
    ) {
        AnimatedIslamicBackground(
            autoChangeSeconds = 30,
            currentWallpaperIndex = wallpaperIndex
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Glass3DCircleIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    isActive = false,
                    onClick = onBack,
                    testTag = "voice_settings_back_arrow"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎙️ Awaz Ka Intikhab",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "آواز کا انتخاب اور رفتار",
                        fontSize = 11.sp,
                        color = IvorySubtle
                    )
                }
                Spacer(modifier = Modifier.size(42.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Current Selection Summary Card
                item(key = "current_selection") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x36D4AF37), Color(0x1EFFFFFF), Color(0x100D4A2E))
                                )
                            )
                            .border(
                                1.2.dp,
                                Brush.verticalGradient(listOf(GoldBright.copy(alpha = 0.85f), GlassBorder)),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(16.dp)
                            .testTag("tv_current_voice_card")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✅ Current Selection",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (isOnline) Color(0x3310B981) else Color(0x33F59E0B)
                                        )
                                        .border(
                                            1.dp,
                                            if (isOnline) Color(0xFF34D399) else GoldBright,
                                            CircleShape
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isOnline) "🌐 Online • Cache: $cachedVoiceCount" else "📴 Offline • Cache: $cachedVoiceCount",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOnline) Color(0xFF6EE7B7) else GoldBright
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${selectedVoiceOption.icon} ${selectedVoiceOption.name}",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldBright
                            )
                            Text(
                                text = "${selectedVoiceOption.desc} • Speed: ${String.format(Locale.US, "%.1fx", draftSpeed)} ($speedLabel)",
                                fontSize = 12.sp,
                                color = IvoryText,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = "🔊 Har awaz Online aur Offline dono par kaam karti hai — suni hui ElevenLabs awazein offline cache mein save ho jati hain aur bina internet offline TTS fallback bhi chalta hai.",
                                fontSize = 11.sp,
                                color = IvorySubtle,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                            if (selectedVoiceOption.isOnline && !hasElevenLabsKey) {
                                Text(
                                    text = "ℹ️ ElevenLabs API key na hone par bhi Phone Ki Offline Awaz automatically play hogi.",
                                    fontSize = 11.sp,
                                    color = GoldLight,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            if (selectedVoiceOption.isOnline && hasElevenLabsKey && isOnline) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Glass3DButton(
                                    onClick = { onPreloadOfflineVoices(draftVoiceId) },
                                    modifier = Modifier.fillMaxWidth(),
                                    isPrimaryGold = false,
                                    enabled = !isPreloadingCache,
                                    height = 42.dp,
                                    testTag = "btn_preload_offline_voices"
                                ) {
                                    Text(
                                        text = if (isPreloadingCache) {
                                            "⏳ Core Azkar Offline Save Ho Rahe Hain..."
                                        } else {
                                            "📥 Core Azkar Offline Ke Liye Cache Karein"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldBright
                                    )
                                }
                            }
                        }
                    }
                }

                item(key = "voices_header") {
                    Text(
                        text = "Available Voices:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldLight,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(voices, key = { it.id }) { voice ->
                    val isSelected = draftVoiceId == voice.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    if (isSelected) {
                                        listOf(Color(0x36FFFFFF), Color(0x20D4AF37), Color(0x140D4A2E))
                                    } else {
                                        listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                                    }
                                )
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) GoldBright else GlassBorder,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { draftVoiceId = voice.id }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("voice_option_${voice.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = voice.icon,
                                fontSize = 26.sp,
                                modifier = Modifier.width(40.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = voice.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) GoldBright else IvoryText
                                )
                                Text(
                                    text = if (voice.isOnline) "🌐 ${voice.desc}" else voice.desc,
                                    fontSize = 12.sp,
                                    color = if (voice.isOnline) GoldLight else IvorySubtle,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { draftVoiceId = voice.id },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = GoldBright,
                                    unselectedColor = IvoryMuted
                                )
                            )
                        }
                    }
                }

                // Speed Slider Card
                item(key = "speed_slider_card") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                                )
                            )
                            .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = "Voice Speed",
                                        tint = GoldBright,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "⏱️ Awaz Ki Raftaar",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldBright
                                    )
                                }
                                Text(
                                    text = "$speedLabel (${String.format(Locale.US, "%.1fx", draftSpeed)})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight,
                                    modifier = Modifier.testTag("tv_voice_speed_label")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Slider(
                                value = draftSpeed,
                                onValueChange = { draftSpeed = it },
                                valueRange = 0.5f..2.0f,
                                steps = 5,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("slider_voice_speed"),
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldBright,
                                    activeTrackColor = GoldBright,
                                    inactiveTrackColor = Color(0x33FFFFFF)
                                )
                            )

                            Text(
                                text = "🐢 Dheemi (0.5x)   •   Normal (1.0x)   •   🏃 Tez (2.0x)",
                                fontSize = 11.sp,
                                color = IvorySubtle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Preview & Save Buttons
                item(key = "voice_actions") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Glass3DButton(
                                onClick = { onPreviewVoice(draftVoiceId, draftSpeed) },
                                modifier = Modifier.weight(1f),
                                isPrimaryGold = true,
                                height = 50.dp,
                                testTag = "btn_preview_voice"
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Preview Voice",
                                    tint = EmeraldDeepDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "🔊 SubhanAllah",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldDeepDark
                                )
                            }

                            Glass3DButton(
                                onClick = onStopVoice,
                                modifier = Modifier.width(96.dp),
                                isPrimaryGold = false,
                                height = 50.dp,
                                testTag = "btn_stop_voice"
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop Voice",
                                    tint = Color(0xFFFF8A80),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Stop",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF8A80)
                                )
                            }
                        }

                        Glass3DButton(
                            onClick = {
                                onSaveVoice(draftVoiceId, draftSpeed)
                                Toast.makeText(context, "✅ Awaz Save Ho Gai!", Toast.LENGTH_SHORT).show()
                                onBack()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isPrimaryGold = true,
                            height = 50.dp,
                            testTag = "btn_save_voice"
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save Voice Settings",
                                tint = EmeraldDeepDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "✅ Save Karein",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldDeepDark
                            )
                        }
                    }
                }

                item(key = "voice_bottom_spacer") {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Bottom Wapas Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Glass3DButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    height = 50.dp,
                    testTag = "voice_settings_wapas_button"
                ) {
                    Text(
                        text = "🔄 Wapas",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }
            }
        }
    }
}
