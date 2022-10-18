package com.androiddevs.RunTracker.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class Converters {

    @TypeConverter
    fun fromBitmap (bmp:Bitmap?): ByteArray? {
        if(bmp!=null){
            val outputStream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG,100,outputStream)
            return outputStream.toByteArray()
        }
        else {
            return null
        }
    }

    @TypeConverter
    fun toBitmap(Bytes:ByteArray?):Bitmap?{
        if (Bytes != null) {
            return BitmapFactory.decodeByteArray(Bytes,0,Bytes.size)
        }else{
            return null
        }
    }

}