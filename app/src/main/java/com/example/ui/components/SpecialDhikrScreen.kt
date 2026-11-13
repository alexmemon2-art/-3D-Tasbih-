package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.SpecialDhikr
import com.example.model.SpecialDhikrsData
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun SpecialDhikrScreen(
    wallpaperIndex: Int,
    tasbihFatimaTotalCount: Int,
    onTapTasbihFatima: () -> Unit,
    onResetTasbihFatima: () -> Unit,
    onSelectSpecialDhikr: (SpecialDhikr) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("special_dhikr_screen")
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
                    testTag = "special_dhikr_back_button"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📿 Tasbih-e-Fatima & Special",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "تسبیحِ فاطمہؓ (33 + 33 + 34) اور خاص اذکار",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Interactive Tasbih-e-Fatima Card (33 + 33 + 34 = 100)
                item(key = "tasbih_fatima_card") {
                    TasbihFatimaCard(
                        totalProgress = tasbihFatimaTotalCount,
                        onTap = onTapTasbihFatima,
                        onReset = onResetTasbihFatima
                    )
                }

                item(key = "special_section_header") {
                    Text(
                        text = "✨ Khas Azkar & Wazaif (Tap to Count)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldLight,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                items(SpecialDhikrsData.allSpecialDhikrs, key = { it.id }) { dhikr ->
                    SpecialDhikrCard(
                        dhikr = dhikr,
                        onStart = { onSelectSpecialDhikr(dhikr) }
                    )
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
                    testTag = "special_dhikr_wapas_button"
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
private fun TasbihFatimaCard(
    totalProgress: Int,
    onTap: () -> Unit,
    onReset: () -> Unit
) {
    val clamped = totalProgress.coerceIn(0, SpecialDhikrsData.TASBIH_FATIMA_TOTAL)
    val isComplete = clamped >= SpecialDhikrsData.TASBIH_FATIMA_TOTAL

    val step1Count = clamped.coerceIn(0, 33)
    val step2Count = (clamped - 33).coerceIn(0, 33)
    val step3Count = (clamped - 66).coerceIn(0, 34)

    val activeStep = when {
        clamped < 33 -> SpecialDhikrsData.tasbihFatimaSteps[0]
        clamped < 66 -> SpecialDhikrsData.tasbihFatimaSteps[1]
        else -> SpecialDhikrsData.tasbihFatimaSteps[2]
    }
    val activeStepCount = when {
        clamped < 33 -> step1Count
        clamped < 66 -> step2Count
        else -> step3Count
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x36D4AF37), Color(0x1EFFFFFF), Color(0x100D4A2E))
                )
            )
            .border(
                1.2.dp,
                Brush.verticalGradient(listOf(GoldBright.copy(alpha = 0.8f), GlassBorder)),
                RoundedCornerShape(22.dp)
            )
            .padding(16.dp)
            .testTag("tasbih_fatima_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🌸 Tasbih-e-Fatima (رضي الله عنها)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "Har farz namaz ke baad & sone se pehle • $clamped / 100",
                        fontSize = 11.sp,
                        color = IvorySubtle
                    )
                }

                Glass3DCircleIconButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = "Reset Tasbih-e-Fatima",
                    isActive = false,
                    onClick = onReset,
                    testTag = "tasbih_fatima_reset"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3-step indicator pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val counts = listOf(step1Count, step2Count, step3Count)
                SpecialDhikrsData.tasbihFatimaSteps.forEachIndexed { idx, step ->
                    val countVal = counts[idx]
                    val stepDone = countVal >= step.target
                    val isCurrent = !isComplete && activeStep.stepNumber == step.stepNumber
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    stepDone -> GoldBright.copy(alpha = 0.9f)
                                    isCurrent -> Color(0x35FFFFFF)
                                    else -> Color(0x16000000)
                                }
                            )
                            .border(
                                1.dp,
                                if (isCurrent || stepDone) GoldBright else GlassBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 7.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = step.title,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (stepDone) EmeraldDeepDark else IvoryText,
                                maxLines = 1
                            )
                            Text(
                                text = "$countVal / ${step.target}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stepDone) EmeraldDeepDark else GoldLight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current Arabic Zikr display
            Text(
                text = if (isComplete) "مَا شَاءَ اللَّهُ • تَسْبِيح مُكَمَّل" else activeStep.arabic,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GoldBright,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = if (isComplete) {
                    "Tasbih-e-Fatima (100/100) Mukammal! Reset dabakar dubara parhein."
                } else {
                    "${activeStep.title} ($activeStepCount / ${activeStep.target}) — ${activeStep.romanUrdu}"
                },
                fontSize = 12.sp,
                color = IvoryText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { clamped / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GoldBright,
                trackColor = Color(0x33FFFFFF)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Glass3DButton(
                onClick = if (isComplete) onReset else onTap,
                modifier = Modifier.fillMaxWidth(),
                isPrimaryGold = true,
                height = 52.dp,
                testTag = "tasbih_fatima_tap_button"
            ) {
                if (isComplete) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = EmeraldDeepDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MashaAllah Complete (100/100) • Tap to Restart",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDeepDark
                    )
                } else {
                    Text(
                        text = "📿 Tap Bead (+1) • ${activeStep.title}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDeepDark
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialDhikrCard(
    dhikr: SpecialDhikr,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x55FFFFFF), GlassBorder, Color(0x18000000))),
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
            .testTag("special_dhikr_card_${dhikr.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dhikr.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = dhikr.subtitleUrdu,
                        fontSize = 12.sp,
                        color = GoldLight
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoldPrimary.copy(alpha = 0.25f))
                        .border(1.dp, GoldBright, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${dhikr.recommendedCount}x",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = dhikr.arabic,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = IvoryText,
                textAlign = TextAlign.Center,
                lineHeight = 33.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = dhikr.transliteration,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = GoldLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = GlassBorder, thickness = 0.6.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dhikr.meaning,
                fontSize = 12.sp,
                color = IvoryText,
                lineHeight = 18.sp
            )
            Text(
                text = "✨ Fazilat: ${dhikr.fazilat}",
                fontSize = 11.sp,
                color = IvorySubtle,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "📚 ${dhikr.reference}",
                fontSize = 10.sp,
                color = IvoryMuted,
                modifier = Modifier.padding(top = 3.dp, bottom = 10.dp)
            )

            Glass3DPill(
                selected = true,
                onClick = onStart,
                modifier = Modifier.fillMaxWidth(),
                testTag = "special_dhikr_start_${dhikr.id}"
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start in 3D Tasbih Counter",
                    tint = EmeraldDeepDark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "3D Tasbih Counter Mein Parhein (${dhikr.recommendedCount}x)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldDeepDark
                )
            }
        }
    }
}
