package com.cocobiz.app.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.domain.model.SaleStatus
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object PdfExporter {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 32f
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    fun exportReportToPdf(
        context: Context,
        sales: List<SalesEntry>,
        totalRevenue: Double,
        reportTitle: String
    ): Boolean {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            drawPage(page.canvas, sales, totalRevenue, reportTitle)
            pdfDocument.finishPage(page)

            val timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val fileName = "CocoBiz_Report_$timestamp.pdf"
            savePdf(context, pdfDocument, fileName)
            pdfDocument.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun drawPage(
        canvas: Canvas,
        sales: List<SalesEntry>,
        totalRevenue: Double,
        reportTitle: String
    ) {
        // Header background
        val headerBgPaint = Paint().apply {
            color = Color.parseColor("#1565C0")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 72f, headerBgPaint)

        // App title
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("CocoBiz", MARGIN, 36f, titlePaint)

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#BBDEFB")
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawText("Coconut Sales Manager", MARGIN, 54f, subtitlePaint)

        val reportTypePaint = Paint().apply {
            color = Color.WHITE
            textSize = 12f
            isAntiAlias = true
        }
        val reportLabel = "Report: $reportTitle"
        val labelWidth = reportTypePaint.measureText(reportLabel)
        canvas.drawText(reportLabel, PAGE_WIDTH - MARGIN - labelWidth, 36f, reportTypePaint)

        val datePaint = Paint().apply {
            color = Color.parseColor("#BBDEFB")
            textSize = 10f
            isAntiAlias = true
        }
        val dateLabel = "Generated: ${LocalDate.now().format(dateFormatter)}"
        val dateWidth = datePaint.measureText(dateLabel)
        canvas.drawText(dateLabel, PAGE_WIDTH - MARGIN - dateWidth, 54f, datePaint)

        // Revenue summary card background
        val cardBgPaint = Paint().apply {
            color = Color.parseColor("#E3F2FD")
            style = Paint.Style.FILL
        }
        val cardRect = Rect(MARGIN.toInt(), 84, (PAGE_WIDTH - MARGIN).toInt(), 148)
        canvas.drawRoundRect(
            cardRect.left.toFloat(), cardRect.top.toFloat(),
            cardRect.right.toFloat(), cardRect.bottom.toFloat(),
            12f, 12f, cardBgPaint
        )

        val revenueLabelPaint = Paint().apply {
            color = Color.parseColor("#1565C0")
            textSize = 11f
            isAntiAlias = true
        }
        canvas.drawText("Total Revenue", MARGIN + 16f, 106f, revenueLabelPaint)

        val revenueValuePaint = Paint().apply {
            color = Color.parseColor("#0D47A1")
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("Rs. ${String.format("%,.2f", totalRevenue)}", MARGIN + 16f, 134f, revenueValuePaint)

        val txPaint = Paint().apply {
            color = Color.parseColor("#1565C0")
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawText("${sales.size} transaction(s)", PAGE_WIDTH - MARGIN - 100f, 120f, txPaint)

        // Table header
        val tableTop = 164f
        val tableHeaderBg = Paint().apply {
            color = Color.parseColor("#1565C0")
            style = Paint.Style.FILL
        }
        canvas.drawRect(MARGIN, tableTop, PAGE_WIDTH - MARGIN, tableTop + 22f, tableHeaderBg)

        val colHeaderPaint = Paint().apply {
            color = Color.WHITE
            textSize = 9f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        canvas.drawText("DEALER", MARGIN + 6f, tableTop + 15f, colHeaderPaint)
        canvas.drawText("PLACE", MARGIN + 120f, tableTop + 15f, colHeaderPaint)
        canvas.drawText("TYPE", MARGIN + 210f, tableTop + 15f, colHeaderPaint)
        canvas.drawText("QTY", MARGIN + 295f, tableTop + 15f, colHeaderPaint)
        canvas.drawText("RATE", MARGIN + 345f, tableTop + 15f, colHeaderPaint)
        canvas.drawText("AMOUNT", MARGIN + 405f, tableTop + 15f, colHeaderPaint)
        canvas.drawText("STATUS", MARGIN + 480f, tableTop + 15f, colHeaderPaint)

        // Table rows
        val rowPaint = Paint().apply { isAntiAlias = true; textSize = 9f; color = Color.parseColor("#212121") }
        val altRowBg = Paint().apply { color = Color.parseColor("#F5F5F5"); style = Paint.Style.FILL }
        val linePaint = Paint().apply {
            color = Color.parseColor("#E0E0E0")
            strokeWidth = 0.5f
        }

        var y = tableTop + 22f
        sales.forEachIndexed { index, sale ->
            val rowHeight = 20f
            if (index % 2 == 0) {
                canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + rowHeight, altRowBg)
            }
            canvas.drawLine(MARGIN, y + rowHeight, PAGE_WIDTH - MARGIN, y + rowHeight, linePaint)

            canvas.drawText(sale.dealerName.take(18), MARGIN + 6f, y + 13f, rowPaint)
            canvas.drawText(sale.dealerPlace.take(14), MARGIN + 120f, y + 13f, rowPaint)
            canvas.drawText(sale.coconutType.displayName.take(12), MARGIN + 210f, y + 13f, rowPaint)
            canvas.drawText(
                if (sale.coconutType.name == "TONNAGE") "${sale.quantity}T" else "${sale.quantity.toInt()}Pc",
                MARGIN + 295f, y + 13f, rowPaint
            )
            canvas.drawText("Rs.${sale.rate.toInt()}", MARGIN + 345f, y + 13f, rowPaint)
            canvas.drawText("Rs.${String.format("%,.0f", sale.totalAmount)}", MARGIN + 405f, y + 13f, rowPaint)

            val statusPaint = Paint().apply {
                isAntiAlias = true
                textSize = 9f
                color = if (sale.status == SaleStatus.COMPLETED)
                    Color.parseColor("#388E3C") else Color.parseColor("#1565C0")
            }
            canvas.drawText(sale.status.displayName, MARGIN + 480f, y + 13f, statusPaint)

            y += rowHeight
            if (y > PAGE_HEIGHT - 60f) return
        }

        // Footer
        val footerPaint = Paint().apply {
            color = Color.parseColor("#9E9E9E")
            textSize = 9f
            isAntiAlias = true
        }
        canvas.drawLine(
            MARGIN, PAGE_HEIGHT - 40f,
            PAGE_WIDTH - MARGIN, PAGE_HEIGHT - 40f,
            linePaint
        )
        canvas.drawText(
            "CocoBiz — Coconut Sales Manager",
            MARGIN, PAGE_HEIGHT - 24f, footerPaint
        )
        val pageLabel = "Page 1"
        val pageLabelWidth = footerPaint.measureText(pageLabel)
        canvas.drawText(pageLabel, PAGE_WIDTH - MARGIN - pageLabelWidth, PAGE_HEIGHT - 24f, footerPaint)
    }

    private fun savePdf(context: Context, pdfDocument: PdfDocument, fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, "Download/CocoBiz")
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: throw IllegalStateException("Failed to create MediaStore entry")
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                pdfDocument.writeTo(stream)
            }
        } else {
            @Suppress("DEPRECATION")
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "CocoBiz"
            )
            dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { stream ->
                pdfDocument.writeTo(stream)
            }
        }
    }
}
