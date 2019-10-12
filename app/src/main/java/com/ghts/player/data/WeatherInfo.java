package com.ghts.player.data;

import android.content.Context;
import android.util.Xml;

import com.ghts.player.bean.AlarmBean;
import com.ghts.player.bean.WeatherBean;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.VeDate;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lijingjing on 17-9-8.
 */
public class WeatherInfo implements Runnable {

    private static String TAG = "WeatherInfo";
    public static boolean isStop = false;
    private String ip;
    private int port_server;
    private Context mContext;
    private ByteArrayInputStream baInPacketHeader;
    private DatagramPacket pkt_in;
    private MulticastSocket socket;

    private Thread myThread;
    private long weather_countdown = Const.WeatherOffLine;
    private long zone_countdown = Const.WarningOffLine;
    private boolean runFlag = true;

    public WeatherInfo(Context context, String ip, int serverPort) {
        mContext = context;
        this.ip = ip;
        port_server = serverPort;

        myThread = new Thread(myRunnable);
        myThread.start();
    }

    public void run() {
        try {
//            Thread.sleep(1000*10);
            byte[] buf_in = new byte[10000];
            InetAddress address = InetAddress.getByName(ip);
            socket = new MulticastSocket(port_server);
            socket.joinGroup(address);
            pkt_in = new DatagramPacket(buf_in, buf_in.length);
            while (isStop) {
                socket.receive(pkt_in);
                int recvLength = pkt_in.getLength();
                byte[] buf_recv = pkt_in.getData();
                assert (buf_recv.length == recvLength);
                baInPacketHeader = new ByteArrayInputStream(buf_recv, 0, recvLength);
                DataInputStream dInHeader = new DataInputStream(baInPacketHeader);
                byte[] xml = new byte[recvLength];
                dInHeader.read(xml, 0, recvLength);
                InputStream inputStream = new ByteArrayInputStream(xml);
                String str = new String(xml);
//                LogUtil.e("########", str);
                if (str.contains("weather") && str.contains(Const.globalBean.getStationCode())) {
                    WeatherBean weatherBean = parseWeather(inputStream);
                    if (weatherBean != null) {
                        Date cur = VeDate.strTo2Date(weatherBean.getUpTime());
                        Date now = VeDate.getNowDate();
                        LogUtil.e("######时间",cur.getTime()+"=="+now.getTime()+"=="+(now.getTime()-cur.getTime()));
                         if (cur.getTime() > now.getTime() || (cur.getTime()<now.getTime()&&(now.getTime()-cur.getTime()< Const.WeatherOffLine) )) {
                             Const.weatherBean = new WeatherBean(weatherBean.getStation(), weatherBean.getUpTime(), weatherBean.getTemperature(), weatherBean.getHumidity(), weatherBean.getWind(), weatherBean.getIcon());
                            weather_countdown = Const.WeatherOffLine;
                        } else {
                            Const.weatherBean = null;
                        }
                    }
                } else if (str.contains("alarm")) {
                    AlarmBean alarmBean = parseAlarm(inputStream);
                    if (alarmBean != null) {
                        Date cur = VeDate.strTo2Date(alarmBean.getUpDate());
                        Date now = VeDate.getNowDate();
                        LogUtil.e("######时间",cur.getTime()+"=="+now.getTime()+"=="+(now.getTime()-cur.getTime()));
                        if (cur.getTime() > now.getTime() || (cur.getTime()<now.getTime()&&(now.getTime()-cur.getTime()< Const.WarningOffLine) )) {
                            Const.alarmBean = new AlarmBean(alarmBean.getUpDate(), alarmBean.getNum(), alarmBean.getZones());
                            zone_countdown = Const.WarningOffLine;
                        } else {
                            Const.alarmBean = null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WeatherBean parseWeather(InputStream inputStream) {
        WeatherBean weatherBean = null;
        try {
            XmlPullParser parse = Xml.newPullParser();
            parse.setInput(inputStream, "GBK");
            int type = parse.getEventType();
            boolean isId = false;
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        //获取开始标签的名字
                        String startname = parse.getName();
                        if ("weather".equals(startname)) {
                            weatherBean = new WeatherBean();
                            weatherBean.setStation(parse.getAttributeValue("", "station"));
                        } else if ("time".equals(startname)) {
                            String time = parse.nextText();
                            weatherBean.setUpTime(time);
                        } else if ("temperature".equals(startname)) {
                            weatherBean.setTemperature(parse.nextText());
                        } else if ("humidity".equals(startname)) {
                            weatherBean.setHumidity(parse.nextText());
                        } else if ("wind".equals(startname)) {
                            weatherBean.setWind(parse.nextText());
                        } else if ("icon".equals(startname)) {
                            String path = parse.nextText();
                            weatherBean.setIcon("/sata/img/shorttimeicon/" + path + ".png");
                        }
                        break;
                    case XmlPullParser.END_TAG://如果是结束标签
                        break;
                }
                type = parse.next();
            }
        } catch (XmlPullParserException e) {
            LogUtil.e("天气信息解析失败1", e.toString());
        } catch (IOException e) {
            LogUtil.e("天气信息解析失败2", e.toString());
        }
        LogUtil.e("######weather", weatherBean.toString());
        return weatherBean;
    }

    private AlarmBean parseAlarm(InputStream inputStream) {
        AlarmBean alarmBean = null;
        WeatherBean bean = null;
        ArrayList<WeatherBean> zones = null;
        try {
            XmlPullParser parse = Xml.newPullParser();
            parse.setInput(inputStream, "GBK");
            int type = parse.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        //获取开始标签的名字
                        String startname = parse.getName();
                        if ("alarm".equals(startname)) {
                            alarmBean = new AlarmBean();
                            alarmBean.setNum(PubUtil.parseInt(parse.getAttributeValue("", "num")));
                        } else if ("time".equals(startname)) {
                            String time = parse.nextText();
                            alarmBean.setUpDate(time);
                            zones = new ArrayList<WeatherBean>();
                            zones.clear();
                        } else if (startname.startsWith("district")) {
                            bean = new WeatherBean();
                            bean.setDistrict(parse.nextText());
                        } else if (startname.startsWith("icon")) {
                            String path = parse.nextText();
                            bean.setIcon("/sata/img/alarmicon/" + path + ".gif");
                            zones.add(bean);
                        }
                        break;
                    case XmlPullParser.END_TAG://如果是结束标签
                        if (parse.getName().equals("alarm")) {
                            alarmBean.setZones(zones);
                        }
                        break;
                }
                type = parse.next();
            }
        } catch (XmlPullParserException e) {
            LogUtil.e("天气信息解析失败3", e.toString());
        } catch (IOException e) {
            LogUtil.e("天气信息解析失败4", e.toString());
        }
        LogUtil.e("######alarm", alarmBean.toString());
        return alarmBean;
    }

    //倒计时的线程
    private Runnable myRunnable = new Runnable() {
        public void run() {
            while (runFlag) {
                if (weather_countdown > 0) {
                    // 倒计时时间减一秒。
                    weather_countdown -= 1000;
                    if (weather_countdown <= 0) { // 倒计时为零后重新计时
                        weather_countdown = Const.WeatherOffLine ;
                        Const.weatherBean = null;
                    }
                }
                if (zone_countdown > 0) {
                    // 倒计时时间减一秒。
                    zone_countdown -= 1000;
                    if (zone_countdown <= 0) { // 倒计时为零后重新计时
                        zone_countdown = Const.WarningOffLine ;
                        Const.alarmBean = null;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LogUtil.i(TAG, "InterruptedException...\r\n" + e);
                }
            }
        }
    };

}
