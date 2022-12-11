import os
import subprocess
# adb工具路径
adb_path = r"C:\Users\komorip\Desktop\daliy\python\adb\platform-tools\adb.exe"

def add_sdcard_file(local_path, remote_path):
    """添加单个文件"""
    subprocess.call([adb_path, "push", local_path, remote_path])

def add_sdcard_files(local_path, remote_path):
    """递归调用该函数，实现文件夹整体的复制"""
    for root, dirs, files in os.walk(local_path):
        if len(dirs) != 0:
            for dir in dirs:
                add_sdcard_files(local_path + "\\" + dir, remote_path + "/" + dir)
        for file in files:
            file_local = local_path +"\\" + file
            print(file_local)
            file_remote = remote_path + "/" + file
            add_sdcard_file(file_local, file_remote)

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


# test
dataset_path = r"C:\Users\komorip\Desktop\daliy\python\wrb\server\post"
sdcard_path = r"/storage/emulated/0/adbtest"
add_sdcard_files(dataset_path, sdcard_path)

#delete_sdcard_dir("/storage/emulated/0/Android/data/cn.ponystar.simplifymobile/files/test", save_empty_dir=False)