package cn.ponystar.simplifymobile.entity.task;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClassifyTask implements Task {
    private final String jsonPath;
    private String taskName;
    private File datasetDirPath;
    private String modelPath;
    private JSONArray classNames;
    public ClassifyTask(String jsonPath){
        this.jsonPath = jsonPath;
        generateTask();
    }
    public String getTaskName() {
        return taskName;
    }

    public File getDatasetDirPath() {
        return datasetDirPath;
    }

    public String getModelPath() {
        return modelPath;
    }

    public JSONArray getClassNames() {
        return classNames;
    }

    @Override
    public void generateTask() {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(Files.newInputStream(Paths.get(this.jsonPath)));
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            JSONObject json = new JSONObject(builder.toString());//builder读取了JSON中的数据。
            this.taskName = json.getString("task_name");
            if (!new File(json.getString("dataset_path")).exists()) {
                Log.e("datapath", "exist error");
            }
            this.datasetDirPath = new File(json.getString("dataset_path"));
            this.modelPath = json.getString("model_path");
            this.classNames = json.getJSONArray("classes");
        }catch (IOException e){
            Log.e("IOException", ":", e);
        }catch (JSONException e){
            Log.e("JSONException", ":", e);
        }
    }
}
