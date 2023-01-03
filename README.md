# Simplify

## 部署流程
### 将安卓手机的开发者模式打开
在系统设置中找到“版本号”选项，连续点击5次版本号，返回系统设置，就可看到开发者选项被激活，接着打开开发者选项并启动USB调试，将手机通过USB数据线连接电脑，手机会弹出是否接受调试，选“是”
### Python环境配置
这里建议使用venv或者conda等工具新建虚拟python环境（当然系统环境也不是不行），推荐版本是python3.8。然后在项目文件夹下打开终端，输入以下命令安装所需要的依赖：
```shell
pip install -r requirements.txt
```
### 安卓环境配置
1. 如果需要开发移动端软件，建议使用Android Studio直接导入项目文件夹下的SimplifyMobie项目，并构建项目，安装在所连接的手机上
2. 如果不需要开发移动端软件，直接在手机上安装Simplify-debug软件


### 准备数据
需要准备图像分类数据集，图像数据的相对路径为：数据集-类别-具体图像，还需要分类模型的pt格式文件（如果使用的是其它框架导出的需要转成pt，现在项目只支持torch框架导出的模型）

### 运行项目
1. 运行c2runshow.py，显示如下界面：  
  ![image](https://user-images.githubusercontent.com/100749413/210332999-20805260-9245-475a-8d7c-a965ce6167be.png)  
2. 将准备好的数据集文件夹和模型文件拖拽到界面上，并点击Run按钮
3. 在程序运行的过程中，注意界面上是否弹出错误提示，并留意c2runshow.py程序控制台的输出
4. 在所有数据处理结束后，点击Plot会生成本次任务准确率、召回率和F1-Score及运行时间，并将混淆矩阵可视化展现在如下所示的界面上：
  ![image](https://user-images.githubusercontent.com/100749413/210336374-01506ed7-6954-41ec-8128-1a18f170e1e2.png)


如果存在问题，欢迎在issue下留言！
