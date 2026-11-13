package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.export.TasbihExportHelper
import com.example.model.DailyDayStat
import com.example.model.Dhikr
import com.example.model.DhikrLevelInfo
import com.example.model.HistoryItem
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun HistoryStatsScreen(
    wallpaperIndex: Int,
    todayCount: Int,
    yesterdayCount: Int,
    weekCount: Int,
    streakDays: Int,
    totalAllTime: Long,
    dailyGoal: Int,
    levelInfo: DhikrLevelInfo,
    weeklyBreakdown: List<DailyDayStat>,
    dhikrs: List<Dhikr>,
    dhikrLifetimes: Map<String, Long>,
    recentHistory: List<HistoryItem>,
    onUpdateDailyGoal: (Int) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedGoal by remember(dailyGoal) { mutableIntStateOf(dailyGoal) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_stats_screen")
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
                    testTag = "history_back_button"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📊 تسبیح ریکارڈ اور اہداف",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "Daily Stats, Streaks, Levels & Export",
                        fontSize = 11.sp,
                        color = IvorySubtle
                    )
                }
                Spacer(modifier = Modifier.size(42.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak + Level Banner
                StatsGlassCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${levelInfo.badge} ${levelInfo.title}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldBright
                                )
                                Text(
                                    text = "Kul Zikr: $totalAllTime • Next Milestone: ${levelInfo.nextLevelThreshold}",
                                    fontSize = 11.sp,
                                    color = IvorySubtle
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.22f))
                                    .border(1.dp, GoldBright, CircleShape)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🔥 $streakDays Din",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldBright
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Level Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(levelInfo.progressFraction.coerceIn(0.03f, 1f))
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(Brush.horizontalGradient(listOf(GoldDeep, GoldBright)))
                            )
                        }
                    }
                }

                // Aaj, Kal, Is Haftay, Kul Zikr Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniStatTile(
                        title = "📅 Aaj (Today)",
                        value = todayCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatTile(
                        title = "🕓 Kal (Yesterday)",
                        value = yesterdayCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniStatTile(
                        title = "📆 Is Haftay (Week)",
                        value = weekCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MiniStatTile(
                        title = "✨ Kul Zikr (Lifetime)",
                        value = totalAllTime.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Daily Goal Progress & Challenge Selector
                StatsGlassCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val goalPct = ((todayCount.toFloat() / selectedGoal.coerceAtLeast(1)) * 100f)
                            .toInt()
                            .coerceIn(0, 100)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌳 Rozana Hadaf (Daily Goal)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldBright
                            )
                            Text(
                                text = "$todayCount / $selectedGoal ($goalPct%)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.14f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((todayCount.toFloat() / selectedGoal.coerceAtLeast(1)).coerceIn(0.02f, 1f))
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(
                                        if (todayCount >= selectedGoal)
                                            Brush.horizontalGradient(listOf(Color(0xFF10B981), GoldBright))
                                        else
                                            Brush.horizontalGradient(listOf(GoldPrimary, GoldBright))
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(100, 300, 500, 1000, 2000).forEach { goalOption ->
                                val isSel = selectedGoal == goalOption
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSel) GoldBright else Color.White.copy(alpha = 0.10f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSel) Color.White else GlassBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedGoal = goalOption
                                            onUpdateDailyGoal(goalOption)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = goalOption.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) EmeraldDeepDark else IvoryText
                                    )
                                }
                            }
                        }
                    }
                }

                // 7-Day Activity Bar Chart
                StatsGlassCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "📈 Guzashta 7 Din Ka Record",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        val maxVal = (weeklyBreakdown.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            weeklyBreakdown.forEach { day ->
                                val ratio = (day.count.toFloat() / maxVal.toFloat()).coerceIn(0.06f, 1f)
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Text(
                                        text = if (day.count > 0) day.count.toString() else "",
                                        fontSize = 9.sp,
                                        color = GoldLight,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.65f)
                                            .weight(1f, fill = false)
                                            .height((70f * ratio).dp)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                            .background(
                                                if (day.label == "Today")
                                                    Brush.verticalGradient(listOf(GoldBright, GoldDeep))
                                                else
                                                    Brush.verticalGradient(listOf(Color(0xFF34D399), Color(0xFF065F46)))
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = day.label,
                                        fontSize = 10.sp,
                                        color = if (day.label == "Today") GoldBright else IvorySubtle,
                                        fontWeight = if (day.label == "Today") FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Per-Tasbih Lifetime Totals
                StatsGlassCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "📿 Har Tasbih Ka Lifetime Total",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        dhikrs.forEachIndexed { idx, dhikr ->
                            val lifeCount = dhikrLifetimes[dhikr.id] ?: 0L
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = dhikr.arabic,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldLight
                                    )
                                    Text(
                                        text = dhikr.transliteration,
                                        fontSize = 11.sp,
                                        color = IvorySubtle
                                    )
                                }
                                Text(
                                    text = "$lifeCount",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldBright
                                )
                            }
                            if (idx < dhikrs.lastIndex) {
                                HorizontalDivider(color = GlassBorder.copy(alpha = 0.4f), thickness = 0.6.dp)
                            }
                        }
                    }
                }

                // Export & Share (PDF + Image Card)
                StatsGlassCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "📤 Export & Share Progress",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )
                        Text(
                            text = "Share your Zikr report as a formatted PDF or Emerald & Gold image card.",
                            fontSize = 11.sp,
                            color = IvorySubtle,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Glass3DButton(
                                onClick = {
                                    try {
                                        TasbihExportHelper.shareStatsAsPdf(
                                            context = context,
                                            todayCount = todayCount,
                                            yesterdayCount = yesterdayCount,
                                            weekCount = weekCount,
                                            streakDays = streakDays,
                                            totalAllTime = totalAllTime,
                                            dailyGoal = selectedGoal,
                                            levelInfo = levelInfo,
                                            dhikrs = dhikrs,
                                            dhikrLifetimes = dhikrLifetimes,
                                            recentHistory = recentHistory,
                                            weeklyBreakdown = weeklyBreakdown
                                        )
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Could not export PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                isPrimaryGold = true,
                                height = 48.dp,
                                testTag = "export_pdf_button"
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "Export PDF",
                                    tint = EmeraldDeepDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PDF Report",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldDeepDark
                                )
                            }

                            Glass3DButton(
                                onClick = {
                                    try {
                                        TasbihExportHelper.shareStatsAsImage(
                                            context = context,
                                            todayCount = todayCount,
                                            yesterdayCount = yesterdayCount,
                                            weekCount = weekCount,
                                            streakDays = streakDays,
                                            totalAllTime = totalAllTime,
                                            dailyGoal = selectedGoal,
                                            levelInfo = levelInfo
                                        )
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Could not export image: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                isPrimaryGold = false,
                                height = 48.dp,
                                testTag = "export_image_button"
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Share Image",
                                    tint = GoldBright,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Share Image",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldBright
                                )
                            }
                        }
                    }
                }

                // Recent History section inside Stats screen as well
                HistorySection(
                    history = recentHistory,
                    onClearHistory = onClearHistory
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun MiniStatTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    StatsGlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GoldBright
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = IvorySubtle,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatsGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
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
    ) {
        content()
    }
}
