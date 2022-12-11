import matplotlib.pyplot as plt
import numpy as np
import itertools
import pandas as pd
from datetime import datetime
import os,shutil

class result():
    def readfile(self,x):
        f1 = pd.read_csv(x,header = None)
        f1 = np.array(f1)
        self.runtime=f1[-1][0]
        f1=f1[:-1]
        f1=f1.astype(int)
        self.row=f1.shape[0]
        return f1

    def plot_confusion_matrix(self,cm, classes, normalize=False, title='Confusion matrix', cmap=plt.cm.Blues):
        """
        混淆矩阵的可视化: 传入混淆矩阵和类别名（或数字代替）
        :param cm: 混淆矩阵
        :param classes: 类别
        :param normalize:
        :param title:
        :param cmap:
        :return:
        """
        if normalize:
            cm = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]
            print("Normalized confusion matrix")
        else:
            print('Confusion matrix, without normalization')

        plt.imshow(cm, interpolation='nearest', cmap=cmap)
        plt.title(title)
        plt.colorbar()
        tick_marks = np.arange(len(classes))
        plt.xticks(tick_marks, classes, rotation=45)
        plt.yticks(tick_marks, classes)

        fmt = '.2f' if normalize else 'd'
        thresh = cm.max() / 2
        """
        for i, j in itertools.product(range(cm.shape[0]), range(cm.shape[1])):
            plt.text(j, i, format(cm[i, j], fmt), horizontalalignment="center",
                     color="white" if cm[i, j] > thresh else "black")
 
"""
        plt.tight_layout()
        plt.ylabel('True label')
        plt.xlabel('Predicted label')
        #
        plt.savefig(r".\ui\tpolt\1.jpg")
        #plt.show()

    def getLabelData(self,file_dir):

        file_path = file_dir
        file_name = os.listdir(file_path)
        return file_name





    def calculate_all_prediction(confMatrix):
        '''
        计算总精度：对角线上所有值除以总数
        '''
        total_sum = confMatrix.sum()
        correct_sum = (np.diag(confMatrix)).sum()
        prediction = round(100 * float(correct_sum) / float(total_sum), 2)
        return prediction


    def calculate_label_prediction(self,confMatrix, labelidx):
        '''
        计算某一个类标预测精度：该类被预测正确的数除以该类的总数
        '''
        label_total_sum = confMatrix.sum(axis=0)[labelidx]
        label_correct_sum = confMatrix[labelidx][labelidx]
        prediction = 0
        if label_total_sum != 0:
            prediction = round(100 * float(label_correct_sum) / float(label_total_sum), 2)
        return prediction


    def calculate_label_recall(self,confMatrix, labelidx):
        '''
        计算某一个类标的召回率：
        '''
        label_total_sum = confMatrix.sum(axis=1)[labelidx]
        label_correct_sum = confMatrix[labelidx][labelidx]
        recall = 0
        if label_total_sum != 0:
            recall = round(100 * float(label_correct_sum) / float(label_total_sum), 2)
        return recall


    def calculate_f1(self,prediction, recall):
        if (prediction + recall) == 0:
            return 0
        return round(2 * prediction * recall / (prediction + recall), 2)

    def detail(self,conf_a,f_a):
        conf = self.readfile(conf_a)
        resl=np.array([['序号','类别名','准确率','召回率','F1']])
        f_a=self.getLabelData(f_a)
        sumpre=0
        sumrecall=0
        for i in range(conf.shape[0]):
            pre=self.calculate_label_prediction(conf,i)
            recall=self.calculate_label_recall(conf,i)
            sumpre+=pre
            sumrecall+=recall
            f1=self.calculate_f1(pre, recall)
            tem=[i+1,f_a[i],pre,recall,f1]
            resl = np.row_stack((resl, tem))


        cla = [i + 1 for i in range(self.row)]
        self.plot_confusion_matrix(conf, cla)
        avp=sumpre/conf.shape[0]
        avr=sumrecall/conf.shape[0]
        af1=self.calculate_f1(avp, avr)
        tem=[conf.shape[0]+1,'模型总体',avp,avr,af1]
        resl = np.row_stack((resl, tem))
        rname,name,pname=self.testname()
        self.copy(r".\result.csv",rname)
        self.copy(r".\ui\tpolt\1.jpg",pname)
        np.savetxt(name, resl, delimiter=', ',fmt='%s')

        return avp,avr,af1
    def testname(self):
        now = datetime.now()  # 获得当前时间
        timestr = now.strftime("%Y_%m_%d_%H_%M_%S")
        #print('年_月_日_时_分_秒：', timestr)
        rdir='.\\test_result\\'+ 'original'+timestr+'.csv'
        dir = '.\\test_result\\'+ timestr+'.csv'
        pdir = '.\\test_result\\' + 'pic' + timestr + '.jpg'
        return rdir,dir,pdir
    def copy(self,f_d,d_d):
        shutil.copy(f_d, d_d)


if __name__=="__main__":
    case=result()

    #labe=case.getLabelData(r"D:\Simplify-main\dataset\dataset")
    #print(labe)
    #dir=case.testname()
    #print(dir)


    #f1=case.readfile(r".\result.csv")
    #case.copy(r".\result.csv",r".\test_result\22.csv")
    avp,avr,avf=case.detail(r".\result.csv",r"D:\Simplify-main\dataset\dataset")
"""
    print(re)
    print('\n')
    print(avp)
    print('\n')
    print(avr)
    print('\n')
    print(avf)

    #cla=[i+1 for i in range(case.row)]
    #case.plot_confusion_matrix(f1,cla)
"""

"""

    #数据已分开
    print(f1)
    print("\n")
    print(case.runtime)
    """