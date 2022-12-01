package cn.ponystar.simplifymobile.entity.task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

//该类用于描述使用神经网络的任务目标
public interface Task {
    //生成task所需要的任务名，数据集路径以及模型路径和各个task自己需要的东西
    void generateTask();
}
