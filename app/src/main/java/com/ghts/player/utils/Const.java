package com.ghts.player.utils;

import com.ghts.player.bean.AlarmBean;
import com.ghts.player.bean.GlobalBean;
import com.ghts.player.bean.WeatherBean;
import com.ghts.player.data.Stationxml;
import com.ghts.player.parse.ParseXml;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class Const {

    public static int screenW = 0;
    public static int screenH = 0;
    public static double ScaleX = 0;
    public static double ScaleY = 0;

    public static ParseXml parseXml;
    public static final String PLAYDATEPATH = "/sata/config/playlist.xml";
    //video存储目录
    public static final String VIDEOPATH = "/sata/media/";
    //GLOBAL文件目录
    public static final String GLOBALPATH = "/sata/config/globalparam.xml";
    //自动升级
    public static final String UPGRADE = "/sata/config/upgrade.xml";
    //自动开关机操作
    public static final String TASK_LIST = "/sata/config/task_list.xml";
    //目的地码
    public static final String STATIONPATH = "/sata/config/station.xml";
    //解析字体
    public static final String FONTPATH = "/sata/config/fonts.xml";

    //天气预报组播地址存放目录
    public static final String WEATHERPATH = "/sata/config/weather.xml";
    //直播
    public static final String ISLIVE = "islive";

    public static String TASKID="id";
    //滚动文本,日期,星期,时间
    public static String scrollingtime = "";

    public static String scrollingtext = "craw";
    /**
     * 存放模块数据
     */
    public static HashMap<String, String> moduleMap;

    //数据库中的紧急消息
    public static final String EMERG_PATH = "/sata/config/Emerg.txt";


    //根据城市名称判断不同功能
//    public static String versionName = "chengdu";

    /**
     *南京：mxc1:232
     *     mxc2: 485/422
     *成都： mxc2:232
     *上海：mxc4:422,38400
     */
    public static String PORT = "ttymxc2";
    public static int BAUDRATE = 38400;
    /**
     *开屏类型 01代表哈尔滨,02代表多个串联
     */
    public static String serial_device_type = "01";

    //配置文件
    public static GlobalBean globalBean = null;

    /**
     * 播表名称
     */
    public static String currentPlay = "test";
    /**
     * 版式名称
     */
    public static String currentLayout = "test";

    public static ArrayList<Stationxml> stationxmlList = null;

    //命令服务器设定的监控查询间隔，单位秒
    public static byte buCheckInterval = 15;

    public static long COUNTDOWNTIME = 1000*60*10;
    /**
     * 是否显示紧急消息** 0不显示，1显示
     */
    public static String ISEXIGENT = "0";
    /**
     * 天气预报模块有效时间:分钟
     */
    public static int WeatherOffLine = 50*60*1000;
    public static int WarningOffLine = 20*60*1000;
    /**
     * 天气预报模块设置全局变量
     */
    public static WeatherBean weatherBean;
    public static AlarmBean alarmBean;
    /**
     * 天气预报组播地址
     */
    public static String WeatherIp="";
    public static int WeatherPort=0;

    /**
     * 记录播表所在的路径
     ***/
    public static String PLAYPATH = "playpath";

    /**
     * 0=不支持,1=静音,2=未静音
     */
    public static final String AudioStatus = "AudioStatus";
    /**
     * 当前音量值
     */
    public static String AudioVolume = "AudioVolume";

    /**
     * 是否开启静音状态
     **/
    public static final String GlobalAudioStatus = "GlobalAudioStatus";
    /**
     * 是否设置过音量
     **/
//    public static final String GlobalAudioVolume = "GlobalAudioVolume";

    /**
     * 是否设置过音量
     **/
    public static  boolean GlobalAudioVolume = false;

    /**
     * 是否关机（关屏）
     **/
    public static String IsPowerOff = "IsPowerOff";

    /**
     * 是否关机（关屏）
     **/
    public static String IsScreenOff = "IsScreenOff";

    public static int atsId = 0;


    public static ParseXml getParseXml() {
        return parseXml;
    }

    public static void setParseXml(ParseXml parseXml) {
        Const.parseXml = parseXml;
    }


    public static byte getBuCheckInterval() {
        return buCheckInterval;
    }

    public static void setBuCheckInterval(byte buCheckInterval) {
        Const.buCheckInterval = buCheckInterval;
    }

    public static String getAudioVolume() {
        return AudioVolume;
    }

    public static void setAudioVolume(String audioVolume) {
        AudioVolume = audioVolume;
    }

    public static int getWeatherOffLine() {
        return WeatherOffLine;
    }

    public static void setWeatherOffLine(int weatherOffLine) {
        WeatherOffLine = weatherOffLine;
    }

    public static int getWarningOffLine() {
        return WarningOffLine;
    }

    public static void setWarningOffLine(int warningOffLine) {
        WarningOffLine = warningOffLine;
    }

    public static WeatherBean getWeatherBean() {
        return weatherBean;
    }

    public static void setWeatherBean(WeatherBean weatherBean) {
        Const.weatherBean = weatherBean;
    }

    public static AlarmBean getAlarmBean() {
        return alarmBean;
    }

    public static void setAlarmBean(AlarmBean alarmBean) {
        Const.alarmBean = alarmBean;
    }

    public static String getWeatherIp() {
        return WeatherIp;
    }

    public static void setWeatherIp(String weatherIp) {
        WeatherIp = weatherIp;
    }

    public static int getWeatherPort() {
        return WeatherPort;
    }

    public static void setWeatherPort(int weatherPort) {
        WeatherPort = weatherPort;
    }

        public static String getHostIP() {
        String url = "127.0.0.1";
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        url = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return url;
    }

}

