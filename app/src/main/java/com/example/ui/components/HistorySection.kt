package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HistoryItem
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun HistorySection(
    history: List<HistoryItem>,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GlassSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
            .testTag("history_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright
                )

                if (history.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .clickable { onClearHistory() }
                            .padding(4.dp)
                            .testTag("clear_history_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear history",
                            tint = IvoryMuted,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Clear",
                            fontSize = 12.sp,
                            color = IvoryMuted
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                color = GoldPrimary.copy(alpha = 0.2f),
                thickness = 0.8.dp
            )

            if (history.isEmpty()) {
                Text(
                    text = "Complete your first Dhikr set to see it recorded here.",
                    fontSize = 13.sp,
                    color = IvoryMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    history.take(8).forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.dhikrArabic,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldPrimary
                                )
                                Text(
                                    text = "${item.dhikrTitle} • ${item.formattedTime}",
                                    fontSize = 11.sp,
                                    color = IvoryMuted
                                )
                            }
                            Text(
                                text = "+${item.count}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = IvoryText
                            )
                        }
                    }
                }
            }
        }
    }
}
