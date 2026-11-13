package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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
fun ExitConfirmScreen(
    sessionCount: Int,
    todayCount: Int,
    completedRounds: Int,
    onCancelExit: () -> Unit,
    onConfirmExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDeepDark)
            .testTag("exit_confirm_screen")
    ) {
        // Ultra Realistic Twilight Islamic Sanctuary Background
        Image(
            painter = painterResource(id = R.drawable.img_exit_art),
            contentDescription = "Twilight Islamic Sanctuary with illuminated lantern and tasbih",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark Vignette Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC071E13),
                            Color(0x990D4A2E),
                            Color(0xEE071E13),
                            Color(0xFA04140C)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(max = 500.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Illuminated Icon Header
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    GoldBright.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(1.5.dp, GoldBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤲",
                        fontSize = 38.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Leaving Tasbih?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GoldBright,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "کیا آپ ایپ بند کرنا چاہتے ہیں؟",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = IvoryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Today's Session Recap Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Today's Sacred Record",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ExitStatItem(value = sessionCount.toString(), label = "This Session")
                            ExitStatItem(value = todayCount.toString(), label = "Today's Total")
                            ExitStatItem(value = completedRounds.toString(), label = "Sets Done")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Kaffaratul Majlis Supplication Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "دُعَاءُ كَفَّارَةِ الْمَجْلِسِ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldBright
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ ، أَشْهَدُ أَنْ لا إِلَهَ إِلا أَنْتَ ، أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            textAlign = TextAlign.Center,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "\"Glory be to You, O Allah, and praise. I testify that there is no god but You. I seek Your forgiveness and repent to You.\"",
                            fontSize = 11.sp,
                            color = IvorySubtle,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "تَقَبَّلَ اللَّهُ مِنَّا وَمِنْكُمُ الصَّالِحَاتِ\nMay Allah accept your dhikr and prayers.",
                    fontSize = 13.sp,
                    color = IvoryMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.widthIn(max = 500.dp)
            ) {
                // 3D Glass Primary Continue Button
                Glass3DButton(
                    onClick = onCancelExit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    isPrimaryGold = true,
                    testTag = "continue_dhikr_button"
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Continue",
                        tint = EmeraldDeepDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "CONTINUE DHIKR (جاری رکھیں)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldDeepDark
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3D Glass Close App Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x35EF5350), Color(0x18C62828), Color(0x0C8E0000))
                            )
                        )
                        .border(
                            1.2.dp,
                            Brush.verticalGradient(
                                listOf(Color(0x80FFA4A2), Color(0x40EF5350), Color(0x15000000))
                            ),
                            CircleShape
                        )
                        .clickable { onConfirmExit() }
                        .testTag("close_app_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close App",
                            tint = Color(0xFFFF8A80),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "CLOSE APP (ایپ بند کریں)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8A80)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExitStatItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GoldBright
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = IvoryMuted,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
