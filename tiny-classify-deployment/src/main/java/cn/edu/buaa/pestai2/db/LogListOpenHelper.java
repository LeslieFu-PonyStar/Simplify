package cn.edu.buaa.pestai2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LogListOpenHelper extends SQLiteOpenHelper {
    public LogListOpenHelper(Context context){
        super(context, "logList.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists loglist (id INTEGER PRIMARY KEY AUTOINCREMENT, image BLOB, output TEXT, time TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //暂不需要使用升级版本的代码
    }
}
