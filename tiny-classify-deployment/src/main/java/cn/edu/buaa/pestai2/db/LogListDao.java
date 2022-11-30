package cn.edu.buaa.pestai2.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import java.util.ArrayList;

import cn.edu.buaa.pestai2.entity.Logger;


public class LogListDao  {
    private Context context;
    private LogListOpenHelper logListOpenHelper;
    private ArrayList<Logger> loggers;
    public LogListDao(Context context){
        super();
        this.logListOpenHelper = new LogListOpenHelper(context);
        this.context = context;
        this.loggers = new ArrayList<>();
    }
    //获取日志，形成列表
    public ArrayList<Logger> getLogList(){
        SQLiteDatabase db = logListOpenHelper.getReadableDatabase();
        Cursor c = db.query("loglist", new String[]{"id", "image", "output","time"}, null,null, null, null, null);
        while(c.moveToNext()){
            byte[] in = c.getBlob(c.getColumnIndex("image"));
            Bitmap bitmap = BitmapFactory.decodeByteArray(in, 0, in.length);
            String output = c.getString(c.getColumnIndex("output"));
            String time = c.getString(c.getColumnIndex("time"));
            Logger logger = new Logger();
            logger.setImage(bitmap);
            logger.setOutputClass(output);
            logger.setTrainDate(time);
            loggers.add(logger);
        }
        db.close();
        c.close();
        return loggers;
    }
}
