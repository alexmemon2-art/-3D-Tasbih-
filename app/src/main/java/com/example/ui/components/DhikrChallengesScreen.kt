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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DhikrChallengesScreen(
    wallpaperIndex: Int,
    weekCount: Int,
    todayCount: Int,
    weeklyTarget: Int,
    todayHadeesDone: Boolean,
    onUpdateWeeklyTarget: (Int) -> Unit,
    onStartChallengeDhikr: (dhikrId: String, target: Int) -> Unit,
    onToggleHadeesChallengeDone: () -> Unit,
    onOpenDailyBaat: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = remember { NumberFormat.getIntegerInstance(Locale.US) }
    val clampedProgress = if (weeklyTarget > 0) (weekCount.toFloat() / weeklyTarget).coerceIn(0f, 1f) else 0f
    val remainingCount = (weeklyTarget - weekCount).coerceAtLeast(0)
    val daysRemainingInWeek = remember {
        val cal = Calendar.getInstance()
        val dow = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun .. 7=Sat
        val dayIndexMonToSun = if (dow == Calendar.SUNDAY) 7 else dow - 1
        (7 - dayIndexMonToSun).coerceAtLeast(1)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("dhikr_challenges_screen")
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
                    testTag = "challenges_back_arrow"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎯 Dhikr Challenges",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "ہفتہ وار اور روزانہ ذکر چیلنجز",
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
                // Weekly Main Challenge Card
                item(key = "weekly_challenge_card") {
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
                            .padding(18.dp)
                            .testTag("weekly_challenge_card")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "🔥 Is Haftay Ka Challenge",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldBright
                            )
                            Text(
                                text = "${formatter.format(weeklyTarget)} baar Allah Ka Zikr karein",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = IvoryText,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Target selector presets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Target:",
                                    fontSize = 11.sp,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                                listOf(3000, 5000, 10000).forEach { goalVal ->
                                    Glass3DPill(
                                        selected = weeklyTarget == goalVal,
                                        onClick = { onUpdateWeeklyTarget(goalVal) },
                                        testTag = "challenge_target_$goalVal"
                                    ) {
                                        Text(
                                            text = formatter.format(goalVal),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (weeklyTarget == goalVal) EmeraldDeepDark else IvoryText
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Meri Kamai: ${formatter.format(weekCount)} / ${formatter.format(weeklyTarget)} (${(clampedProgress * 100).toInt()}%)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldLight,
                                modifier = Modifier.testTag("weekly_challenge_progress_text")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { clampedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = GoldBright,
                                trackColor = Color(0x33FFFFFF)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (remainingCount == 0) {
                                    "MashaAllah! Is haftay ka challenge mukammal ho gaya ✅"
                                } else {
                                    "Baqi: ${formatter.format(remainingCount)} baar • Din baqi: $daysRemainingInWeek"
                                },
                                fontSize = 12.sp,
                                color = IvorySubtle
                            )
                        }
                    }
                }

                item(key = "more_challenges_header") {
                    Text(
                        text = "🏅 Aur Challenges (Aaj ka Total: ${formatter.format(todayCount)})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldLight,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                item(key = "challenge_morning") {
                    ChallengeActionCard(
                        title = "🌅 Subah ka Hadaf",
                        description = "Roz 500 baar SubhanAllah (سُبْحَانَ اللَّهِ)",
                        buttonText = "Shuru Karein",
                        isCompleted = false,
                        testTag = "challenge_subah_button",
                        onAction = { onStartChallengeDhikr("subhanallah", 500) }
                    )
                }

                item(key = "challenge_evening") {
                    ChallengeActionCard(
                        title = "🌙 Sham ka Hadaf",
                        description = "Roz 500 baar Alhamdulillah (الْحَمْدُ لِلَّهِ)",
                        buttonText = "Shuru Karein",
                        isCompleted = false,
                        testTag = "challenge_sham_button",
                        onAction = { onStartChallengeDhikr("alhamdulillah", 500) }
                    )
                }

                item(key = "challenge_istighfar") {
                    ChallengeActionCard(
                        title = "🕊️ Taubah & Barkat Challenge",
                        description = "Roz 100 baar Astaghfirullah (أَسْتَغْفِرُ اللَّهَ)",
                        buttonText = "Shuru Karein",
                        isCompleted = false,
                        testTag = "challenge_istighfar_button",
                        onAction = { onStartChallengeDhikr("astaghfirullah", 100) }
                    )
                }

                item(key = "challenge_hadees") {
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
                            .border(
                                1.dp,
                                if (todayHadeesDone) GoldBright else GlassBorder,
                                RoundedCornerShape(18.dp)
                            )
                            .padding(14.dp)
                            .testTag("challenge_hadees_card")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "📖 Hadees Parho",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldBright
                                    )
                                    Text(
                                        text = "Roz ek Hadees aur Ayat yaad karein",
                                        fontSize = 12.sp,
                                        color = IvorySubtle,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                if (todayHadeesDone) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Done",
                                            tint = GoldBright,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "✅ Done!",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = GoldBright
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Glass3DPill(
                                    selected = false,
                                    onClick = onOpenDailyBaat,
                                    modifier = Modifier.weight(1f),
                                    testTag = "challenge_open_baat_btn"
                                ) {
                                    Text(
                                        text = "☀️ Aaj ki Baat Parhein",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IvoryText
                                    )
                                }

                                Glass3DPill(
                                    selected = !todayHadeesDone,
                                    onClick = onToggleHadeesChallengeDone,
                                    modifier = Modifier.weight(1f),
                                    testTag = "challenge_hadees_done_btn"
                                ) {
                                    Text(
                                        text = if (todayHadeesDone) "Completed ✅" else "Mark Done",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (todayHadeesDone) GoldBright else EmeraldDeepDark
                                    )
                                }
                            }
                        }
                    }
                }

                item(key = "challenges_spacer") {
                    Spacer(modifier = Modifier.height(6.dp))
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
                    testTag = "challenges_wapas_button"
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
private fun ChallengeActionCard(
    title: String,
    description: String,
    buttonText: String,
    isCompleted: Boolean,
    testTag: String,
    onAction: () -> Unit
) {
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
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = IvorySubtle,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (isCompleted) {
                Text(
                    text = "✅ Done!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
            } else {
                Glass3DPill(
                    selected = true,
                    onClick = onAction,
                    testTag = testTag
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = buttonText,
                        tint = EmeraldDeepDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = buttonText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDeepDark
                    )
                }
            }
        }
    }
}
