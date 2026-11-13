package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.IvoryText

/**
 * Premium 3D Glass Button with frosted glass gradient, bevel edge highlights,
 * tactile depth shadow, and press depression animation.
 */
@Composable
fun Glass3DButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    isPrimaryGold: Boolean = true,
    enabled: Boolean = true,
    height: Dp = 54.dp,
    testTag: String = "",
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "glass_press_scale"
    )

    val (glassGradient, borderBrush) = if (isPrimaryGold) {
        Pair(
            Brush.verticalGradient(
                colors = if (isPressed) listOf(
                    Color(0xDDCAA431),
                    Color(0xFF8A650C)
                ) else listOf(
                    Color(0xFFF7D95B), // High-intensity gold glass top
                    Color(0xFFD4AF37), // Pure royal gold mid
                    Color(0xFF9E7714)  // Deep gold glass bottom
                )
            ),
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),       // Ultra-sharp top specular rim
                    Color(0xAAFDE047),       // Upper golden bevel
                    Color(0x66B8860B),       // Lower bevel
                    Color(0x99382200)        // Deep bottom shadow bevel
                )
            )
        )
    } else {
        Pair(
            Brush.verticalGradient(
                colors = if (isPressed) listOf(
                    Color(0x28FFFFFF),
                    Color(0x0EFFFFFF)
                ) else listOf(
                    Color(0x35FFFFFF), // Frosted glass highlight top
                    Color(0x18FFFFFF), // Translucent glass body
                    Color(0x0CFFFFFF)  // Darker glass base
                )
            ),
            Brush.verticalGradient(
                colors = listOf(
                    Color(0x80FFFFFF),       // Bevel light reflection
                    Color(0x40D4AF37),       // Golden rim
                    Color(0x20FFFFFF),       // Glass side
                    Color(0x10000000)        // Subtle bottom bevel
                )
            )
        )
    }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isPressed) 2.dp else if (isPrimaryGold) 10.dp else 4.dp,
                shape = shape,
                ambientColor = if (isPrimaryGold) GoldBright.copy(alpha = 0.5f) else Color.Black,
                spotColor = if (isPrimaryGold) GoldPrimary.copy(alpha = 0.6f) else Color.Black
            )
            .clip(shape)
            .background(glassGradient)
            .border(
                width = if (isPrimaryGold) 1.5.dp else 1.dp,
                brush = borderBrush,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Inner Glass Sheen (diagonal light reflection across the top half of the 3D glass)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isPrimaryGold) 0.35f else 0.18f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 50f
                    )
                )
        )

        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * 3D Glass Pill Chip for selectors and secondary buttons.
 */
@Composable
fun Glass3DPill(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "",
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "pill_press_scale"
    )

    val backgroundBrush = if (selected) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFDE047),
                Color(0xFFD4AF37),
                Color(0xFF9E7714)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x2AFFFFFF),
                Color(0x10FFFFFF),
                Color(0x06FFFFFF)
            )
        )
    }

    val borderBrush = if (selected) {
        Brush.verticalGradient(
            colors = listOf(Color.White, Color(0xFFFDE047), Color(0x66B8860B))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color(0x66FFFFFF), Color(0x33D4AF37), Color(0x15000000))
        )
    }

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (selected) 6.dp else 2.dp,
                shape = CircleShape,
                ambientColor = if (selected) GoldBright.copy(alpha = 0.4f) else Color.Black,
                spotColor = if (selected) GoldPrimary.copy(alpha = 0.5f) else Color.Black
            )
            .clip(CircleShape)
            .background(backgroundBrush)
            .border(width = 1.dp, brush = borderBrush, shape = CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * 3D Glass Circular Icon Button with depth shadow, metallic border, and tactile feedback.
 */
@Composable
fun Glass3DCircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color? = null,
    borderColor: Color? = null,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgBrush = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFDE047),
                Color(0xFFD4AF37),
                Color(0xFF9E7714)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x35FFFFFF),
                Color(0x18FFFFFF),
                Color(0x0CFFFFFF)
            )
        )
    }

    val borderBrush = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(Color.White, Color(0xFFFDE047), Color(0x66B8860B))
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x80FFFFFF),
                borderColor ?: Color(0x33D4AF37),
                Color(0x15000000)
            )
        )
    }

    Box(
        modifier = modifier
            .size(42.dp)
            .scale(if (isPressed) 0.94f else 1f)
            .shadow(
                elevation = if (isActive) 5.dp else 2.dp,
                shape = CircleShape,
                ambientColor = if (isActive) GoldBright else Color.Black,
                spotColor = if (isActive) GoldPrimary else Color.Black
            )
            .clip(CircleShape)
            .background(bgBrush)
            .border(1.2.dp, borderBrush, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint ?: if (isActive) Color(0xFF071E13) else GoldBright,
            modifier = Modifier.size(20.dp)
        )
    }
}

