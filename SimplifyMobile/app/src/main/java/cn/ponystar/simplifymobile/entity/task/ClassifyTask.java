package cn.ponystar.simplifymobile.entity.task;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ClassifyTask implements Task {
    private final String jsonPath;
    private String taskName;
    private String modelPath;
    private JSONArray classNames;
    private Map<Boolean, String> generateState;
    public ClassifyTask(String jsonPath){
        this.jsonPath = jsonPath;
        this.generateState = new HashMap<>();
        generateTask();
    }
    public String getTaskName() {
        return taskName;
    }



    public String getModelPath() {
        return modelPath;
    }

    public JSONArray getClassNames() {
        return classNames;
    }

    public Map<Boolean, String> getGenerateState() {
        return generateState;
    }
    public void putGenerateState(boolean state, String description){
        this.generateState.put(state, description);
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
                this.generateState.put(false, "Dataset path is incorrect or not exist.");
            }else if(!new File(json.getString("model_path")).exists()){
                this.generateState.put(false, "Model path is incorrect or not exist.");
            }
            this.modelPath = json.getString("model_path");
            this.classNames = json.getJSONArray("classes");
            this.generateState.put(true, "");
        }catch (IOException e){
            this.generateState.put(false, "Stream Error.");
        }catch (JSONException e){
            this.generateState.put(false, "Some json key not exist.");
        }
    }
}
