package cn.edu.buaa.pestai2.ui.home;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import cn.edu.buaa.pestai2.config.MysqlConfig;

public class QueryTest {
    protected static String getTestContent(){
        String mText = "";
        Connection conn = MysqlConfig.getConnection();
        String sql = "select * from test_connect";

        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                String title = rs.getString("connect_title");
                String user = rs.getString("connect_user");
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("submission_date"));
                mText = mText + title + " "
                        + user + " "
                        + time + "\n";
            }
        }catch (SQLException e){
            Log.e("SQL Exception", "Some error when use mysql database");
        }
        return mText;
    }


}
