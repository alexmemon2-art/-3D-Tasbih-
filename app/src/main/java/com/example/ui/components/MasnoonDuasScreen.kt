package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DuaCategory
import com.example.model.MasnoonDua
import com.example.model.MasnoonDuasData
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun MasnoonDuasScreen(
    wallpaperIndex: Int,
    onReciteAsTasbih: (MasnoonDua) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(DuaCategory.ALL) }
    var query by remember { mutableStateOf("") }

    val filteredDuas = remember(selectedCategory, query) {
        MasnoonDuasData.allDuas.filter { dua ->
            val matchesCat = selectedCategory == DuaCategory.ALL || dua.category == selectedCategory
            val q = query.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                dua.title.lowercase().contains(q) ||
                dua.titleUrdu.contains(query.trim()) ||
                dua.romanUrdu.lowercase().contains(q) ||
                dua.englishMeaning.lowercase().contains(q) ||
                dua.transliteration.lowercase().contains(q)
            matchesCat && matchesQuery
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("masnoon_duas_screen")
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
            // Top Bar + Category Filters + Search
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
                        testTag = "duas_back_button"
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🤲 Masnoon Duas",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )
                        Text(
                            text = "روزمرہ مسنون دعائیں • Authentic Sunnah Duas",
                            fontSize = 11.sp,
                            color = IvorySubtle
                        )
                    }
                    Spacer(modifier = Modifier.size(42.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(DuaCategory.values()) { cat ->
                        val isSel = cat == selectedCategory
                        Glass3DPill(
                            selected = isSel,
                            onClick = { selectedCategory = cat },
                            testTag = "dua_cat_${cat.name}"
                        ) {
                            Text(
                                text = cat.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) EmeraldDeepDark else IvoryText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search Dua (e.g. Sleeping, Safar, Shifa)", color = IvoryMuted, fontSize = 13.sp) },
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
                        .testTag("duas_search_input"),
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDuas, key = { it.number }) { dua ->
                    MasnoonDuaCard(
                        dua = dua,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val textToCopy = "${dua.title}\n${dua.arabic}\n${dua.transliteration}\n${dua.romanUrdu}\n(${dua.reference})"
                            clipboard.setPrimaryClip(ClipData.newPlainText("Masnoon Dua", textToCopy))
                            Toast.makeText(context, "Dua copied to clipboard ✅", Toast.LENGTH_SHORT).show()
                        },
                        onRecite = { onReciteAsTasbih(dua) }
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
                    testTag = "duas_wapas_button"
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
private fun MasnoonDuaCard(
    dua: MasnoonDua,
    onCopy: () -> Unit,
    onRecite: () -> Unit
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
            .testTag("dua_card_${dua.number}")
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
                            text = dua.number.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = dua.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldBright
                        )
                        Text(
                            text = dua.titleUrdu,
                            fontSize = 12.sp,
                            color = GoldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = dua.arabic,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = IvoryText,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = dua.transliteration,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = GoldLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = GlassBorder, thickness = 0.6.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dua.romanUrdu,
                fontSize = 12.sp,
                color = IvoryText,
                lineHeight = 17.sp
            )
            Text(
                text = dua.englishMeaning,
                fontSize = 11.sp,
                color = IvorySubtle,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 3.dp, bottom = 6.dp)
            )

            Text(
                text = "📚 ${dua.reference}",
                fontSize = 10.sp,
                color = IvoryMuted,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Glass3DPill(
                    selected = false,
                    onClick = onCopy,
                    modifier = Modifier.weight(1f),
                    testTag = "dua_copy_${dua.number}"
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Dua",
                        tint = GoldBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Copy Dua",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IvoryText
                    )
                }

                Glass3DPill(
                    selected = true,
                    onClick = onRecite,
                    modifier = Modifier.weight(1f),
                    testTag = "dua_recite_${dua.number}"
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Recite in Tasbih",
                        tint = EmeraldDeepDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Tasbih (${dua.recommendedCount}x)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDeepDark
                    )
                }
            }
        }
    }
}
