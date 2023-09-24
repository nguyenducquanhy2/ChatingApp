package com.example.chatingappver2.presenter.activity

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.example.chatingappver2.contract.activity.ViewFullScreenContract
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ViewFullScreenActivityPresenter(private val view: ViewFullScreenContract.view, private val context: Context):
    ViewFullScreenContract.presenter {
    override fun saveImage(ImageView: ImageView) {
        val bitmap = getImageBitmap(ImageView)

//        //Generating a file name
        val filename = "${System.currentTimeMillis()}"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        }
        else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(context, "Saved to Photos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageBitmap(imageView: ImageView): Bitmap {
        val drawable = imageView.drawable as BitmapDrawable
        return drawable.bitmap
    }
}