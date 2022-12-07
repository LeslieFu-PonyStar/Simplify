package cn.ponystar.simplifymobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends Activity {

    private ImageView imageView;
    private String appDirPath;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imageView = findViewById(R.id.imageView);
        this.appDirPath = getExternalFilesDir(null).toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String configPath = utils.joinPath(this.appDirPath, "config.json");
        while(! new File(configPath).exists()){
            Log.i("Config", "Config file is not found.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i("Config", "Config file is found.");
        Intent batchTestIntent = new Intent(MainActivity.this, BatchTestService.class);
        startService(batchTestIntent);
    }
}