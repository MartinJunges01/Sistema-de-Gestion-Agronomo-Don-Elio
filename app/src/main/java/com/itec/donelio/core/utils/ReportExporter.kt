package com.itec.donelio.core.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.itec.donelio.domain.model.Cosecha
import com.itec.donelio.domain.model.InsumoResumen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clase utilitaria para exportar reportes (CSV, PDF) mediante SAF.
 */
object ReportExporter {

    suspend fun exportToCsv(uri: Uri, context: Context, data: List<InsumoResumen>, cosechas: List<Cosecha>, campaniaNombre: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val csvHeader = "Campaña: $campaniaNombre\n\n--- INSUMOS ---\nInsumo,Cantidad,Total ($)\n"
                    outputStream.write(csvHeader.toByteArray())
                    
                    data.forEach { insumo ->
                        val line = "${insumo.nombreInsumo},${insumo.cantidadTotal},${insumo.costoTotal}\n"
                        outputStream.write(line.toByteArray())
                    }
                    
                    val csvCosechasHeader = "\n--- COSECHAS ---\nFecha,Cantidad (Tn),Destino\n"
                    outputStream.write(csvCosechasHeader.toByteArray())
                    
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    cosechas.forEach { cosecha ->
                        val destino = if (cosecha.almacen.isNotBlank()) "Almacén: ${cosecha.almacen}" else "Venta"
                        val fechaStr = dateFormat.format(Date(cosecha.fecha))
                        val line = "${fechaStr},${cosecha.cantidad},${destino}\n"
                        outputStream.write(line.toByteArray())
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun exportToPdf(uri: Uri, context: Context, data: List<InsumoResumen>, cosechas: List<Cosecha>, campaniaNombre: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                var page = pdfDocument.startPage(pageInfo)
                var canvas: Canvas = page.canvas
                val paint = Paint()
                var yPosition = 50f
                var pageNum = 1

                fun checkPageBreak(requiredSpace: Float = 30f) {
                    if (yPosition + requiredSpace > 800f) {
                        pdfDocument.finishPage(page)
                        pageNum++
                        val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNum).create()
                        page = pdfDocument.startPage(newPageInfo)
                        canvas = page.canvas
                        yPosition = 50f
                    }
                }

                fun drawInsumosHeader() {
                    paint.textSize = 16f
                    paint.isFakeBoldText = true
                    canvas.drawText("Insumos", 50f, yPosition, paint)
                    
                    paint.textSize = 14f
                    yPosition += 25f
                    canvas.drawText("Insumo", 50f, yPosition, paint)
                    canvas.drawText("Cantidad", 300f, yPosition, paint)
                    canvas.drawText("Total ($)", 450f, yPosition, paint)
                    
                    yPosition += 10f
                    canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
                    yPosition += 30f
                }

                fun drawCosechasHeader() {
                    paint.textSize = 16f
                    paint.isFakeBoldText = true
                    canvas.drawText("Cosechas", 50f, yPosition, paint)
                    
                    paint.textSize = 14f
                    yPosition += 25f
                    canvas.drawText("Fecha", 50f, yPosition, paint)
                    canvas.drawText("Cantidad (Tn)", 250f, yPosition, paint)
                    canvas.drawText("Destino", 450f, yPosition, paint)
                    
                    yPosition += 10f
                    canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
                    yPosition += 30f
                }

                // Título
                paint.color = Color.BLACK
                paint.textSize = 24f
                paint.isFakeBoldText = true
                canvas.drawText("Reporte de Campaña", 50f, yPosition + 30f, paint)
                yPosition += 60f

                // Subtítulo
                paint.textSize = 14f
                paint.isFakeBoldText = false
                canvas.drawText("Sistema de Gestión Agrónomo - Don Elio", 50f, yPosition, paint)
                yPosition += 25f

                // Nombre de Campaña
                paint.isFakeBoldText = true
                canvas.drawText("Campaña: $campaniaNombre", 50f, yPosition, paint)
                yPosition += 45f

                // SECCION INSUMOS
                drawInsumosHeader()
                
                paint.isFakeBoldText = false
                var granTotal = 0.0
                
                data.forEach { insumo ->
                    if (yPosition > 800f) {
                        checkPageBreak(30f)
                        drawInsumosHeader()
                        paint.isFakeBoldText = false
                    }
                    
                    canvas.drawText(insumo.nombreInsumo, 50f, yPosition, paint)
                    canvas.drawText(insumo.cantidadTotal.toString(), 300f, yPosition, paint)
                    canvas.drawText(String.format("%.2f", insumo.costoTotal), 450f, yPosition, paint)
                    
                    granTotal += insumo.costoTotal
                    yPosition += 30f
                }
                
                // Total Final Insumos
                checkPageBreak(50f)
                yPosition += 10f
                paint.isFakeBoldText = true
                canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
                yPosition += 25f
                canvas.drawText("Total Gasto Insumos:", 250f, yPosition, paint)
                canvas.drawText(String.format("$ %.2f", granTotal), 450f, yPosition, paint)

                // SECCION COSECHAS
                yPosition += 50f
                checkPageBreak(70f)
                drawCosechasHeader()
                
                paint.isFakeBoldText = false
                
                var totalCosecha = 0.0
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                
                cosechas.forEach { cosecha ->
                    if (yPosition > 800f) {
                        checkPageBreak(30f)
                        drawCosechasHeader()
                        paint.isFakeBoldText = false
                    }
                    
                    val fechaStr = dateFormat.format(Date(cosecha.fecha))
                    val destino = if (cosecha.almacen.isNotBlank()) "Almacén" else "Venta"
                    
                    canvas.drawText(fechaStr, 50f, yPosition, paint)
                    canvas.drawText(String.format("%.2f", cosecha.cantidad), 250f, yPosition, paint)
                    canvas.drawText(destino, 450f, yPosition, paint)
                    
                    totalCosecha += cosecha.cantidad
                    yPosition += 30f
                }
                
                // Total Final Cosechas
                checkPageBreak(50f)
                yPosition += 10f
                paint.isFakeBoldText = true
                canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
                yPosition += 25f
                canvas.drawText("Total Tn Cosechadas:", 200f, yPosition, paint)
                canvas.drawText(String.format("%.2f Tn", totalCosecha), 380f, yPosition, paint)

                pdfDocument.finishPage(page)

                // Escribir a uri
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                
                pdfDocument.close()
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
