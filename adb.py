import os
import subprocess
import re
from func_timeout import func_set_timeout, FunctionTimedOut
# adb工具路径
adb_path = r'D:\CSTCloud\Freshman\SoftwareEngineering\Simplify\Simplify\adb\adb.exe'
app_name = 'cn.ponystar.simplifymobile/cn.ponystar.simplifymobile.'
def add_sdcard_file(local_path, remote_path):
    """添加单个文件"""
    subprocess.call([adb_path, "push", local_path, remote_path])

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

def startActivity(activity_name):
    activity_name = app_name + activity_name
    cmd = subprocess.Popen([adb_path, "shell","am", "start", "-n", activity_name], stderr = subprocess.PIPE)
    error = cmd.stderr.readlines()
    if(error != None):
        print(error)

@func_set_timeout(10)
def get_file_dir_path():
    """从adb logcat中寻找对应的tag，但是如果长时间没找到直接退出（证明有问题）"""
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

def listen_to_main_activity():
    batch_id = 0
    cmd = subprocess.Popen([adb_path, "logcat","BatchTestEnd:I", "*:S"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)
    pattern = re.compile(r'I BatchTestEnd:')
    subprocess.call([adb_path, "shell", "am", "startservice", "-n", "cn.ponystar.simplifymobile/cn.ponystar.simplifymobile.BatchTestService", "--ei", "currentClass", "0"])
    while True:
        if(batch_id == 12/3):
            subprocess.call([adb_path, "shell", "am", "startservice", "-n", "cn.ponystar.simplifymobile/cn.ponystar.simplifymobile.BatchTestService", "--ei", "currentClass", "16"])
            break      
        line = cmd.stdout.readline()
        res = re.search(pattern, line)
        if(res != None):
            batch_id += 1
            add_sdcard_files(r"D:\CSTCloud\Freshman\SoftwareEngineering\Simplify\init\dataset", file_dir_path + "/dataset", False, batch_id*3 + 1, batch_id*3 + 3)
            subprocess.call([adb_path, "shell", "am", "startservice", "-n",  "cn.ponystar.simplifymobile/cn.ponystar.simplifymobile.BatchTestService", "--ei", "currentClass", "0"])
# test
# dataset_path = r"D:\CSTCloud\freshman\SoftwareEngineering\Program\dataset"
# sdcard_path = "/storage/emulated/0/Android/data/cn.ponystar.simplifymobile/files/tmpDataset"
# add_sdcard_files(dataset_path, sdcard_path)
# delete_sdcard_dir("/storage/emulated/0/Android/data/cn.ponystar.simplifymobile/files/test", save_empty_dir=False)
file_dir_path = ""

startActivity("MainActivity")
try:
    file_dir_path = get_file_dir_path()
except FunctionTimedOut as e:
    print('function timeout + msg = ', e.msg)
#add_sdcard_files(r"D:\CSTCloud\Freshman\SoftwareEngineering\Simplify\init", file_dir_path)
add_sdcard_file(r"D:\CSTCloud\Freshman\SoftwareEngineering\Simplify\init\config.json", file_dir_path + "/config.json")
add_sdcard_file(r"D:\CSTCloud\Freshman\SoftwareEngineering\Simplify\init\SqueezeNet.pt", file_dir_path + "/SqueezeNet.pt")
add_sdcard_files(r"D:\CSTCloud\Freshman\SoftwareEngineering\Simplify\init\dataset", file_dir_path + "/dataset", False, 1, 3)
listen_to_main_activity()

