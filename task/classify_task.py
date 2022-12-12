import os
import subprocess
import re
import json
from .task import Task
from adb import *

class ClassifyTask(Task):
    def __init__(self, dataset_path, model_path, batch_size):
        """逻辑上讲，只要更换了模型或者数据集，就是一个新的task，所以需要重新初始化（或更改成员变量）"""
        super().__init__("classify")
        self.dataset_path = dataset_path
        self.model_path = model_path
        self.batch_size = batch_size

    def start_main_activity(self):
        logcat_clean()
        # 获取应用文件夹
        file_dir_path = ""
        try:
            file_dir_path = get_file_dir_path()
        except FunctionTimedOut as e:
            print('function timeout + msg = ', e.msg)
            error_handle()

        self.generate_config(file_dir_path, self.dataset_path, self.model_path)
        activity_name = app_name + "MainActivity"
        start_cmd = subprocess.call([adb_path, "shell","am", "start", "-n", activity_name])
        add_sdcard_file(r".\config.json", file_dir_path + "/config.json")
        add_sdcard_file(self.model_path, file_dir_path + "/" + get_dir_and_file(self.model_path).get("file_name"))
        # 配置文件是否存在
        config_found_listener = subprocess.Popen([adb_path, "logcat","Config:I", "*:S"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
        config_not_found_pattern = re.compile(r'Config file is not found.')
        config_found_pattern = re.compile(r'Config file is found.')
        while(True):
            line = config_found_listener.stdout.readline()
            res_config_not_found = re.search(config_not_found_pattern, line)
            if(res_config_not_found != None):
                add_sdcard_file(r".\config.json", file_dir_path + "/config.json")
                continue
            res_config_found = re.search(config_found_pattern, line)
            if(res_config_found != None):
                config_found_listener.kill()
                break;
        # Service创建过程是否存在问题
        load_file_listener = subprocess.Popen([adb_path, "logcat","LoadFile:I", "*:S"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
        load_file_success_pattern = re.compile(r"Successfully")
        load_file_fail_pattern = re.compile(r"Error,")
        while(True):
            line = load_file_listener.stdout.readline()
            if(re.search(load_file_success_pattern, line) != None):
                break
            elif(re.search(load_file_fail_pattern, line) != None):
                error_handle()
                return
        load_file_listener.kill()
        # 批处理
        batch_test_end_listener = subprocess.Popen([adb_path, "logcat","BatchTestEnd:I", "*:S"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
        batch_test_end_pattern = re.compile(r'I BatchTestEnd:')

        
        dir_names = os.listdir(self.dataset_path)
        class_num = len(dir_names)
        class_id = 0
        for dir in dir_names:
            batch_id = 0
            class_dir = os.path.join(self.dataset_path, dir)
            class_size = len(os.listdir(class_dir))
            while(True):
                line = batch_test_end_listener.stdout.readline()
                res = re.search(batch_test_end_pattern, line)
                if(res != None):
                    file_begin = batch_id*self.batch_size + 1
                    if(batch_id == int((class_size - 1) / self.batch_size)):
                        file_end = class_size
                        add_sdcard_files(class_dir, file_dir_path + "/dataset", False, file_begin, file_end)
                        subprocess.call([adb_path, "shell", "am", "startservice", "-n",  app_name + "BatchTestService", "--ei", "currentClass", str(class_id)])
                        break
                    else:
                        file_end = batch_id*self.batch_size + self.batch_size
                        add_sdcard_files(class_dir, file_dir_path + "/dataset", False, file_begin, file_end)
                        subprocess.call([adb_path, "shell", "am", "startservice", "-n",  app_name + "BatchTestService", "--ei", "currentClass", str(class_id)])
                    batch_id += 1
            class_id += 1
        
        line = batch_test_end_listener.stdout.readline()
        res = re.search(batch_test_end_pattern, line)
        if(res != None):
            subprocess.call([adb_path, "shell", "am", "startservice", "-n", app_name + "BatchTestService", "--ei", "currentClass", str(class_num)])
            batch_test_end_listener.kill()
        result_write_listenser = subprocess.Popen([adb_path, "logcat","CsvWrite:I", "*:S"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
        result_write_pattern = re.compile(r"I CsvWrite")
        while(True):
            line = result_write_listenser.stdout.readline()
            res = re.search(result_write_pattern, line)
            if(res != None):
                subprocess.call([adb_path, "pull", file_dir_path + "/result.csv", r".\result.csv"])
                result_write_listenser.kill()
                break
