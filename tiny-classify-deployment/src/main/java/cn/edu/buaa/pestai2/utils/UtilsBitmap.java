package cn.edu.buaa.pestai2.utils;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public class UtilsBitmap {
    public static ByteArrayOutputStream compressBitmap(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        while(byteArrayOutputStream.toByteArray().length / 1024 > 100){
            byteArrayOutputStream.reset();
            quality = quality - 9;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        }
        return byteArrayOutputStream;
    }
}
