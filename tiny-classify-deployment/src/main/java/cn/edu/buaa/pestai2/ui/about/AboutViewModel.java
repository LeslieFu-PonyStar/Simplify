package cn.edu.buaa.pestai2.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cn.edu.buaa.pestai2.BuildConfig;
import cn.edu.buaa.pestai2.config.ServerConfig;

public class AboutViewModel extends ViewModel{
    private MutableLiveData<String> mText;
    public AboutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(
                "软件名称： 农作物分类app(毕业设计)\n" +
                "版本号： " + BuildConfig.VERSION_NAME + "\n" +
                "开发者： LeslieFu\n" +
                "服务器IP：" + ServerConfig.ipAddress
        );
    }

    public LiveData<String> getText() {
        return mText;
    }
}
