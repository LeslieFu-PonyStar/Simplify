package cn.edu.buaa.pestai2.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cn.edu.buaa.pestai2.R;
import cn.edu.buaa.pestai2.config.MysqlConfig;

public class HomeFragment extends Fragment {

    private TextView textView;
    private LocationManager locationManager;
    private String provider;
    //只要收到线程模块发出的消息，执行handleMessage函数
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            String mText;
            switch (msg.what){
                case 1:
                    mText = msg.obj.toString();
                    textView.setText(mText);
                    break;
            }
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        textView = root.findViewById(R.id.text_home);
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // 申请权限
            ActivityCompat.requestPermissions(this.getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        locationManager = (LocationManager) this.getActivity().getSystemService(Context. LOCATION_SERVICE);
        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            // 当没有可用的位置提供器时，弹出Toast提示用户
        }
        Location location = locationManager.getLastKnownLocation(provider);
        textView.setText(getLocationAddress(location));
        locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
        //查询线程，这里必须新开一个线程，否则申请到的数据库连接是null
        //当线程的代码执行完的时候，线程会自动关闭，不需要我们手动关闭
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                StringBuffer mText = new StringBuffer();
//                Connection conn = MysqlConfig.getConnection();
//                String sql = "select * from test_connect";
//                try{
//                    PreparedStatement ps = conn.prepareStatement(sql);
//                    ResultSet rs = ps.executeQuery(sql);
//                    while(rs.next()){
//                        String title = rs.getString("connect_title");
//                        String user = rs.getString("connect_user");
//                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("submission_date"));
//                        mText.append(title)
//                                .append(" ")
//                                .append(user)
//                                .append(" ")
//                                .append(time)
//                                .append("\n\n");
//                    }
//                    MysqlConfig.closeAll(conn, ps, rs);
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = mText;
//                    handler.sendMessage(msg);
//                }catch (SQLException e){
//                    Log.e("SQL Exception", "Some error when use mysql database");
//                }
//                System.out.println(mText);
//            }
//        }).start();
        return root;
    }
    private String getLocationAddress(Location location) {
        String add = "";
        Geocoder geoCoder = new Geocoder(this.getActivity(), Locale.CHINESE);
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(),
                    1);
            System.out.println(addresses);
            Address address = addresses.get(0);
            Log.e("location", "getLocationAddress: " + address.toString());
            // Address[addressLines=[0:"中国",1:"北京市海淀区",2:"华奥饭店公司写字间中关村创业大街"]latitude=39.980973,hasLongitude=true,longitude=116.301712]
            int maxLine = address.getMaxAddressLineIndex();
            if (maxLine >= 2) {
                add = address.getAddressLine(1) + address.getAddressLine(2);
            } else {
                add = address.getAddressLine(1);
            }
        } catch (IOException e) {
            add = "";
            e.printStackTrace();
        }
        return add;
    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onLocationChanged(Location location) {
            // 更新当前设备的位置信息
            System.out.println(location);
        }
    };

}
