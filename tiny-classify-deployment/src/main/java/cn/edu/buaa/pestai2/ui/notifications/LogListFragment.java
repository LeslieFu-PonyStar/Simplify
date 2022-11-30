package cn.edu.buaa.pestai2.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;

import cn.edu.buaa.pestai2.R;
import cn.edu.buaa.pestai2.adapter.LogListAdapter;
import cn.edu.buaa.pestai2.db.LogListDao;
import cn.edu.buaa.pestai2.entity.Logger;


public class LogListFragment extends Fragment {
    private NotificationsViewModel notificationsViewModel;
    private ListView logListView;
    private TextView noLogTextView;
    private ArrayList<Logger> loggers = new ArrayList<>();
    private LogListDao logListDao;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_loglist, container, false);
        logListView = root.findViewById(R.id.list_view);
        noLogTextView = root.findViewById(R.id.no_log);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        logListDao = new LogListDao(this.getActivity());
        loggers = logListDao.getLogList();
        //检查日志记录的长度是否为0，为0则显示没有日志记录，否则隐藏该文本控件
        if(loggers.size() == 0){
            noLogTextView.setText("暂无训练记录");
            noLogTextView.setVisibility(View.VISIBLE);
        }
        else{
            noLogTextView.setVisibility(View.INVISIBLE);
        }
        LogListAdapter logListAdapter = new LogListAdapter(this.getActivity(), R.layout.log_item, loggers);
        logListView.setAdapter(logListAdapter);
    }
}
