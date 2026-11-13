package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DhikrLevelInfo
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvorySubtle

@Composable
fun StatsCards(
    todayCount: Int,
    yesterdayCount: Int,
    weekCount: Int,
    streakDays: Int,
    totalAllTime: Long,
    dailyGoal: Int,
    levelInfo: DhikrLevelInfo,
    onOpenFullStats: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Daily Goal Progress & Level Strip (tappable to open full Stats & Export screen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(GlassSurface)
                .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                .clickable { onOpenFullStats() }
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("home_goal_level_card")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${levelInfo.badge} ${levelInfo.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                    Text(
                        text = "🔥 $streakDays Day Streak",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldLight
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val safeGoal = dailyGoal.coerceAtLeast(1)
                val fraction = (todayCount.toFloat() / safeGoal).coerceIn(0f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily Goal: $todayCount / $safeGoal",
                        fontSize = 11.sp,
                        color = IvorySubtle
                    )
                    Text(
                        text = "${(fraction * 100).toInt()}% • Tap for Full Stats",
                        fontSize = 11.sp,
                        color = GoldLight
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction.coerceIn(0.02f, 1f))
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(Brush.horizontalGradient(listOf(GoldDeep, GoldBright)))
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                value = todayCount.toString(),
                label = "Aaj (Today)",
                modifier = Modifier.weight(1f),
                testTag = "stat_today_count"
            )
            StatCard(
                value = yesterdayCount.toString(),
                label = "Kal (Yesterday)",
                modifier = Modifier.weight(1f),
                testTag = "stat_yesterday_count"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                value = weekCount.toString(),
                label = "Is Haftay (Week)",
                modifier = Modifier.weight(1f),
                testTag = "stat_week_count"
            )
            StatCard(
                value = totalAllTime.toString(),
                label = "Kul Zikr (All-Time)",
                modifier = Modifier.weight(1f),
                testTag = "stat_total_count"
            )
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GlassSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 12.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GoldBright
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = IvorySubtle,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
