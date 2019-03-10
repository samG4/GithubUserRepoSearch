package com.shadow.githubsearch

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import android.support.v7.app.AlertDialog
import android.view.Window
import android.widget.FrameLayout
import com.squareup.picasso.Transformation


val GIT_REPO = "git"
val CONTRIBUTOR = "contributor"
fun Activity.showAlert(message: String?){
    val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
    } else {
        AlertDialog.Builder(this)
    }
    builder.setTitle("Alert")
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { _, _->
        }
        .setIcon(android.R.drawable.ic_dialog_alert)
        .show()
}


fun Context.getDialog(resId: Int): Dialog {
    val dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(resId)
    dialog.window?.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    return dialog
}


class CircleTransform : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(size, size, source.config)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(
            squaredBitmap,
            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
        )
        paint.shader = shader
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}