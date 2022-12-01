package cn.ponystar.simplifymobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONObject;

import java.io.File;

public class GenerateFilePathActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String appDirPath = getExternalFilesDir(null).toString();
        String appDirPathWrite;
        if (!new File(appDirPath + "/dirPath.json").exists()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dir_path", appDirPath);

                if(appDirPath.charAt(appDirPath.length() - 1) == '/')
                    appDirPathWrite = appDirPath + "dirPath.json";
                else
                    appDirPathWrite = appDirPath + "/dirPath.json";

                utils.jsonWrite(appDirPathWrite,jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}