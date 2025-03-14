package com.example.sceneform_maintained_test

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.view.PixelCopy
import android.widget.Toast
import androidx.print.PrintHelper
import com.google.ar.sceneform.ArSceneView
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

public class PhotoSaver (
    private val activity: ARActivity
    ){


    private fun generateFilename(): String?{
        val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)?.absolutePath +
                "/MuseuDoCangacoAR/${date}_screenshot.jpg"
    }

    private fun saveBitmapToGallery(bmp: Bitmap){
        val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val contentValue = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${date}_screenshot.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/MuseuDoCangacoAR")
        }
        val uri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)
        activity.contentResolver.openOutputStream(uri ?: return).use { outputStream->
            outputStream?.let {
                try {
                    saveDataToGallery(bmp, outputStream)
                } catch (e: IOException) {
                    Toast.makeText(activity, "Failed to save bitmap to gallery.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun saveBitmapToGallery(bmp: Bitmap, fileName: String) {
        val out = File(fileName)
        if(!out.parentFile.exists()){
            out.parentFile.mkdirs()
        }
        try {
            val outputStream = FileOutputStream(fileName)
            saveDataToGallery(bmp, outputStream)
            MediaScannerConnection.scanFile(activity, arrayOf(fileName), null, null)
        }catch (e: IOException){
            Toast.makeText(activity, "Failed to save bitmap to gallery.", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveDataToGallery(bmp: Bitmap, outputStream: OutputStream){
        val outputData = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputData)
        outputData.writeTo(outputStream)
        outputStream.flush()
        outputStream.close()
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        return Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
    }

    fun takePhoto(arSceneView: ArSceneView,imageResult: ImageResult) {
        val bmp =
            Bitmap.createBitmap(arSceneView.width, arSceneView.height, Bitmap.Config.ARGB_8888)
        var watermark =
            BitmapFactory.decodeResource(activity.resources, R.drawable.ra2)
        watermark = getResizedBitmap(watermark, 300,300)
        val handlerThread = HandlerThread("PixelCopyThread")
        handlerThread.start()

        PixelCopy.request(arSceneView, bmp, { result ->
            if (result == PixelCopy.SUCCESS) {
                val canvas = Canvas(bmp)
                canvas.drawBitmap(watermark, 0f, ((bmp.height) - (watermark.height*1)).toFloat(), null)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    val fileName = generateFilename()
                    saveBitmapToGallery(bmp, fileName ?: return@request)
                } else {
                    saveBitmapToGallery(bmp)
                }
                activity.runOnUiThread {
                    Toast.makeText(activity, "Foto capturada!", Toast.LENGTH_LONG).show()
                }
                imageResult.onResult(bmp)
            } else {
                activity.runOnUiThread {
                    Toast.makeText(activity, "Não foi possível capturar a foto!", Toast.LENGTH_LONG).show()
                }
            }
            handlerThread.quitSafely()
        }, Handler(handlerThread.looper))
    }

    fun doPhotoPrint()
    {
        val fileName = generateFilename()
        val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        activity?.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FILL
            }.also { printHelper ->
                //var droids = R.drawable.image
                val bitmap = BitmapFactory.decodeResource(context.resources, "${date}_screenshot,jpg".toInt())
                printHelper.printBitmap(fileName?:return@also, bitmap)
            }
        }
    }

    companion object {
        lateinit var doPhotoPrint: Any
    }

}