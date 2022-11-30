package cn.edu.buaa.pestai2.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private static String showText = "请选择需要分类的农作物叶片图像";
    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue(showText);
    }

    public LiveData<String> getText() {
        return mText;
    }
}