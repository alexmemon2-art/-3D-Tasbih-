package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyBaatProvider
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun DailyReminderScreen(
    wallpaperIndex: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val todayIndex = remember { DailyBaatProvider.getTodayIndex() }
    var currentIndex by remember { mutableIntStateOf(todayIndex) }
    val totalCount = DailyBaatProvider.allEntries.size
    val dailyContent = remember(currentIndex) { DailyBaatProvider.getDailyEntry(currentIndex) }
    val isToday = ((currentIndex % totalCount) + totalCount) % totalCount == todayIndex

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_reminder_screen")
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
            // Header
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
                    testTag = "daily_back_arrow"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "☀️ Aaj ki Baat",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "آج کی آیت اور حدیث • Daily Inspiration",
                        fontSize = 11.sp,
                        color = IvorySubtle
                    )
                }
                Spacer(modifier = Modifier.size(42.dp))
            }

            // Day Switcher Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Glass3DCircleIconButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Day",
                    isActive = false,
                    onClick = { currentIndex = (currentIndex - 1 + totalCount) % totalCount },
                    testTag = "daily_prev_button"
                )

                Glass3DPill(
                    selected = isToday,
                    onClick = { currentIndex = todayIndex },
                    testTag = "daily_today_pill"
                ) {
                    Text(
                        text = if (isToday) "✨ Today (${dailyContent.dayNumber}/$totalCount)" else "Day ${dailyContent.dayNumber}/$totalCount • Tap for Today",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) EmeraldDeepDark else GoldBright
                    )
                }

                Glass3DCircleIconButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Day",
                    isActive = false,
                    onClick = { currentIndex = (currentIndex + 1) % totalCount },
                    testTag = "daily_next_button"
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Ayat Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x2EFFFFFF), Color(0x16FFFFFF), Color(0x0BFFFFFF))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(listOf(Color(0x66FFFFFF), GlassBorder, Color(0x18000000))),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(18.dp)
                        .testTag("daily_ayat_card")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📖 Aaj ki Ayat",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldBright
                            )
                            Text(
                                text = dailyContent.ayatReference,
                                fontSize = 11.sp,
                                color = GoldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = dailyContent.ayatArabic,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = IvoryText,
                            textAlign = TextAlign.Center,
                            lineHeight = 38.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = GlassBorder, thickness = 0.6.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = dailyContent.ayatUrdu,
                            fontSize = 14.sp,
                            color = IvorySubtle,
                            lineHeight = 21.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Hadees Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(listOf(Color(0x55FFFFFF), GlassBorder, Color(0x18000000))),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(18.dp)
                        .testTag("daily_hadees_card")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "📜 Aaj ki Hadees",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = dailyContent.hadees,
                            fontSize = 15.sp,
                            color = IvoryText,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "📖 ${dailyContent.reference}",
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = GoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Share + Copy Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Glass3DButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipText = """
                                ☀️ Aaj ki Ayat (${dailyContent.ayatReference}):
                                ${dailyContent.ayatArabic}
                                ${dailyContent.ayatUrdu}

                                📜 Aaj ki Hadees:
                                ${dailyContent.hadees}
                                📖 ${dailyContent.reference}
                            """.trimIndent()
                            clipboard.setPrimaryClip(ClipData.newPlainText("Aaj ki Baat", clipText))
                            Toast.makeText(context, "Ayat & Hadees copied ✅", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        isPrimaryGold = false,
                        height = 50.dp,
                        testTag = "daily_copy_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Daily Reminder",
                            tint = GoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Copy",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = IvoryText
                        )
                    }

                    Glass3DButton(
                        onClick = {
                            val shareText = """
                                ☀️ Aaj ki Ayat:
                                ${dailyContent.ayatArabic}
                                ${dailyContent.ayatUrdu}

                                📜 Aaj ki Hadees:
                                ${dailyContent.hadees}
                                📖 ${dailyContent.reference}

                                ✨ 3D Tasbih Counter App se liya gaya
                            """.trimIndent()
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Karein"))
                        },
                        modifier = Modifier.weight(1.4f),
                        isPrimaryGold = true,
                        height = 50.dp,
                        testTag = "daily_share_button"
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Karein",
                            tint = EmeraldDeepDark,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "📤 Share Karein",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldDeepDark
                        )
                    }
                }

                Text(
                    text = "Har din nayi Quranic Ayat aur Hadees-e-Nabawi ﷺ khud-ba-khud update hoti hai.",
                    fontSize = 11.sp,
                    color = IvoryMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
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
                    testTag = "daily_wapas_button"
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
