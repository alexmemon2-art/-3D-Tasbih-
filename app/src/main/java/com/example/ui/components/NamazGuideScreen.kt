package com.example.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NamazGuideData
import com.example.model.NamazGuideStep
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun NamazGuideScreen(
    wallpaperIndex: Int,
    onOpenQiblaCompass: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var completedSteps by remember { mutableStateOf(setOf<Int>()) }
    var showRakaatChart by remember { mutableStateOf(false) }

    val totalSteps = NamazGuideData.steps.size
    val progressFraction = if (totalSteps > 0) completedSteps.size.toFloat() / totalSteps else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("namaz_guide_screen")
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
            // Top Header
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
                    testTag = "namaz_guide_back_arrow"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🕌 Namaz Parhne Ka Tareeqa",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Pehlay Parhne Walo Ke Liye Aasan Guide",
                        fontSize = 11.sp,
                        color = IvorySubtle,
                        textAlign = TextAlign.Center
                    )
                }
                Glass3DCircleIconButton(
                    icon = Icons.Default.Explore,
                    contentDescription = "Open Qibla Compass",
                    isActive = false,
                    onClick = onOpenQiblaCompass,
                    testTag = "namaz_guide_qibla_btn"
                )
            }

            // Progress + Rakaat Chart Toggle Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Guide Progress: ${completedSteps.size} / $totalSteps Steps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Glass3DPill(
                        selected = showRakaatChart,
                        onClick = { showRakaatChart = !showRakaatChart },
                        testTag = "namaz_rakaat_toggle"
                    ) {
                        Text(
                            text = if (showRakaatChart) "Hide Rakaat Chart" else "📋 5 Namaz Rakaat",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showRakaatChart) EmeraldDeepDark else GoldBright
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GoldBright,
                    trackColor = Color(0x33FFFFFF)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showRakaatChart) {
                    item(key = "rakaat_chart_card") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0x30D4AF37), Color(0x18FFFFFF), Color(0x0E0D4A2E))
                                    )
                                )
                                .border(1.dp, GoldBright.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                                .padding(16.dp)
                                .testTag("namaz_rakaat_card")
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "📋 5 Waqt Ki Namaz Ke Rakaat",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldBright
                                )
                                HorizontalDivider(color = GlassBorder, thickness = 0.6.dp)
                                NamazGuideData.rakaatSummary.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = item.prayerName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldLight
                                        )
                                        Text(
                                            text = item.rakaatDetails,
                                            fontSize = 12.sp,
                                            color = IvoryText
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                items(NamazGuideData.steps, key = { it.stepNumber }) { step ->
                    val isDone = completedSteps.contains(step.stepNumber)
                    NamazStepCard(
                        step = step,
                        isCompleted = isDone,
                        onToggleComplete = {
                            completedSteps = if (isDone) {
                                completedSteps - step.stepNumber
                            } else {
                                completedSteps + step.stepNumber
                            }
                        }
                    )
                }

                item(key = "namaz_guide_footer") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✨ Prepared by: Usama Memon ✨",
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Har step par tap kar ke checklist mark karein",
                            fontSize = 11.sp,
                            color = IvoryMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
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
                    testTag = "namaz_guide_wapas_button"
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

@Composable
private fun NamazStepCard(
    step: NamazGuideStep,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    if (isCompleted) {
                        listOf(Color(0x32D4AF37), Color(0x1CFFFFFF), Color(0x100D4A2E))
                    } else {
                        listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                    }
                )
            )
            .border(
                1.dp,
                if (isCompleted) {
                    Brush.verticalGradient(listOf(GoldBright, GlassBorder))
                } else {
                    Brush.verticalGradient(listOf(Color(0x55FFFFFF), GlassBorder, Color(0x18000000)))
                },
                RoundedCornerShape(20.dp)
            )
            .clickable { onToggleComplete() }
            .padding(16.dp)
            .testTag("namaz_step_card_${step.stepNumber}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.25f))
                            .border(1.dp, GoldBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = step.stepNumber.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = step.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )
                        Text(
                            text = step.urduTitle,
                            fontSize = 12.sp,
                            color = GoldLight
                        )
                    }
                }

                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = if (isCompleted) "Step completed" else "Mark step completed",
                    tint = if (isCompleted) GoldBright else IvoryMuted,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (!step.arabicSnippet.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x28000000))
                        .border(0.8.dp, GlassBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = step.arabicSnippet,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = IvoryText,
                        textAlign = TextAlign.Center,
                        lineHeight = 29.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = GlassBorder, thickness = 0.6.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                step.instructions.forEach { bullet ->
                    Text(
                        text = "• $bullet",
                        fontSize = 13.sp,
                        color = IvoryText,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}
