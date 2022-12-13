from c4ui import Ui_MainWindow
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtWidgets import QMainWindow, QApplication, QFileDialog  # 导入qt窗体类

import matplotlib.pyplot as plt
from matplotlib.pyplot import savefig
import sys

from adb import *
from task.classify_task import ClassifyTask, Task
import requests

from cresult import result

class TaskThread(QtCore.QThread):
    end_signal = QtCore.pyqtSignal(str)
    def __init__(self, task:Task):
        super(TaskThread, self).__init__()
        self.task = task

    @QtCore.pyqtSlot()
    def run(self):
        self.task.start_main_activity()
        self.end_signal.emit("Complete!")

class main(Ui_MainWindow,QtWidgets.QMainWindow,result):


    def __init__(self):
        super().__init__()
        #super(Ui_MainWindow,self).__init__()
        self.setupUi(self)
        self.retranslateUi(self)
        self.current_task = None
        self.current_task_thread = TaskThread(self.current_task)

    def post1(self,filepath):
        try:
            url = 'http://127.0.0.1:5000'
            str000 = filepath
            newname = str000.split('/')
            print(newname[len(newname) - 1])
            newname1 = str(newname[len(newname) - 1])

            # 传单张图片
            # files = {'file':(newname1,open(r'C:\Users\komorip\Desktop\daliy\python\wrb\server\post\2.jpg','rb'),'image/jpg')}

            files = {'file': (
                newname1, open(filepath, 'rb'),
                'application/x-zip-compressed')}
            r = requests.post(url, files=files)
            result = newname1 + '\n' + r.text
            return result
        except:
            ####
            self.lineEdit.setText("1")
            error_dialog = QtWidgets.QErrorMessage()

            error_dialog.showMessage('''Oh no!错误位置：// post1 //函数 出现问题，请检查一下您的url设置或者也有可能出现其他问题!
            ┭┮﹏┭┮''')

            error_dialog.exec()

    def tplot(self):
        # 正确显示中文和负号
        try:
            conf_d=r".\result.csv"
            dataset_d=self.lineEdit_3.text()
            avp,avr,avf=self.detail(conf_d,dataset_d)
            self.label.setPixmap(QtGui.QPixmap(r".\ui\tpolt\1.jpg"))
            self.label.setScaledContents(True)
            self.label_2.setText('准确率：'+str(round(avp,2)))
            self.label_3.setText('召回率：'+str(round(avr,2)))
            self.label_4.setText('f1:'+str(round(avf,2)))
            self.label_5.setText('运行时间'+str(self.runtime)+'ms')
            return 1
        except:
            error_dialog = QtWidgets.QErrorMessage()
            error_dialog.showMessage('''Oh no!错误位置：// tplot //函数出现问题\n，请检查一下函数输入的数据对是否匹配，或者存在其他错误!
                                                                  ┭┮﹏┭┮''')
            error_dialog.exec()

            return 0


    def openfile(self):
        try:
            global filep
            # 打开文件的窗体，进行图片的选择
            openfile_name = QFileDialog.getOpenFileName()
            if openfile_name[0] != '':

            #格式测试
                print(openfile_name[0])

                filep = openfile_name[0]   # 获取选中的图片路径
                self.textEdit.setText(filep)
        except:
            pass
    #
    def saveToFile(self):
        path = QFileDialog.getSaveFileName(self, '请选择保存位置', './', "Files (*.{});;All Files (*)".format('txt'))
        if path[0]:
            self.tx.setText('选择的保存位置为：{}'.format(path[0]))

    def post(self):
        try:
            global filep
            #要调用才能选中文件地址
            self.drag_addrss()


            result=self.post1(filep)



            self.textEdit_2.setText(result)
        except:
           pass

    def readQss(style):
        try:
            with open(style, 'r') as f:
                return f.read()
        except:
            pass

    def drag_addrss(self):
        try:
             global filep
             filep=self.lineEdit.text()
             # test
             self.textEdit.setText(filep)
        except:
            pass


    def utplot(self,conf_d,dataset_d ):
        # 正确显示中文和负号
        try:
            x=self.tplot(conf_d,dataset_d)
            if x==1:
                self.label.setPixmap(QtGui.QPixmap(r".\ui\tplot\1.jpg"))
                self.label.setScaledContents(True)

        except:
            error_dialog = QtWidgets.QErrorMessage()
            error_dialog.showMessage('''Oh no!错误位置: // utplot //函数出现问题 ，\n请检查一下!
                    ┭┮﹏┭┮''')
    #按钮绑定

    def start_test(self):
        try:
        #     adb=self.lineEdit_2.text()
        #    cadb_path(adb)
            model=self.lineEdit_4.text()
            dataset=self.lineEdit_3.text()
            self.current_task = ClassifyTask(dataset, model, batch_size=200)
            self.current_task_thread = TaskThread(self.current_task)
            self.pushButton_4.setEnabled(False)
            self.current_task_thread.start()
            self.current_task_thread.end_signal.connect(self.end_test)

            
        except:
            error_dialog = QtWidgets.QErrorMessage()
            error_dialog.showMessage('''Oh no!错误位置: // start_test //函数出现问题 ，\n请检查一下!
                                ┭┮﹏┭┮''')
            error_dialog.exec()

    def end_test(self, str):
        if(str == "Complete!"):
            self.pushButton_4.setEnabled(True)

if __name__=="__main__":
    app = QApplication(sys.argv)

    styleFile = r".\ui\QSS-master\ElegantDark.qss"



    ui=main()

    style = main.readQss(styleFile)
    ui.setStyleSheet(style)

    ui.show()

    #ui.pushButton.clicked.connect(ui.openfile)
    #ui.pushButton_2.clicked.connect(ui.post)

    # test

   # a = ["wo", "oo", "2h", "3", "4", "8"]
   # b = [20, 70, 40, 62, 45, 70]
    #a=r".\result.csv"
    #b=ui.lineEdit_3.text()
    #ui.pushButton_3.clicked.connect(lambda: ui.tplot(a, b))
    sys.exit(app.exec_())



