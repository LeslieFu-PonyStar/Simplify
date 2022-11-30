package cn.edu.buaa.pestai2;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.pytorch.Module;

import cn.edu.buaa.pestai2.ui.about.AboutFragment;
import cn.edu.buaa.pestai2.ui.dashboard.DashboardFragment;
import cn.edu.buaa.pestai2.ui.notifications.LogListFragment;

public class MainPage extends AppCompatActivity implements View.OnClickListener {

    private Fragment currentFragment=new Fragment();
    private RadioButton radioButtonHome, radioButtonDashboard, radioButtonLogList, radioButtonAbout;
    private Fragment fragmentHome, fragmentDashboard, fragmentLogList, fragmentAbout;
    private FragmentManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initView();
        initFragment();
        initEvent();
        showFragment(fragmentDashboard);
    }

    /**
     * 初始化监听
     */
    private void initEvent() {
//        radioButtonHome.setOnClickListener(this);
        radioButtonDashboard.setOnClickListener(this);
        radioButtonLogList.setOnClickListener(this);
        radioButtonAbout.setOnClickListener(this);
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        manager = getSupportFragmentManager();

        Bundle bundle = new Bundle();
//        bundle.putInt("tag", 1);
//        fragmentHome = new HomeFragment();
//        fragmentHome.setArguments(bundle);

        bundle = new Bundle();
        bundle.putInt("tag", 2);
        fragmentDashboard = new DashboardFragment();
        fragmentDashboard.setArguments(bundle);

        bundle = new Bundle();
        bundle.putInt("tag", 3);
        fragmentLogList = new LogListFragment();
        fragmentLogList.setArguments(bundle);

        bundle = new Bundle();
        bundle.putInt("tag", 4);
        fragmentAbout = new AboutFragment();
        fragmentAbout.setArguments(bundle);
    }

    /**
     * 展示Fragment
     */
    private void showFragment(Fragment fragment) {
        if (currentFragment!=fragment) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.hide(currentFragment);
            currentFragment = fragment;
            if (!fragment.isAdded()) {
                transaction.add(R.id.home_container, fragment).show(fragment).commit();
            } else {
                transaction.show(fragment).commit();
            }
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
//        radioButtonHome = (RadioButton) findViewById(R.id.radio_button_home);
        radioButtonDashboard = (RadioButton) findViewById(R.id.radio_button_dashboard);
        radioButtonLogList = (RadioButton) findViewById(R.id.radio_button_loglist);
        radioButtonAbout = (RadioButton) findViewById(R.id.radio_button_about);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.radio_button_home:
//                showFragment(fragmentHome);
//                break;
            case R.id.radio_button_dashboard:
                showFragment(fragmentDashboard);
                break;
            case R.id.radio_button_loglist:
                showFragment(fragmentLogList);
                break;
            case R.id.radio_button_about:
                showFragment(fragmentAbout);
                break;
        }
    }
}