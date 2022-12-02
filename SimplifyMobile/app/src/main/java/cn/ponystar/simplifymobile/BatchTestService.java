package cn.ponystar.simplifymobile;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pytorch.IValue;
import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import cn.ponystar.simplifymobile.entity.task.ClassifyTask;

public class BatchTestService extends Service {
    private Module module;
    private String appDirPath;
    private String configPath;
    private ClassifyTask task;
    private JSONObject resJson;
    private HashMap<String, JSONArray> resArraysHashMap;
    public BatchTestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //应用独占的文件路径，其它应用无法访问
        this.appDirPath = getExternalFilesDir(null).toString();
        if(appDirPath.charAt(appDirPath.length() - 1) == '/')
            this.configPath = appDirPath + "config.json";
        else
            this.configPath = appDirPath + "/config.json";
        try{
            this.task = new ClassifyTask(configPath);
            this.module = Module.load(task.getModelPath());
        }catch (Exception e){
            Log.e("MainActivity", "File not found");
        }
        this.resJson = new JSONObject();
        this.resArraysHashMap = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //通过adb发送的参数判断当前数据集的真实标签
        int currentClassId = intent.getIntExtra("currentClass", 0);
        if(currentClassId < this.task.getClassNames().length()){
            String currentClassName = "";
            try {
                currentClassName = this.task.getClassNames().get(currentClassId).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(!this.resArraysHashMap.containsKey(currentClassName)){
                this.resArraysHashMap.put(currentClassName, new JSONArray());
            }
            File datasetDir = this.task.getDatasetDirPath();
            File[] imageList = datasetDir.listFiles();
            Log.i("datasetDir", datasetDir.toString());
            if (imageList != null) {
                for (File image : imageList) {
                    Bitmap bitmap = utils.getBitmap(image);
                    //如果传入了非图像文件，则开启下一轮循环
                    if (bitmap == null)
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
                    Objects.requireNonNull(this.resArraysHashMap.get(currentClassName)).put(maxScoreIdx);
                }
                //先删除图像文件，再发消息
                utils.deleteDir(datasetDir, true);
                Log.i("BatchTestEnd", "Current batch test end.");
            }
        }
        else{
            try {
                for(int i = 0; i < resArraysHashMap.size(); i++){
                    String className = this.task.getClassNames().get(i).toString();
                    resJson.put(className, this.resArraysHashMap.get(className));
                }
                utils.jsonWrite(this.appDirPath + "/result.json", resJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}