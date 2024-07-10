package work.wander.funnyface.domain.bitmap

import android.graphics.Bitmap
import android.graphics.Matrix

fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix().apply {
        postRotate(degrees)
    }
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}