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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.model.Dhikr
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun DhikrFazilatScreen(
    wallpaperIndex: Int,
    dhikrs: List<Dhikr>,
    currentDhikr: Dhikr,
    onSelectAndCount: (Dhikr) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orderedDhikrs = listOf(currentDhikr) + dhikrs.filter { it.id != currentDhikr.id }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("dhikr_fazilat_screen")
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
                    testTag = "fazilat_back_button"
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "📖 ذکر کے معنیٰ اور فضیلت",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldBright
                    )
                    Text(
                        text = "Arabic Text, Tarjuma & Authentic Fazilat",
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orderedDhikrs, key = { it.id }) { dhikr ->
                    val isCurrent = dhikr.id == currentDhikr.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    if (isCurrent)
                                        listOf(Color(0x38D4AF37), Color(0x1E0D4A2E), Color(0x12000000))
                                    else
                                        listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                                )
                            )
                            .border(
                                if (isCurrent) 1.4.dp else 1.dp,
                                if (isCurrent) GoldBright else GlassBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            if (isCurrent) {
                                Text(
                                    text = "★ CURRENT ACTIVE DHIKR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldBright,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            Text(
                                text = dhikr.arabic,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldBright,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = dhikr.transliteration,
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic,
                                color = IvoryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = GlassBorder, thickness = 0.7.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Tarjuma / Meaning:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            Text(
                                text = dhikr.meaning,
                                fontSize = 13.sp,
                                color = IvoryText,
                                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                            )

                            if (dhikr.fazilat.isNotBlank()) {
                                Text(
                                    text = "Fazilat & Virtue:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Text(
                                    text = dhikr.fazilat,
                                    fontSize = 12.sp,
                                    color = IvorySubtle,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (dhikr.reference.isNotBlank()) "📚 ${dhikr.reference}" else "Target: ${dhikr.target}",
                                    fontSize = 11.sp,
                                    color = IvoryMuted
                                )

                                Glass3DPill(
                                    selected = true,
                                    onClick = { onSelectAndCount(dhikr) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Count this Dhikr",
                                        tint = EmeraldDeepDark,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Start (${dhikr.target})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = EmeraldDeepDark
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
