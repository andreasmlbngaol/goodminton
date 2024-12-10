package com.mightsana.goodminton.features.main.result

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext

fun View.toBitmapWhenAttached(onBitmapReady: (Bitmap) -> Unit) {
    if (isAttachedToWindow) {
        // Jika sudah attach, langsung buat bitmap
        onBitmapReady(createBitmapFromView())
    } else {
        // Tambahkan listener untuk menunggu attach
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                removeOnAttachStateChangeListener(this)
                onBitmapReady(createBitmapFromView())
            }

            override fun onViewDetachedFromWindow(v: View) {}
        })
    }
}

// Fungsi pembantu untuk membuat bitmap dari view
private fun View.createBitmapFromView(): Bitmap {
    measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    )
    layout(0, 0, measuredWidth, measuredHeight)
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    draw(Canvas(bitmap))
    return bitmap
}

@Composable
fun CreateBitmapFromComposable(content: @Composable () -> Unit, onBitmapReady: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val composeView = ComposeView(context).apply {
        setContent { content() }
    }

    composeView.toBitmapWhenAttached { bitmap ->
        onBitmapReady(bitmap)
    }
}