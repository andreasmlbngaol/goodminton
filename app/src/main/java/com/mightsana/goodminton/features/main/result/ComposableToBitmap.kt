package com.mightsana.goodminton.features.main.result

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.mightsana.goodminton.features.main.model.LeagueJoint
import com.mightsana.goodminton.features.main.model.LeagueParticipantJoint
import com.mightsana.goodminton.features.main.model.Match
import com.mightsana.goodminton.features.main.model.ParticipantStatsJoint
import java.io.ByteArrayOutputStream
import java.io.File

fun renderComposableToBitmap(
    context: Context,
    width: Int,
    height: Int,
    composable: @Composable () -> Unit
): Bitmap {
    val density = context.resources.displayMetrics.density
    val bitmap = Bitmap.createBitmap((width * density).toInt(), (height * density).toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Buat ComposeView
    val composeView = ComposeView(context).apply {
        setContent { composable() }
    }

    // Tambahkan ComposeView ke WindowManager sementara
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val layoutParams = WindowManager.LayoutParams(
        1, // Dimensi kecil karena hanya sementara
        1,
        WindowManager.LayoutParams.TYPE_APPLICATION,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    )

    windowManager.addView(composeView, layoutParams)

    // Render konten ke bitmap
    composeView.post {
        composeView.measure(
            View.MeasureSpec.makeMeasureSpec((width * density).toInt(), View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec((height * density).toInt(), View.MeasureSpec.EXACTLY)
        )
        composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)
        composeView.draw(canvas)

        // Hapus ComposeView dari WindowManager
        windowManager.removeView(composeView)
    }

    return bitmap
}

fun saveBitmapAsPdf(context: Context, bitmap: Bitmap, fileName: String): File {
    val pdfFile = File(context.getExternalFilesDir(null), "$fileName.pdf")
    val document = PdfDocument()

    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = document.startPage(pageInfo)

    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
    document.finishPage(page)

    pdfFile.outputStream().use {
        document.writeTo(it)
    }
    document.close()

    return pdfFile
}

fun movePdfToExternalStorage(context: Context, sourceFile: File): File {
    val externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val destinationFile = File(externalDir, sourceFile.name)

    sourceFile.inputStream().use { input ->
        destinationFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return destinationFile
}

fun savePdfToMediaStore(context: Context, fileName: String, pdfData: ByteArray): Uri? {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, fileName) // Nama file
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf") // Tipe file
        put(MediaStore.Downloads.IS_PENDING, 1) // Tandai sebagai "sedang ditulis"
    }

    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        resolver.openOutputStream(it)?.use { outputStream ->
            outputStream.write(pdfData) // Tulis data PDF ke output stream
        }

        // Tandai file selesai ditulis
        contentValues.clear()
        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
    }
    return uri
}

fun saveBitmapAsPdfToByteArray(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    val document = PdfDocument()

    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = document.startPage(pageInfo)

    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
    document.finishPage(page)

    document.writeTo(outputStream)
    document.close()

    return outputStream.toByteArray()
}

fun generateAndSaveReportToMediaStore(
    context: Context,
    league: LeagueJoint,
    participants: List<LeagueParticipantJoint>,
    participantsStats: List<ParticipantStatsJoint>,
    matches: List<Match>,
    fileName: String
) {
    // Render composable menjadi bitmap
    val bitmap = renderComposableToBitmap(
        context,
        width = 1080,
        height = 1920
    ) {
        LeagueReport(
            league = league,
            participants = participants,
            participantsStats = participantsStats,
            matches = matches
        )
    }

    // Konversi bitmap ke PDF ByteArray
    val pdfData = saveBitmapAsPdfToByteArray(bitmap)

    // Simpan ke MediaStore
    val uri = savePdfToMediaStore(context, fileName, pdfData)

    if (uri != null) {
        Toast.makeText(context, "PDF berhasil disimpan di folder Downloads", Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(context, "Gagal menyimpan PDF", Toast.LENGTH_LONG).show()
    }
}
