package me.hupeng.app.wechat.conf;

public class Configuration {
    private static String HOST = "123.206.21.155";
//    private static String HOST = "10.25.44.122";
    private static String PORT = "8080";
    private static String APP = "WechatWeb/v1";

    public static String BASE_HTTP_URL = "http://" + HOST + ":" + PORT +  (APP.equals("")?"/":"/" + APP + "/");
    public static String BASE_WS_URL = "ws://" + HOST + ":" + PORT + (APP.equals("")?"/":"/" + APP + "/") ;
}

