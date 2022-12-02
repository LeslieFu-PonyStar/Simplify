package cn.ponystar.simplifymobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class utils {
    private static final String TAG = "MainActivity";
    //获取指定路径的图片
    public static Bitmap getBitmap(File file){
        byte[] buf = new byte[1024*1024*10];
        Bitmap bitmap;
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = fis.read(buf, 0, buf.length);
            bitmap = BitmapFactory.decodeByteArray(buf, 0, len);
            if (bitmap == null) {
                Log.e(TAG, "read error");
                return null;
            }
            fis.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
        return bitmap;
    }

    public static void jsonWrite(String jsonPath, JSONObject json) throws Exception{
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(jsonPath), StandardCharsets.UTF_8);
        osw.write(json.toString());
        osw.flush();
        osw.close();
    }

    public static void deleteDir(File dir, boolean saveDir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDir(file, false); // 递规的方式删除文件夹
        }
        if (!saveDir){
            dir.delete();// 删除目录本身
        }
    }
}
