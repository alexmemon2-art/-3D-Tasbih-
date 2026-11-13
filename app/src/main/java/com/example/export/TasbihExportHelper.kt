package com.example.export

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.DailyDayStat
import com.example.model.Dhikr
import com.example.model.DhikrLevelInfo
import com.example.model.HistoryItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TasbihExportHelper {

    fun shareStatsAsPdf(
        context: Context,
        todayCount: Int,
        yesterdayCount: Int,
        weekCount: Int,
        streakDays: Int,
        totalAllTime: Long,
        dailyGoal: Int,
        levelInfo: DhikrLevelInfo,
        dhikrs: List<Dhikr>,
        dhikrLifetimes: Map<String, Long>,
        recentHistory: List<HistoryItem>,
        weeklyBreakdown: List<DailyDayStat>
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val bgPaint = Paint().apply { color = Color.parseColor("#09291A") }
        canvas.drawRect(0f, 0f, 595f, 842f, bgPaint)

        val borderPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(RectF(20f, 20f, 575f, 822f), 18f, 18f, borderPaint)

        val titlePaint = Paint().apply {
            color = Color.parseColor("#F4D03F")
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val subPaint = Paint().apply {
            color = Color.parseColor("#D9D3C2")
            textSize = 12f
            isAntiAlias = true
        }
        val headingPaint = Paint().apply {
            color = Color.parseColor("#FFE082")
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val bodyPaint = Paint().apply {
            color = Color.parseColor("#F5F0E1")
            textSize = 12.5f
            isAntiAlias = true
        }
        val cardPaint = Paint().apply {
            color = Color.parseColor("#144D31")
            isAntiAlias = true
        }

        var y = 64f
        canvas.drawText("Tasbih 3D — Zikr & Ibadat Report", 40f, y, titlePaint)
        y += 20f
        val dateText = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date())
        canvas.drawText("Generated: $dateText  •  By Usama Memon App", 40f, y, subPaint)

        y += 22f
        canvas.drawRoundRect(RectF(38f, y, 557f, y + 130f), 14f, 14f, cardPaint)
        val sy = y + 26f
        canvas.drawText("Summary Statistics", 52f, sy, headingPaint)
        canvas.drawText("Today (Aaj): $todayCount / $dailyGoal", 52f, sy + 24f, bodyPaint)
        canvas.drawText("Yesterday (Kal): $yesterdayCount", 310f, sy + 24f, bodyPaint)
        canvas.drawText("This Week (Is Haftay): $weekCount", 52f, sy + 46f, bodyPaint)
        canvas.drawText("Streak: $streakDays consecutive days", 310f, sy + 46f, bodyPaint)
        canvas.drawText("All-Time Total (Kul Zikr): $totalAllTime", 52f, sy + 68f, bodyPaint)
        canvas.drawText("Level: ${levelInfo.title}", 310f, sy + 68f, bodyPaint)
        val weekSummary = weeklyBreakdown.joinToString("  |  ") { "${it.label}: ${it.count}" }
        canvas.drawText("7-Day Log: $weekSummary", 52f, sy + 92f, subPaint)

        y += 150f
        canvas.drawText("Lifetime Totals by Dhikr", 40f, y, headingPaint)
        y += 18f
        dhikrs.take(10).forEach { dhikr ->
            val life = dhikrLifetimes[dhikr.id] ?: 0L
            canvas.drawText("• ${dhikr.transliteration}: $life", 48f, y, bodyPaint)
            y += 18f
        }

        y += 16f
        canvas.drawText("Recent Completed Sets", 40f, y, headingPaint)
        y += 18f
        if (recentHistory.isEmpty()) {
            canvas.drawText("No completed rounds logged yet.", 48f, y, subPaint)
        } else {
            recentHistory.take(12).forEach { item ->
                if (y < 785f) {
                    canvas.drawText("• ${item.dhikrTitle} (+${item.count}) at ${item.formattedTime}", 48f, y, bodyPaint)
                    y += 18f
                }
            }
        }

        pdfDocument.finishPage(page)

        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, "Tasbih_3D_Report.pdf")
        FileOutputStream(file).use { pdfDocument.writeTo(it) }
        pdfDocument.close()

        shareFile(context, file, "application/pdf", "Share Tasbih PDF Report")
    }

    fun shareStatsAsImage(
        context: Context,
        todayCount: Int,
        yesterdayCount: Int,
        weekCount: Int,
        streakDays: Int,
        totalAllTime: Long,
        dailyGoal: Int,
        levelInfo: DhikrLevelInfo
    ) {
        val width = 1080
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(
                    Color.parseColor("#071E13"),
                    Color.parseColor("#0D4A2E"),
                    Color.parseColor("#09291A")
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val borderPaint = Paint().apply {
            color = Color.parseColor("#D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(40f, 40f, width - 40f, height - 40f), 40f, 40f, borderPaint)

        val titlePaint = Paint().apply {
            color = Color.parseColor("#F4D03F")
            textSize = 54f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#D9D3C2")
            textSize = 28f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val labelPaint = Paint().apply {
            color = Color.parseColor("#FFE082")
            textSize = 30f
            isAntiAlias = true
        }
        val valuePaint = Paint().apply {
            color = Color.WHITE
            textSize = 58f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val cardPaint = Paint().apply {
            color = Color.parseColor("#2AFFFFFF")
            isAntiAlias = true
        }

        canvas.drawText("3D تسبیح کاؤنٹر", width / 2f, 150f, titlePaint)
        canvas.drawText("My Daily Zikr & Tasbih Progress", width / 2f, 205f, subtitlePaint)

        val cards = listOf(
            "Aaj (Today)" to "$todayCount / $dailyGoal",
            "Kal (Yesterday)" to yesterdayCount.toString(),
            "Is Haftay (This Week)" to weekCount.toString(),
            "Lagatar Din (Streak)" to "🔥 $streakDays Days",
            "Kul Zikr (All-Time)" to totalAllTime.toString(),
            "Spiritual Level" to "${levelInfo.badge} ${levelInfo.title}"
        )

        var y = 270f
        cards.forEach { (label, value) ->
            canvas.drawRoundRect(RectF(84f, y, width - 84f, y + 135f), 28f, 28f, cardPaint)
            canvas.drawRoundRect(RectF(84f, y, width - 84f, y + 135f), 28f, 28f, Paint().apply {
                color = Color.parseColor("#66D4AF37")
                style = Paint.Style.STROKE
                strokeWidth = 2.5f
                isAntiAlias = true
            })
            canvas.drawText(label, 124f, y + 48f, labelPaint)
            canvas.drawText(value, 124f, y + 108f, if (value.length > 18) labelPaint.apply { textSize = 36f } else valuePaint)
            y += 160f
        }

        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.US).format(Date())
        canvas.drawText("Tasbih 3D App • $dateStr", width / 2f, height - 80f, subtitlePaint)

        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, "Tasbih_3D_Stats.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        bitmap.recycle()

        shareFile(context, file, "image/png", "Share Tasbih Progress Card")
    }

    private fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}
