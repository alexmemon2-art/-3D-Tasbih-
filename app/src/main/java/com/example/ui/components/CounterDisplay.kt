package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryMuted
import com.example.ui.theme.IvorySubtle
import com.example.ui.theme.IvoryText

@Composable
fun CounterDisplay(
    count: Int,
    dhikr: Dhikr,
    countLocked: Boolean = false,
    volumeKeyEnabled: Boolean = true,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    onUndo: () -> Unit,
    onToggleLock: () -> Unit = {},
    onOpenFazilat: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val countScale = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            countScale.snapTo(1.14f)
            countScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f)
            )
        }
    }

    val orbInteractionSource = remember { MutableInteractionSource() }
    val orbPressed by orbInteractionSource.collectIsPressedAsState()
    val orbScale by animateFloatAsState(
        targetValue = if (orbPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "orb_press_scale"
    )

    val progress = if (dhikr.target > 0) (count.toFloat() / dhikr.target).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "progress_animation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0x30FFFFFF),
                        Color(0x18FFFFFF),
                        Color(0x10071E13)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(Color(0x70FFFFFF), Color(0x55D4AF37), GlassBorder)
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(20.dp)
            .testTag("counter_box")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Utility Row: Count Lock Pill & Meaning/Fazilat Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (countLocked) GoldPrimary.copy(alpha = 0.25f)
                            else Color.White.copy(alpha = 0.10f)
                        )
                        .border(
                            1.dp,
                            if (countLocked) GoldBright else GlassBorder,
                            CircleShape
                        )
                        .clickable { onToggleLock() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("counter_lock_toggle"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (countLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = if (countLocked) "Unlock Reset" else "Lock Reset",
                        tint = if (countLocked) GoldBright else IvorySubtle,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (countLocked) "Reset Locked" else "Lock Reset",
                        fontSize = 11.sp,
                        fontWeight = if (countLocked) FontWeight.Bold else FontWeight.Medium,
                        color = if (countLocked) GoldBright else IvorySubtle
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.10f))
                        .border(1.dp, GlassBorder, CircleShape)
                        .clickable { onOpenFazilat() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("counter_fazilat_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Meaning and Fazilat",
                        tint = GoldLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Meaning & Fazilat",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldLight
                    )
                }
            }

            // Arabic Text
            Text(
                text = dhikr.arabic,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold,
                color = GoldBright,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dhikr_arabic_text")
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Transliteration
            Text(
                text = dhikr.transliteration,
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                color = IvoryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("dhikr_transliteration_text")
            )

            // Meaning
            Text(
                text = dhikr.meaning,
                fontSize = 12.sp,
                color = IvorySubtle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 14.dp)
                    .testTag("dhikr_meaning_text")
            )

            // Interactive 3D Frosted Glass Circular Counter Orb
            Box(
                modifier = Modifier
                    .size(196.dp)
                    .scale(orbScale)
                    .shadow(
                        elevation = 14.dp,
                        shape = CircleShape,
                        ambientColor = GoldBright.copy(alpha = 0.45f),
                        spotColor = GoldPrimary.copy(alpha = 0.6f)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0x40FFFFFF),
                                Color(0x220D4A2E),
                                Color(0x6604140C)
                            )
                        )
                    )
                    .border(
                        width = 1.8.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xCCFFFFFF),
                                GoldBright.copy(alpha = 0.85f),
                                GoldDeep.copy(alpha = 0.6f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = orbInteractionSource,
                        indication = null,
                        onClick = onIncrement
                    )
                    .testTag("progress_ring_container"),
                contentAlignment = Alignment.Center
            ) {
                // Top glass reflection highlight
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.28f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.25f)
                                )
                            )
                        )
                )

                // Circular Sweep Progress Ring
                Canvas(modifier = Modifier.size(176.dp)) {
                    val strokeWidth = 9.dp.toPx()
                    drawCircle(
                        color = Color.White.copy(alpha = 0.14f),
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(GoldPrimary, GoldBright, GoldLight, GoldDeep, GoldPrimary)
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = count.toString(),
                        fontSize = 60.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier
                            .scale(countScale.value)
                            .testTag("counter_number_display")
                    )
                    Text(
                        text = "/ ${dhikr.target} • ${(progress * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldBright,
                        modifier = Modifier.testTag("target_count_display")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "✨ Tap the circle to count ✨" +
                    if (volumeKeyEnabled) "  •  🎚️ Vol +/−" else "",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = GoldLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // Big Primary 3D Glass Count Button
            Glass3DButton(
                onClick = onIncrement,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                isPrimaryGold = true,
                testTag = "main_count_button"
            ) {
                Text(
                    text = "COUNT +1",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EmeraldDeepDark,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: 3D Glass Reset & Undo (protected when Count Lock is ON)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Glass3DButton(
                    onClick = onReset,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp),
                    isPrimaryGold = false,
                    enabled = !countLocked,
                    height = 46.dp,
                    testTag = "reset_button"
                ) {
                    Icon(
                        imageVector = if (countLocked) Icons.Default.Lock else Icons.Default.Refresh,
                        contentDescription = if (countLocked) "Locked" else "Reset",
                        tint = if (countLocked) IvoryMuted.copy(alpha = 0.5f) else GoldBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = if (countLocked) "Locked" else "Reset",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (countLocked) IvoryMuted.copy(alpha = 0.5f) else GoldBright
                    )
                }

                Glass3DButton(
                    onClick = onUndo,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp),
                    isPrimaryGold = false,
                    enabled = count > 0 && !countLocked,
                    height = 46.dp,
                    testTag = "undo_button"
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (count > 0 && !countLocked) GoldBright else IvoryMuted.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "Undo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (count > 0 && !countLocked) GoldBright else IvoryMuted.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
