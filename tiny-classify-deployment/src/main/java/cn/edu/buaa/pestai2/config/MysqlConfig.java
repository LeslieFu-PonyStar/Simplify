package cn.edu.buaa.pestai2.config;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlConfig {
    private static String diver = "com.mysql.jdbc.Driver";
    //private static String url = "jdbc:mysql://" + ServerConfig.getMysqlPort() +"/pestai?characterEncoding=utf-8&serverTimezone=UTC";
    private static String url = "jdbc:mysql://" + ServerConfig.getMysqlPort() + "/pestai?characterEncoding=utf-8&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "admin";

    //获取数据库连接
    public static Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName(diver);
            conn = DriverManager.getConnection(url,user,password);//获取连接
            if(conn!=null)
                System.out.println("test success");
            else
                System.out.println("fail");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
    //关闭数据库连接
    public static void closeAll(Connection conn, PreparedStatement ps){
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //重载方法，关闭数据库，针对查询操作
    public static void closeAll(Connection conn, PreparedStatement ps, ResultSet rs){
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
