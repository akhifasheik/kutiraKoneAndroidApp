package com.kutira.kone.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt

@Singleton
class ImageUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Loads [uri], downscales longest edge to [maxEdgePx], compresses JPEG at [quality].
     */
    fun compressToJpeg(uri: Uri, maxEdgePx: Int = 1280, quality: Int = 82): ByteArray {
        val input: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Unable to open image stream")
        val raw = BitmapFactory.decodeStream(input)
            ?: throw IllegalStateException("Unable to decode bitmap")
        input.close()

        val scaled = scaleDown(raw, maxEdgePx)
        if (scaled != raw) {
            raw.recycle()
        }
        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
        scaled.recycle()
        return out.toByteArray()
    }

    private fun scaleDown(bitmap: Bitmap, maxEdge: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val longest = max(w, h)
        if (longest <= maxEdge) return bitmap
        val scale = maxEdge.toFloat() / longest
        val nw = (w * scale).roundToInt().coerceAtLeast(1)
        val nh = (h * scale).roundToInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, nw, nh, true)
    }
}
