package com.mightsana.goodminton.features.main.result

import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ExportToPdf() {
    val context = LocalContext.current

    val fileName = "output_composable"

    CreateBitmapFromComposable(
        content = {
            ComposableToExport()
        }
    ) { bitmap ->
        createPdfFromBitmap(context, bitmap, "$fileName.pdf")

        // Tampilkan pesan sukses
        Toast.makeText(context, "PDF berhasil disimpan di: $fileName.pdf", Toast.LENGTH_SHORT).show()

    }

    Button(
        onClick = {
            Toast.makeText(
                context,
                "PDF is saved in: $fileName.pdf",
                Toast.LENGTH_SHORT
            ).show()
        }
    ) {
        Text("Save PDF")
    }
}

@Composable
fun ComposableToExport() {
    Text("Hello World")
}