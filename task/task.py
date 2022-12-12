from enum import Enum, unique, auto
import json
import os
from adb import get_dir_and_file
@unique
class TaskList(Enum):
    classify = auto()


class Task:
    def __init__(self, task_name:str):
        self.task_name = None
        for name, member in TaskList.__members__.items():
            if(task_name == name):
                self.task_name = member
                break
        

    def generate_config(self, file_dir_path, dataset_path, model_path):
        dir_names = os.listdir(dataset_path)
        json_dict = {}
        json_dict["task_name"] = self.task_name.name
        json_dict["dataset_path"] = file_dir_path + "/" + "dataset"
        json_dict["model_path"] = file_dir_path + "/" + get_dir_and_file(model_path).get("file_name")
        if self.task_name is TaskList.classify:
            json_dict["classes"] = dir_names
        with open(r".\config.json","w") as f:
            json.dump(json_dict,f)

    def start_main_activity():
        pass
