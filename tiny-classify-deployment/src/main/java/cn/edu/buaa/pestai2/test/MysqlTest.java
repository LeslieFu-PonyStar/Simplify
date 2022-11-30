package cn.edu.buaa.pestai2.test;

import java.sql.Connection;

import cn.edu.buaa.pestai2.config.MysqlConfig;

public class MysqlTest {
    public static void main(String[] args) {
        Connection conn = MysqlConfig.getConnection();
        if(conn!=null)
            System.out.println("test success");
        else
            System.out.println("fail");
    }
}
