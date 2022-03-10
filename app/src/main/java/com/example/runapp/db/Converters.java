package com.example.runapp.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

/**
 * Converter class to save bitmaps in the Room Database
 */
public class Converters {

    @TypeConverter
    public Bitmap toBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @TypeConverter
    public byte[] fromBitmap(Bitmap bmp){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG,1,outputStream);
        byte[] byteArray = outputStream.toByteArray();

        while (byteArray.length > 500000){
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.5), (int)(bitmap.getHeight()*0.5), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }

        return byteArray;
    }

}
