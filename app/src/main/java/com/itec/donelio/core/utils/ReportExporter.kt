package com.itec.donelio.core.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.itec.donelio.domain.model.InsumoResumen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Clase utilitaria para exportar reportes (CSV, PDF) mediante SAF.
 */
object ReportExporter {

    suspend fun exportToCsv(uri: Uri, context: Context, data: List<InsumoResumen>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val csvHeader = "Insumo,Cantidad,Total ($)\n"
                    outputStream.write(csvHeader.toByteArray())
                    
                    data.forEach { insumo ->
                        val line = "${insumo.nombreInsumo},${insumo.cantidadTotal},${insumo.costoTotal}\n"
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

    suspend fun exportToPdf(uri: Uri, context: Context, data: List<InsumoResumen>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val pdfDocument = PdfDocument()
                
                // Formato A4 aprox (595 x 842 pt)
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas
                val paint = Paint()

                // Título
                paint.color = Color.BLACK
                paint.textSize = 24f
                paint.isFakeBoldText = true
                canvas.drawText("Reporte de Gastos por Insumo", 50f, 80f, paint)

                // Subtítulo
                paint.textSize = 14f
                paint.isFakeBoldText = false
                canvas.drawText("Sistema de Gestión Agrónomo - Don Elio", 50f, 110f, paint)

                // Encabezados de tabla
                paint.textSize = 16f
                paint.isFakeBoldText = true
                var yPosition = 160f
                canvas.drawText("Insumo", 50f, yPosition, paint)
                canvas.drawText("Cantidad", 300f, yPosition, paint)
                canvas.drawText("Total ($)", 450f, yPosition, paint)
                
                // Línea separadora
                yPosition += 10f
                canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
                
                // Filas
                paint.textSize = 14f
                paint.isFakeBoldText = false
                yPosition += 30f
                
                var granTotal = 0.0
                
                data.forEach { insumo ->
                    // Salto de página si excedemos altura
                    if (yPosition > 800f) {
                        pdfDocument.finishPage(page)
                        // Para simplificar, omitiremos el multi-page en esta versión inicial básica,
                        // asumiendo que los insumos entran en una sola página.
                    }
                    
                    canvas.drawText(insumo.nombreInsumo, 50f, yPosition, paint)
                    canvas.drawText(insumo.cantidadTotal.toString(), 300f, yPosition, paint)
                    canvas.drawText(String.format("%.2f", insumo.costoTotal), 450f, yPosition, paint)
                    
                    granTotal += insumo.costoTotal
                    yPosition += 30f
                }
                
                // Total Final
                yPosition += 10f
                paint.isFakeBoldText = true
                canvas.drawLine(50f, yPosition, 545f, yPosition, paint)
                yPosition += 25f
                canvas.drawText("Total Gasto Insumos:", 250f, yPosition, paint)
                canvas.drawText(String.format("$ %.2f", granTotal), 450f, yPosition, paint)

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
