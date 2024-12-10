package com.mightsana.goodminton.features.main.result

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import java.io.FileOutputStream

fun createPdfFromBitmap(context: Context, bitmap: Bitmap, fileName: String) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument
        .PageInfo
        .Builder(bitmap.width, bitmap.height, 1)
        .create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    canvas.drawBitmap(bitmap, 0f, 0f, null)
    pdfDocument.finishPage(page)

    val filePath = context.getExternalFilesDir(null)?.absolutePath + "/$fileName.pdf"
    val fileOutputStream = FileOutputStream(filePath)
    pdfDocument.writeTo(fileOutputStream)

    pdfDocument.close()
    fileOutputStream.close()
}