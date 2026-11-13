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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AsmaName
import com.example.model.AsmaUlHusnaData
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun AsmaUlHusnaScreen(
    wallpaperIndex: Int,
    onSelectNameForTasbih: (AsmaName, Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var chosenTarget by remember { mutableIntStateOf(100) }

    val filteredNames = remember(query) {
        if (query.isBlank()) {
            AsmaUlHusnaData.all99Names
        } else {
            val q = query.trim().lowercase()
            AsmaUlHusnaData.all99Names.filter {
                it.transliteration.lowercase().contains(q) ||
                    it.englishMeaning.lowercase().contains(q) ||
                    it.romanUrdu.lowercase().contains(q) ||
                    it.urduMeaning.contains(query.trim()) ||
                    it.arabic.contains(query.trim()) ||
                    it.number.toString() == q
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("asma_ul_husna_screen")
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
            // Top Bar + Target Switcher + Search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Glass3DCircleIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        isActive = false,
                        onClick = onBack,
                        testTag = "asma_back_button"
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🕋 99 Names of Allah",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )
                        Text(
                            text = "أسماء الله الحسنىٰ • Tap any Name to recite",
                            fontSize = 11.sp,
                            color = IvorySubtle
                        )
                    }
                    Spacer(modifier = Modifier.size(42.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Target selector (33 vs 100 matching Tasbih target)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tasbih Target:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    listOf(33, 99, 100).forEach { targetVal ->
                        Glass3DPill(
                            selected = chosenTarget == targetVal,
                            onClick = { chosenTarget = targetVal },
                            testTag = "asma_target_$targetVal"
                        ) {
                            Text(
                                text = "${targetVal}x",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (chosenTarget == targetVal) EmeraldDeepDark else IvoryText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = {
                        Text(
                            "Search Name, Urdu/English Meaning or #",
                            color = IvoryMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GoldBright
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("asma_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldBright,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = IvoryText,
                        unfocusedTextColor = IvoryText,
                        cursorColor = GoldBright,
                        focusedContainerColor = Color(0x28000000),
                        unfocusedContainerColor = Color(0x20000000)
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNames, key = { it.number }) { item ->
                    AsmaNameCard(
                        item = item,
                        target = chosenTarget,
                        onStartTasbih = { onSelectNameForTasbih(item, chosenTarget) }
                    )
                }
            }

            // Bottom Wapas 3D Glass Button
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
                    testTag = "asma_wapas_button"
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
private fun AsmaNameCard(
    item: AsmaName,
    target: Int,
    onStartTasbih: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0x28FFFFFF), Color(0x14FFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0x55FFFFFF), GlassBorder, Color(0x18000000))),
                RoundedCornerShape(18.dp)
            )
            .clickable { onStartTasbih() }
            .padding(14.dp)
            .testTag("asma_card_${item.number}")
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.25f))
                            .border(1.dp, GoldBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.number.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "${item.transliteration} • ${item.romanUrdu}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = IvoryText
                        )
                        Text(
                            text = "${item.urduMeaning} (${item.englishMeaning})",
                            fontSize = 12.sp,
                            color = GoldLight
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = item.arabic,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.benefit,
                    fontSize = 11.sp,
                    color = IvorySubtle,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                )

                Glass3DPill(
                    selected = true,
                    onClick = onStartTasbih,
                    testTag = "asma_start_${item.number}"
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Zikr",
                        tint = EmeraldDeepDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Zikr ($target)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDeepDark
                    )
                }
            }
        }
    }
}
