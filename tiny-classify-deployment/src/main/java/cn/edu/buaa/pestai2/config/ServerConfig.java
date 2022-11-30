package cn.edu.buaa.pestai2.config;

public final class ServerConfig {
    //服务器的配置信息
    public static final String ipAddress = "10.0.2.2";
    private static String mysqlPort = "3306";

    public static String getMysqlPort(){
        return ipAddress + ":" + mysqlPort;
    }
}
