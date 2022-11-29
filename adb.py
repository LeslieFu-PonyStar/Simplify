import os
import subprocess
# adb工具路径
adb_path = "D:\CSTCloud\\freshman\SoftwareEngineering\Program\platform-tools\\adb.exe"

def add_sdcard_files(dataset_path, sdcard_path):
    """将电脑上的某文件夹下的所有文件传输至手机上的可读写特定路径"""
    for root, dirs, files in os.walk(dataset_path):
        for file in files:
            file_local = dataset_path +"\\" + file
            print(file_local)
            file_remote = sdcard_path + "/" + file
            subprocess.call([adb_path, "push", file_local, file_remote])

def delete_sdcard_files(sdcard_path):
    """删除sdcard_path目录下的所有文件，但不删除目录本身"""
    if sdcard_path[-1] == '/':
        sdcard_path = sdcard_path + "*"
    else:
        sdcard_path = sdcard_path + "/*"
    subprocess.call([adb_path, "shell", "rm", "-rf", sdcard_path])
# test
dataset_path = "D:\CSTCloud\\freshman\SoftwareEngineering\Program\dataset"
sdcard_path = "/sdcard/Simplify/tmpDataset"
add_sdcard_files(dataset_path, sdcard_path)
delete_sdcard_files(sdcard_path)
