import os
import subprocess
# adb工具路径
adb_path = "D:\CSTCloud\\freshman\SoftwareEngineering\Program\platform-tools\\adb.exe"
# 数据集路径
dataset_path = "D:\CSTCloud\\freshman\SoftwareEngineering\Program\dataset"
# 目的设备上的数据集路径
sdcard_path = "/sdcard/Simplify/tmpDataset"
for root, dirs, files in os.walk(dataset_path):
    for file in files:
        file_local = dataset_path +"\\" + file
        print(file_local)
        file_remote = sdcard_path + "/" + file
        subprocess.call([adb_path, "push", file_local, file_remote])
subprocess.call([adb_path, "shell", "rm", sdcard_path])
