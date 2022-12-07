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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import cn.ponystar.simplifymobile.entity.task.ClassifyTask;

public class BatchTestService extends Service {
    private Module module;
    private String appDirPath;
    private String configPath;
    private File datasetDir;
    private ClassifyTask task;
    private boolean startState;
    private StringBuilder errorMessages;
    private int[][] resArrays;
    private long time;
    public BatchTestService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //onCreate方法用于第一次启动service时进行初始化，在后续再次启动该service时不会再次初始化，而是再次启动
    //startCommand方法
    @Override
    public void onCreate() {
        super.onCreate();
        //应用独占的文件路径，其它应用无法访问
        this.appDirPath = getExternalFilesDir(null).toString();
        this.configPath = utils.joinPath(this.appDirPath, "config.json");
        this.datasetDir = new File(utils.joinPath(this.appDirPath, "dataset"));
        this.datasetDir.mkdirs();
        this.task = new ClassifyTask(configPath);
        this.errorMessages = new StringBuilder();
        int classSize = this.task.getClassNames().length();
        this.resArrays = new int[classSize][classSize];
        this.time = 0;
        if(this.task.getGenerateState().containsKey(false)){
            for(Map.Entry<Boolean, String> entry : this.task.getGenerateState().entrySet()){
                if(!entry.getKey()){
                    this.errorMessages.append(entry.getValue()).append(",");
                }
            }
            this.startState = false;
        }
        else{
            try{
                this.module = Module.load(task.getModelPath());
                this.startState = true;
            }catch (Exception e){
                StringBuilder moduleLoadFail = new StringBuilder("Model file maybe not correct.");
                this.errorMessages.append(moduleLoadFail.append(","));
                this.startState = false;
            }
        }
        if(this.startState)
            Log.i("LoadFile", "Successfully");
        else
            Log.i("LoadFile", "Error," + this.errorMessages.toString());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(this.startState) {
            //通过adb发送的参数判断当前数据集的真实标签
            int currentClassId = intent.getIntExtra("currentClass", -1);
            if(currentClassId < 0){
                Log.i("BatchTestEnd", "Activate");
            }else if (currentClassId < this.task.getClassNames().length()) {
                File[] imageList = this.datasetDir.listFiles();
                if (imageList != null) {
                    for (File image : imageList) {
                        long startTime = System.currentTimeMillis();
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
                        this.time += System.currentTimeMillis() - startTime;
                        Log.i("res", "" + maxScoreIdx);
                        this.resArrays[currentClassId][maxScoreIdx] += 1;
                    }
                    //先删除图像文件，再发消息
                    utils.deleteDir(datasetDir, true);
                    Log.i("BatchTestEnd", "Current batch test end.");
                }
            } else {
                try {
                    PrintWriter csvWriter = new PrintWriter(utils.joinPath(this.appDirPath, "result.csv"));
                    StringBuilder resSb = new StringBuilder();
                    for (int i = 0; i < this.task.getClassNames().length(); i++) {
                        for(int j = 0; j < this.task.getClassNames().length(); j++){
                            resSb.append(resArrays[i][j]).append(",");
                        }
                        resSb.append("\n");
                    }
                    resSb.append(this.time);
                    csvWriter.write(resSb.toString());
                    csvWriter.close();
                } catch (Exception e) {
                    Log.i("error", e.toString());
                } finally {
                    Log.i("CsvWrite", "Current batch test end.");
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}