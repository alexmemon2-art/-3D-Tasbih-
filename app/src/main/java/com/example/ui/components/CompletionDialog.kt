package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Dhikr
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun CompletionDialog(
    dhikr: Dhikr,
    targetCount: Int,
    onContinue: () -> Unit,
    onNextDhikr: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onContinue) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = EmeraldDeepDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldBright),
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("completion_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Crescent Emblem
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(GoldBright.copy(alpha = 0.35f), Color.Transparent)
                            )
                        )
                        .border(1.5.dp, GoldBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "☪",
                        fontSize = 34.sp,
                        color = GoldBright
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = dhikr.arabic,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Target Completed!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Completed $targetCount times\nMashaAllah! تَقَبَّلَ اللَّهُ",
                    fontSize = 15.sp,
                    color = IvorySubtle,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                )

                // 3D Glass Continue Button
                Glass3DButton(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = true,
                    shape = RoundedCornerShape(14.dp),
                    height = 50.dp,
                    testTag = "completion_continue_button"
                ) {
                    Text(
                        text = "Continue Counting",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeepDark
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3D Glass Next Dhikr Button
                Glass3DButton(
                    onClick = onNextDhikr,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimaryGold = false,
                    shape = RoundedCornerShape(14.dp),
                    height = 46.dp,
                    testTag = "completion_next_dhikr_button"
                ) {
                    Text(
                        text = "Next Dhikr (اگلا ذکر)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright
                    )
                }
            }
        }
    }
}
