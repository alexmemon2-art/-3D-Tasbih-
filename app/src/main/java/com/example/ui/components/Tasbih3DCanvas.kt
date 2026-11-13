package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BeadStyle
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private data class ProjectedBead(
    val index: Int,
    val x: Float,
    val y: Float,
    val z: Float,
    val radius: Float,
    val isGoldSeparator: Boolean,
    val isActive: Boolean
)

@Composable
fun Tasbih3DCanvas(
    count: Int,
    target: Int,
    beadStyle: BeadStyle,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalBeads = 33
    val activeBeadIndex = if (count == 0) 0 else ((count - 1) % totalBeads)

    // Pulse animation for active bead when count changes
    val activeScale = remember { Animatable(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            activeScale.snapTo(1.5f)
            activeScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.45f, stiffness = 450f)
            )
        }
    }

    // Continuous subtle breathing glow and specular shimmer for the tasbih
    val infiniteTransition = rememberInfiniteTransition(label = "tasbih_infinite")
    val idleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(38000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "idle_rotation"
    )

    val shimmerGlint by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_glint"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    var manualRotation by remember { mutableFloatStateOf(0f) }
    var swayOffset by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(265.dp)
            .testTag("tasbih_3d_canvas_container"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .testTag("tasbih_3d_canvas")
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onIncrement()
                        }
                    )
                }
                .pointerInput(Unit) {
                    var accumulatedDrag = 0f
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val delta = dragAmount.x + dragAmount.y * 0.5f
                            manualRotation += delta * 0.008f
                            swayOffset = (swayOffset + delta * 0.15f).coerceIn(-18f, 18f)
                            accumulatedDrag += delta
                            if (abs(accumulatedDrag) > 32f) {
                                onIncrement()
                                accumulatedDrag = 0f
                            }
                        },
                        onDragEnd = {
                            swayOffset = 0f
                        }
                    )
                }
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f - 6.dp.toPx()
            val ringRadius = size.minDimension * 0.365f
            val baseBeadRadius = size.minDimension * 0.0385f
            val cameraDistance = ringRadius * 4.2f
            val tiltAngle = 0.40f // ~23 degrees tilt for realistic 3D depth

            val effectiveRotation = idleRotation + manualRotation

            // Pre-calculate 3D beads
            val beads = ArrayList<ProjectedBead>(totalBeads)
            var minZ = Float.MAX_VALUE
            var maxZ = Float.MIN_VALUE

            for (i in 0 until totalBeads) {
                val beadAngle = effectiveRotation + (i.toFloat() / totalBeads) * (2 * PI.toFloat())
                val x = cos(beadAngle) * ringRadius
                val y = sin(beadAngle) * ringRadius * cos(tiltAngle)
                val z = sin(beadAngle) * ringRadius * sin(tiltAngle)

                if (z < minZ) minZ = z
                if (z > maxZ) maxZ = z

                val perspective = cameraDistance / (cameraDistance - z)
                val projX = centerX + x * perspective
                val projY = centerY + y * perspective
                val isSeparator = (i % 11 == 0)
                val isActive = (i == activeBeadIndex && count > 0)

                val radius = baseBeadRadius * perspective * (if (isActive) activeScale.value else 1f)

                beads.add(
                    ProjectedBead(
                        index = i,
                        x = projX,
                        y = projY,
                        z = z,
                        radius = radius,
                        isGoldSeparator = isSeparator,
                        isActive = isActive
                    )
                )
            }

            val zRange = (maxZ - minZ).coerceAtLeast(1f)

            // Draw Braided Golden Silk Thread connecting the beads
            val cordPath = Path()
            for (i in 0..totalBeads) {
                val bead = beads[i % totalBeads]
                if (i == 0) cordPath.moveTo(bead.x, bead.y)
                else cordPath.lineTo(bead.x, bead.y)
            }
            // Base shadow of thread
            drawPath(
                path = cordPath,
                color = Color(0x3304140C),
                style = Stroke(width = 3.2.dp.toPx(), cap = StrokeCap.Round)
            )
            // Braided golden thread with metallic sheen
            drawPath(
                path = cordPath,
                brush = Brush.radialGradient(
                    colors = listOf(GoldBright, GoldPrimary, GoldDeep.copy(alpha = 0.6f)),
                    center = Offset(centerX, centerY),
                    radius = ringRadius * 1.2f
                ),
                style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw beads sorted by Z ascending (Back-to-Front painter's algorithm)
            val sortedBeads = beads.sortedBy { it.z }

            // Material colors by selected style
            val stylePalettes = getBeadPalette(beadStyle)

            for (bead in sortedBeads) {
                val center = Offset(bead.x, bead.y)
                // Depth-of-Field factor: 0.0 = deep background, 1.0 = sharp foreground
                val dofFactor = ((bead.z - minZ) / zRange).coerceIn(0f, 1f)
                val isBackgroundDoF = dofFactor < 0.45f
                val dofAlpha = 0.55f + dofFactor * 0.45f

                // 1. Subtle Depth-of-Field Blur for far background beads
                if (isBackgroundDoF) {
                    val blurSpread = (1f - dofFactor) * 4.5f
                    drawCircle(
                        color = Color(0x220D4A2E),
                        radius = bead.radius + blurSpread * 1.5f,
                        center = center
                    )
                    drawCircle(
                        color = Color(0x33000000),
                        radius = bead.radius + blurSpread,
                        center = Offset(center.x + 1f, center.y + 2f)
                    )
                } else {
                    // Crisp realistic drop shadow for foreground beads
                    val shadowOffset = Offset(
                        (1f - dofFactor) * 1f + 2.5f,
                        dofFactor * 3.5f + 1.5f
                    )
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.45f * dofAlpha),
                        radius = bead.radius * 0.98f,
                        center = center + shadowOffset
                    )
                }

                // 2. Active Bead Radiant Golden Aura & Halo
                if (bead.isActive) {
                    // Wide ethereal halo
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GoldBright.copy(alpha = 0.95f * glowAlpha),
                                GoldPrimary.copy(alpha = 0.45f * glowAlpha),
                                Color(0x22B8860B),
                                Color.Transparent
                            ),
                            center = center,
                            radius = bead.radius * 3.2f
                        ),
                        radius = bead.radius * 3.2f,
                        center = center
                    )

                    // Secondary sharp pulsating inner halo
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.8f * shimmerGlint),
                                GoldLight.copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = bead.radius * 1.6f
                        ),
                        radius = bead.radius * 1.6f,
                        center = center
                    )
                }

                // 3. Shaded Spherical Body (Polished Gold Metallic or Jade/Gemstone)
                if (bead.isGoldSeparator || bead.isActive) {
                    // Polished Multi-Stop Gold Metallic Shader
                    drawPolishedGoldBead(
                        center = center,
                        radius = bead.radius,
                        isActive = bead.isActive,
                        dofFactor = dofFactor,
                        dofAlpha = dofAlpha,
                        shimmer = shimmerGlint
                    )
                } else {
                    // Gemstone / Material Bead with Subsurface Scattering & Bounce Light
                    drawGemstoneBead(
                        center = center,
                        radius = bead.radius,
                        palette = stylePalettes,
                        dofFactor = dofFactor,
                        dofAlpha = dofAlpha
                    )
                }

                // 4. Realistic Primary Light Reflection (Key Light from Top-Left ~-45 deg)
                val lightOffsetX = -bead.radius * 0.34f
                val lightOffsetY = -bead.radius * 0.34f
                val specularCenter = Offset(center.x + lightOffsetX, center.y + lightOffsetY)

                // Soft specular bloom halo
                val bloomRadius = bead.radius * (if (bead.isActive) 0.55f else 0.42f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isBackgroundDoF) 0.35f else 0.75f * dofAlpha),
                            Color(0x88FFFFFF).copy(alpha = if (isBackgroundDoF) 0.15f else 0.4f),
                            Color.Transparent
                        ),
                        center = specularCenter,
                        radius = bloomRadius
                    ),
                    radius = bloomRadius,
                    center = specularCenter
                )

                // Pinpoint razor-sharp specular hotspot (only on focused foreground beads)
                if (!isBackgroundDoF) {
                    drawCircle(
                        color = Color.White.copy(alpha = if (bead.isActive) 0.98f else 0.92f),
                        radius = bead.radius * 0.18f,
                        center = specularCenter
                    )
                    // Secondary micro glint
                    drawCircle(
                        color = Color.White.copy(alpha = 0.7f),
                        radius = bead.radius * 0.08f,
                        center = Offset(specularCenter.x - bead.radius * 0.12f, specularCenter.y - bead.radius * 0.08f)
                    )
                }

                // 5. Starburst Sparkle Glint on Active Bead & Polished Separators
                if (bead.isActive && !isBackgroundDoF) {
                    drawStarburstGlint(
                        center = specularCenter,
                        size = bead.radius * 1.4f * shimmerGlint,
                        tint = Color(0xFFFFFDE7)
                    )
                } else if (bead.isGoldSeparator && dofFactor > 0.6f && shimmerGlint > 0.7f) {
                    drawStarburstGlint(
                        center = specularCenter,
                        size = bead.radius * 0.85f * ((shimmerGlint - 0.7f) / 0.3f),
                        tint = GoldLight
                    )
                }
            }

            // 6. Polished Gold Imam (Minaret) Bead & Ornate Silky Tassel
            val imamY = centerY - ringRadius * 0.98f
            val imamCenter = Offset(centerX, imamY)
            drawOrnateImamAndTassel(
                imamCenter = imamCenter,
                baseRadius = baseBeadRadius,
                swayOffset = swayOffset,
                shimmer = shimmerGlint
            )
        }
    }
}

// -------------------------------------------------------------------------
// Shading & Rendering Helpers
// -------------------------------------------------------------------------

/**
 * Draws a photorealistic polished gold metallic bead with multi-tone luster,
 * anisotropic curvature sheen, and metallic filigree rim.
 */
private fun DrawScope.drawPolishedGoldBead(
    center: Offset,
    radius: Float,
    isActive: Boolean,
    dofFactor: Float,
    dofAlpha: Float,
    shimmer: Float
) {
    // Multi-stop metallic gold spherical shader
    val goldBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFFFFF),       // Specular apex
            Color(0xFFFFF8DC),       // Champagne light
            Color(0xFFFDE047),       // Bright yellow gold
            Color(0xFFD4AF37),       // Royal gold body
            Color(0xFFB8860B),       // Deep golden core
            Color(0xFF785306),       // Shadow bronze
            Color(0xFF382302)        // Deep occlusion rim
        ),
        center = Offset(center.x - radius * 0.32f, center.y - radius * 0.32f),
        radius = radius * 1.35f
    )

    // Main spherical body
    drawCircle(
        brush = goldBrush,
        radius = radius,
        center = center,
        alpha = dofAlpha
    )

    // Polished anisotropic metallic rim reflection (bounce light from emerald environment)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x3310B981),   // Subtle emerald environment reflection on metallic gold
                Color(0x66FDE047),   // Bright golden rim edge
                Color(0xAAFFFFFF)    // Sharp metallic edge
            ),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center,
        alpha = dofAlpha
    )

    // Subtle engraved filigree metallic band around gold separators
    if (!isActive && dofFactor > 0.45f) {
        drawCircle(
            color = Color(0x66785306),
            radius = radius * 0.88f,
            center = center,
            style = Stroke(width = 1.2.dp.toPx())
        )
        drawCircle(
            color = Color(0x88FFF9C4),
            radius = radius * 0.84f,
            center = center,
            style = Stroke(width = 0.8.dp.toPx())
        )
    }

    // Secondary warm bounce reflection on bottom-right (subsurface illusion)
    val bounceCenter = Offset(center.x + radius * 0.45f, center.y + radius * 0.45f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                GoldLight.copy(alpha = 0.5f * dofAlpha),
                Color.Transparent
            ),
            center = bounceCenter,
            radius = radius * 0.65f
        ),
        radius = radius * 0.65f,
        center = bounceCenter
    )
}

/**
 * Draws an authentic translucent gemstone bead (Jade, Pearl, Amber, Lapis)
 * featuring subsurface light scattering, depth color absorption, and ambient bounce reflection.
 */
private fun DrawScope.drawGemstoneBead(
    center: Offset,
    radius: Float,
    palette: BeadPalette,
    dofFactor: Float,
    dofAlpha: Float
) {
    // Spherical volume gradient
    val bodyBrush = Brush.radialGradient(
        colors = listOf(
            palette.highlight,
            palette.midtone,
            palette.core,
            palette.deepShadow
        ),
        center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
        radius = radius * 1.32f
    )

    drawCircle(
        brush = bodyBrush,
        radius = radius,
        center = center,
        alpha = dofAlpha
    )

    // Inner translucent subsurface glow (light penetrating the translucent stone)
    val innerGlowCenter = Offset(center.x + radius * 0.15f, center.y + radius * 0.15f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                palette.subsurfaceGlow.copy(alpha = 0.45f * dofAlpha),
                Color.Transparent
            ),
            center = innerGlowCenter,
            radius = radius * 0.78f
        ),
        radius = radius * 0.78f,
        center = innerGlowCenter
    )

    // Realistic bottom-right ambient bounce light (ambient reflection from surroundings)
    val rimCenter = Offset(center.x + radius * 0.42f, center.y + radius * 0.42f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                palette.ambientBounce.copy(alpha = 0.55f * dofAlpha),
                Color.Transparent
            ),
            center = rimCenter,
            radius = radius * 0.6f
        ),
        radius = radius * 0.6f,
        center = rimCenter
    )

    // Polished vitrified glass/jade gloss rim
    if (dofFactor > 0.45f) {
        drawCircle(
            color = Color.White.copy(alpha = 0.18f * dofAlpha),
            radius = radius,
            center = center,
            style = Stroke(width = 0.8.dp.toPx())
        )
    }
}

/**
 * Draws a 4-point golden starburst sparkle glint for realistic jewel specular reflection.
 */
private fun DrawScope.drawStarburstGlint(
    center: Offset,
    size: Float,
    tint: Color
) {
    if (size <= 1f) return
    val halfSize = size / 2f
    val glintPath = Path().apply {
        // Horizontal diamond ray
        moveTo(center.x - size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - halfSize * 0.3f)
        quadraticTo(center.x, center.y, center.x + size, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + halfSize * 0.3f)
        close()

        // Vertical diamond ray
        moveTo(center.x, center.y - size)
        quadraticTo(center.x, center.y, center.x + halfSize * 0.3f, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + size)
        quadraticTo(center.x, center.y, center.x - halfSize * 0.3f, center.y)
        close()
    }

    drawPath(
        path = glintPath,
        color = tint.copy(alpha = 0.85f)
    )

    // Center brilliant core
    drawCircle(
        color = Color.White,
        radius = size * 0.16f,
        center = center
    )
}

/**
 * Draws an intricately faceted and polished golden Imam (Minaret) bead
 * with multi-layered filigree caps and swinging golden tassel threads.
 */
private fun DrawScope.drawOrnateImamAndTassel(
    imamCenter: Offset,
    baseRadius: Float,
    swayOffset: Float,
    shimmer: Float
) {
    val imamRadius = baseRadius * 1.25f

    // 1. Lower Imam spherical base bead
    drawPolishedGoldBead(
        center = imamCenter,
        radius = imamRadius,
        isActive = false,
        dofFactor = 0.9f,
        dofAlpha = 1f,
        shimmer = shimmer
    )

    // 2. Ornate faceted golden spacer ring
    val ringCenter = Offset(imamCenter.x, imamCenter.y - imamRadius * 1.05f)
    drawOval(
        brush = Brush.horizontalGradient(
            colors = listOf(GoldDeep, GoldBright, Color.White, GoldPrimary, GoldDeep)
        ),
        topLeft = Offset(ringCenter.x - imamRadius * 0.75f, ringCenter.y - 2.5.dp.toPx()),
        size = androidx.compose.ui.geometry.Size(imamRadius * 1.5f, 5.dp.toPx())
    )

    // 3. Golden Minaret cone/finial
    val coneTop = Offset(imamCenter.x, ringCenter.y - imamRadius * 1.4f)
    val conePath = Path().apply {
        moveTo(ringCenter.x - imamRadius * 0.65f, ringCenter.y)
        lineTo(coneTop.x, coneTop.y)
        lineTo(ringCenter.x + imamRadius * 0.65f, ringCenter.y)
        close()
    }
    drawPath(
        path = conePath,
        brush = Brush.linearGradient(
            colors = listOf(GoldDeep, GoldBright, Color.White, GoldPrimary),
            start = Offset(ringCenter.x - imamRadius, 0f),
            end = Offset(ringCenter.x + imamRadius, 0f)
        )
    )

    // Top crescent finial pin
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, GoldBright, GoldDeep),
            center = coneTop,
            radius = 3.dp.toPx()
        ),
        radius = 2.5.dp.toPx(),
        center = coneTop
    )

    // 4. Silky golden tassel cords with dynamic sway
    val tasselStart = Offset(coneTop.x, coneTop.y - 2.dp.toPx())
    val tasselKnot = Offset(tasselStart.x + swayOffset * 0.4f, tasselStart.y - 12.dp.toPx())

    // Hanging braided loop
    drawLine(
        brush = Brush.verticalGradient(listOf(GoldBright, GoldDeep)),
        start = tasselStart,
        end = tasselKnot,
        strokeWidth = 2.4.dp.toPx(),
        cap = StrokeCap.Round
    )

    // Golden Bell Cap for tassel skirt
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, GoldBright, GoldDeep),
            center = Offset(tasselKnot.x - 1f, tasselKnot.y - 1f),
            radius = baseRadius * 0.6f
        ),
        radius = baseRadius * 0.55f,
        center = tasselKnot
    )

    // Flowing golden silk tassel strands with physics curve
    val tasselLength = 26.dp.toPx()
    val strandOffsets = listOf(-8f, -4f, -1f, 2f, 5f, 9f)
    for ((index, xOff) in strandOffsets.withIndex()) {
        val strandEnd = Offset(
            tasselKnot.x + xOff * 1.4f + swayOffset * 0.8f,
            tasselKnot.y - tasselLength + (abs(xOff) * 0.5f)
        )
        val strandCtrl = Offset(
            tasselKnot.x + xOff * 0.8f + swayOffset * 0.4f,
            tasselKnot.y - tasselLength * 0.5f
        )
        val strandPath = Path().apply {
            moveTo(tasselKnot.x, tasselKnot.y)
            quadraticTo(strandCtrl.x, strandCtrl.y, strandEnd.x, strandEnd.y)
        }
        val strandAlpha = 0.7f + (index % 2) * 0.25f
        drawLine(
            color = if (index % 2 == 0) GoldBright.copy(alpha = strandAlpha) else GoldLight.copy(alpha = strandAlpha),
            start = tasselKnot,
            end = strandEnd,
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Tiny golden bead at each strand terminus
        drawCircle(
            color = GoldBright,
            radius = 1.2.dp.toPx(),
            center = strandEnd
        )
    }
}

// -------------------------------------------------------------------------
// Color Palette Data Structures
// -------------------------------------------------------------------------

private data class BeadPalette(
    val highlight: Color,
    val midtone: Color,
    val core: Color,
    val deepShadow: Color,
    val subsurfaceGlow: Color,
    val ambientBounce: Color
)

private fun getBeadPalette(style: BeadStyle): BeadPalette {
    return when (style) {
        BeadStyle.EMERALD_GOLD -> BeadPalette(
            highlight = Color(0xFF6EE7B7),       // Bright mint emerald highlight
            midtone = Color(0xFF10B981),         // Radiant jade green
            core = Color(0xFF065F46),            // Deep forest emerald
            deepShadow = Color(0xFF022C22),      // Shadow core
            subsurfaceGlow = Color(0xFF34D399),  // Translucent jade glow
            ambientBounce = Color(0xFF059669)    // Environment bounce
        )
        BeadStyle.ROYAL_GOLD -> BeadPalette(
            highlight = Color(0xFFFFFBEB),
            midtone = Color(0xFFFDE047),
            core = Color(0xFFB8860B),
            deepShadow = Color(0xFF452B04),
            subsurfaceGlow = Color(0xFFFACC15),
            ambientBounce = Color(0xFFD4AF37)
        )
        BeadStyle.WHITE_PEARL -> BeadPalette(
            highlight = Color(0xFFFFFFFF),       // Iridescent pearl apex
            midtone = Color(0xFFF1F5F9),         // Soft pearlescent silver
            core = Color(0xFFCBD5E1),            // Deep pearl body
            deepShadow = Color(0xFF64748B),      // Shaded luster
            subsurfaceGlow = Color(0xFFF8FAFC),  // Nacreous translucency
            ambientBounce = Color(0xFFE2E8F0)    // Environment sheen
        )
        BeadStyle.AGATE_AMBER -> BeadPalette(
            highlight = Color(0xFFFED7AA),       // Warm amber honey highlight
            midtone = Color(0xFFF97316),         // Vibrant carnelian orange
            core = Color(0xFF9A3412),            // Deep resinous amber
            deepShadow = Color(0xFF431407),      // Dark amber occlusion
            subsurfaceGlow = Color(0xFFFB923C),  // Warm inner candle glow
            ambientBounce = Color(0xFFEA580C)    // Honey bounce reflection
        )
        BeadStyle.LAPIS_BLUE -> BeadPalette(
            highlight = Color(0xFF93C5FD),       // Sapphire glint
            midtone = Color(0xFF2563EB),         // Royal lapis lazuli
            core = Color(0xFF1E3A8A),            // Deep celestial cobalt
            deepShadow = Color(0xFF0F172A),      // Midnight blue shadow
            subsurfaceGlow = Color(0xFF3B82F6),  // Inner ultramarine refraction
            ambientBounce = Color(0xFF1D4ED8)    // Cyan bounce light
        )
    }
}
