package cn.ponystar.simplifymobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView mainPageTextView;
    private ImageView imageView;



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mainPageTextView = findViewById(R.id.mainPageView);
        this.imageView = findViewById(R.id.imageView);
        String appDirPath = getExternalFilesDir(null).toString();
        Log.i("AppDirPath", appDirPath);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //查看当前文件的路径
        this.mainPageTextView.setText(getExternalFilesDir(null).toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}