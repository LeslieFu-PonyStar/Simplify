package cn.ponystar.simplifymobile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pytorch.IValue;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;

import cn.ponystar.simplifymobile.entity.task.ClassifyTask;


public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private TextView mainPageTextView;
    private ImageView imageView;

    private Module module;
    private String appDirPath;
    private String configPath;
    private ClassifyTask task;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mainPageTextView = findViewById(R.id.mainPageView);
        this.imageView = findViewById(R.id.imageView);

        //应用独占的文件路径，其它应用无法访问
        this.appDirPath = getExternalFilesDir(null).toString();
        if(appDirPath.charAt(appDirPath.length() - 1) == '/')
            this.configPath = appDirPath + "config.json";
        else
            this.configPath = appDirPath + "/config.json";

        this.task = new ClassifyTask(configPath);
        this.module = Module.load(task.getModelPath());
        
        //查看当前文件的路径
        this.mainPageTextView.setText(this.appDirPath);
    }

    @Override
    protected void onStart() {
        super.onStart();
        File[] imageList = this.task.getDatasetDirPath().listFiles();
        Log.i("datasetDir", this.task.getDatasetDirPath().toString());
        JSONObject resJson = new JSONObject();
        JSONArray resArray = new JSONArray();
        if (imageList != null) {
            for(File image : imageList){
                Bitmap bitmap = utils.getBitmap(image);
                //如果传入了非图像文件，则开启下一轮循环
                if(bitmap == null)
                    continue;
                Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                        TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST);
                Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
                float[] scores = outputTensor.getDataAsFloatArray();
                float maxScore = -Float.MAX_VALUE;
                int maxScoreIdx = -1;
                for (int i = 0; i < scores.length; i++) {
                    if (scores[i] > maxScore) {
                        maxScore = scores[i];
                        maxScoreIdx = i;
                    }
                }
                Log.i("res", "" + maxScoreIdx);
                resArray.put(maxScoreIdx);
            }
            try {
                resJson.put("Apple", resArray);
                utils.jsonWrite(this.appDirPath + "/result.json", resJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}