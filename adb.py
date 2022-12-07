import os
import subprocess
import re
import json
from func_timeout import func_set_timeout, FunctionTimedOut
# adb工具路径
adb_path = r'D:\CSTCloud\Freshman\SoftwareEngineering\Simplify\Simplify\adb\adb.exe'
app_name = 'cn.ponystar.simplifymobile/cn.ponystar.simplifymobile.'
def error_handle():
    pass
def add_sdcard_file(local_path, remote_path):
    """添加单个文件"""
    if(os.path.exists(local_path)):
        subprocess.call([adb_path, "push", local_path, remote_path])

    else:
        error_handle()


def add_sdcard_files(local_path, remote_path, recursion:bool, file_begin, file_end):
    if(recursion == True):
        """递归调用该函数，实现文件夹整体的复制"""
        local_file_list = os.listdir(local_path)
        for file in local_file_list:
            subpath = os.path.join(local_path, file)
            if os.path.isdir(subpath):
                add_sdcard_files(subpath, remote_path + "/" + file)
            else:
                print(subpath)
                file_remote = remote_path + "/" + file
                add_sdcard_file(subpath, file_remote)
    else:
        local_file_list = os.listdir(local_path)
        for file in local_file_list[file_begin - 1: file_end]:
            subpath = os.path.join(local_path, file)
            file_remote = remote_path + "/" + file
            add_sdcard_file(subpath, file_remote)

"""下面两个函数分别是强制删除指定路径文件和文件夹路径下所有文件，但是是强制删除，有待改善"""
def delete_sdcard_file(sdcard_file_path):
    subprocess.call([adb_path, "shell", "rm", "-rf", sdcard_file_path])

def delete_sdcard_dir(sdcard_dir_path, save_empty_dir:bool = False):
    if save_empty_dir == True:
        """删除sdcard_dir_path目录下的所有文件，但保留空目录本身"""
        if sdcard_dir_path[-1] == '/':
            sdcard_dir_path = sdcard_dir_path + "*"
        else:
            sdcard_dir_path = sdcard_dir_path + "/*"
        subprocess.call([adb_path, "shell", "rm", "-rf", sdcard_dir_path])
    else:
        """不仅删除目录下所有文件，同时删除目录本身"""
        subprocess.call([adb_path, "shell", "rm", "-rf", sdcard_dir_path])
def logcat_clean():
    subprocess.call([adb_path, "logcat", "-c"])
def get_dir_and_file(path):
    res_dict = {}
    dir, file = os.path.split(path)
    res_dict["dir_name"] = dir
    res_dict["file_name"] = file
    return res_dict

def generate_config(file_dir_path, dataset_path, model_path):
    dir_names = os.listdir(dataset_path)
    json_dict = {}
    json_dict["task_name"] = "classify"
    json_dict["dataset_path"] = file_dir_path + "/" + "dataset"
    json_dict["model_path"] = file_dir_path + "/" + get_dir_and_file(model_path).get("file_name")
    json_dict["classes"] = dir_names
    with open(r".\config.json","w") as f:
        json.dump(json_dict,f)

def start_main_activity(batch_size = 200, dataset_path = r"D:\CSTCloud\Freshman\SoftwareEngineering\Simplify\init\test_set", model_path=r"D:\CSTCloud\Freshman\SoftwareEngineering\Simplify\init\SqueezeNet.pt"):
    logcat_clean()
    # 获取应用文件夹
    file_dir_path = ""
    try:
        file_dir_path = get_file_dir_path()
    except FunctionTimedOut as e:
        print('function timeout + msg = ', e.msg)
        error_handle()

    generate_config(file_dir_path, dataset_path, model_path)
    activity_name = app_name + "MainActivity"
    start_cmd = subprocess.call([adb_path, "shell","am", "start", "-n", activity_name])
    add_sdcard_file(r".\config.json", file_dir_path + "/config.json")
    add_sdcard_file(model_path, file_dir_path + "/" + get_dir_and_file(model_path).get("file_name"))
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
    load_file_fail_pattern = re.compile(r"Error: ")
    line = load_file_listener.stdout.readline()
    # if(re.search(load_file_success_pattern, line) != None):
    #     pass
    # elif(re.search(load_file_fail_pattern, line) != None):
    #     error_handle()
    #     return
    # else:
    #     error_handle()
    #     return
    load_file_listener.kill()
    # 批处理
    batch_test_end_listener = subprocess.Popen([adb_path, "logcat","BatchTestEnd:I", "*:S"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
    batch_test_end_pattern = re.compile(r'I BatchTestEnd:')

    class_id = 0
    
    for root,dir_names,file_names in os.walk(dataset_path):
        class_num = len(dir_names)
        for dir in dir_names:
            batch_id = 0
            class_dir = os.path.join(dataset_path, dir)
            class_size = len(os.listdir(class_dir))
            while(True):
                line = batch_test_end_listener.stdout.readline()
                res = re.search(batch_test_end_pattern, line)
                if(res != None):
                    file_begin = batch_id*batch_size + 1
                    if(batch_id == int((class_size - 1) / batch_size)):
                        file_end = class_size
                        add_sdcard_files(class_dir, file_dir_path + "/dataset", False, file_begin, file_end)
                        subprocess.call([adb_path, "shell", "am", "startservice", "-n",  app_name + "BatchTestService", "--ei", "currentClass", str(class_id)])
                        break
                    else:
                        file_end = batch_id*batch_size + batch_size
                        add_sdcard_files(class_dir, file_dir_path + "/dataset", False, file_begin, file_end)
                        subprocess.call([adb_path, "shell", "am", "startservice", "-n",  app_name + "BatchTestService", "--ei", "currentClass", str(class_id)])
                    batch_id += 1
            class_id += 1
    subprocess.call([adb_path, "shell", "am", "startservice", "-n", app_name + "BatchTestService", "--ei", "currentClass", str(class_num)])
    subprocess.call([adb_path, "pull", file_dir_path + "/result.csv", r".\result.csv"])
    batch_test_end_listener.kill()
@func_set_timeout(10)
def get_file_dir_path():
    """从adb logcat中寻找对应的tag，但是如果长时间没找到直接退出（证明有问题）"""
    activity_name = app_name + "GenerateFileDirPathActivity"
    start_cmd = subprocess.Popen([adb_path, "shell","am", "start", "-n", activity_name])
    cmd = subprocess.Popen([adb_path, "logcat","AppDirPath:I", "*:S"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
    pattern = re.compile(r'I AppDirPath:')
    while True:
        line = cmd.stdout.readline()
        print(line)
        res = re.search(pattern, line)
        if(res != None):
            cmd.stdout.close()
            (begin, end) = res.span()
            file_dir_path = line[end + 1:-1]
            return file_dir_path

start_main_activity()

