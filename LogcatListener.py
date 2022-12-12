import subprocess
import re
import adb
from adb import *
adb_path = r'./adb/adb.exe'
class LogcatListener():
    """监听LogCat的监听器，此类还没完善完"""
    def __init__(self, tag:str, tag_type:str, adb_path:str):
        listen_content = ":".join((tag, tag_type))
        self.listener = subprocess.Popen([adb_path, "logcat", listen_content, "*:S"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, universal_newlines=True)

    def run(self):
        """必须重写此方法，否则报错"""
        raise NotImplemented
    
    def close(self):
        """关闭监听"""
        self.listener.kill()

class ConfigFoundListener(LogcatListener):
    """检查配置文件是否正确传输到移动设备"""
    def __init__(self, adb_path:str):
        LogcatListener.__init__(self, "Config", "I", adb_path)

    def run(self):
        config_not_found_pattern = re.compile(r'Config file is not found.')
        config_found_pattern = re.compile(r'Config file is found.')
        while(True):
            line = self.listener.stdout.readline()
            res_config_not_found = re.search(config_not_found_pattern, line)
            if(res_config_not_found != None):
                adb.add_sdcard_file(r".\config.json", file_dir_path + "/config.json")
                continue
            res_config_found = re.search(config_found_pattern, line)
            if(res_config_found != None):
                break