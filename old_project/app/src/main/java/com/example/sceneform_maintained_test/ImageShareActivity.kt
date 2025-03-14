package com.example.sceneform_maintained_test

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ImageShareActivity : AppCompatActivity()
{


    lateinit var imageView: ImageView
    lateinit var btnShare:Button
    lateinit var bitmap: Bitmap
    lateinit var file : File
    lateinit var uri : Uri
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_share)

        if(  ARActivity.bitmap==null)
            finish()


        file=  File(externalCacheDir, "image.jpg")
        uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)
        bitmap=  ARActivity.bitmap

        btnShare=findViewById(R.id.btnShare)
        imageView=findViewById(R.id.imageView)
        imageView.setImageBitmap(bitmap)

        btnShare.setOnClickListener {

            saveImage(bitmap)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/png"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "Share"))

        }
    }

    private fun saveImage(image: Bitmap)
    {

        try
        {

            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException)
        {
            Log.d("ImageShareActivity",  e.message.toString())
        }
    }
}