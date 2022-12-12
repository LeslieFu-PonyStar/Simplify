package cn.ponystar.simplifymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class GenerateFileDirPathActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_file_path);
        String appDirPath = getExternalFilesDir(null).toString();
        utils.sendMessageToComputer("AppDirPath", appDirPath);
    }

}