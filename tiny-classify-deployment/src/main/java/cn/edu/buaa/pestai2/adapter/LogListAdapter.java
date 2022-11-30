package cn.edu.buaa.pestai2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.edu.buaa.pestai2.R;
import cn.edu.buaa.pestai2.entity.Logger;


public class LogListAdapter extends ArrayAdapter<Logger> {
    // 创建ListView子组件的对象，将子组件的前端组件和数据绑定
    class ViewHolder{
        ImageView mTrainPicture;
        TextView mOutputClass;
        TextView mTrainDate;
    }
    private Context context;
    private ArrayList<Logger> trainLogs;
    private int resourceId;
    public LogListAdapter(Context context,int viewResourceId, ArrayList<Logger> trainLogs) {
        super(context, viewResourceId, trainLogs);
        this.context = context;
        this.trainLogs = trainLogs;
        this.resourceId = viewResourceId;
    }

    @Override
    public int getCount() {
        return trainLogs.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder = new ViewHolder();
        final Logger logger = trainLogs.get(i);
        if(convertView==null){
            holder=new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(//convertView为空代表布局没有被加载过，即getView方法没有被调用过，需要创建
                    resourceId, null);     // 得到子布局，非固定的，和子布局id有关
            holder.mTrainPicture = view.findViewById(R.id.log_image);//获取控件,只需要调用一遍，调用过后保存在ViewHolder中
            holder.mOutputClass = view.findViewById(R.id.output_name);
            holder.mTrainDate = view.findViewById(R.id.log_date);  //获取控件
            view.setTag(holder);
        }else{
            view=convertView;      //convertView不为空代表布局被加载过，只需要将convertView的值取出即可
            holder=(ViewHolder) view.getTag();
        }
        holder.mTrainPicture.setImageBitmap(logger.getImage());
        holder.mOutputClass.setText(logger.getOutputClass());
        holder.mTrainDate.setText(logger.getTrainDate());
        return view;
    }
}
