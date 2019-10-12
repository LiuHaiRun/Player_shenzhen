package com.ghts.player.parse;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.Xml;
import com.ghts.player.bean.ChannelBean;
import com.ghts.player.bean.DayList;
import com.ghts.player.bean.EmergBean;
import com.ghts.player.bean.FontXml;
import com.ghts.player.bean.Frame;
import com.ghts.player.bean.GlobalBean;
import com.ghts.player.bean.LayoutBean;
import com.ghts.player.bean.LocalPlayItem;
import com.ghts.player.bean.MsgBean;
import com.ghts.player.bean.MsgModel;
import com.ghts.player.bean.PlayDateBean;
import com.ghts.player.bean.PlayDayBean;
import com.ghts.player.bean.PlayList;
import com.ghts.player.bean.PlayerBean;
import com.ghts.player.bean.StationViewBean;
import com.ghts.player.bean.TaskBean;
import com.ghts.player.bean.TrainInfoBean;
import com.ghts.player.bean.UpgradeBean;
import com.ghts.player.data.Stationxml;
import com.ghts.player.data.SystemBean;
import com.ghts.player.data.TrainData;
import com.ghts.player.data.TrainInfoData;
import com.ghts.player.data.TrainInfoItem;
import com.ghts.player.data.TrainItem;
import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.BGParam;
import com.ghts.player.enumType.BackType;
import com.ghts.player.enumType.DateParam;
import com.ghts.player.enumType.DateShow;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.GradientColorType;
import com.ghts.player.enumType.ImgChange;
import com.ghts.player.enumType.MDShow;
import com.ghts.player.enumType.POS;
import com.ghts.player.enumType.RGBA;
import com.ghts.player.enumType.ShowFontType;
import com.ghts.player.enumType.ShowLang;
import com.ghts.player.enumType.ShowType;
import com.ghts.player.enumType.YearShow;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.video.LocalVideoView;
import com.ghts.player.widget.AdvertisementView;
import com.ghts.player.widget.AtsStationView;
import com.ghts.player.widget.AtsStrainView;
import com.ghts.player.widget.DateView;
import com.ghts.player.widget.ExigentInfo;
import com.ghts.player.widget.ExigentInfo_Full;
import com.ghts.player.widget.MyImgView;
import com.ghts.player.widget.ScrollView;
import com.ghts.player.widget.TimeView;
import com.ghts.player.widget.TitleView;
import com.ghts.player.widget.TrainInfoView;
import com.ghts.player.widget.WeatherView;
import com.ghts.player.widget.WeekView;
import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ParseXml {
    private static final String TAG = "xmlParserByPull";
    private static ParseXml parseXml = null;
    private Context context;

    public ParseXml(Context context) {
        this.context = context;
    }

    // 提供一个全局的静态方法
    public static ParseXml getParseXml(Context context) {
        if (parseXml == null) {
            parseXml = new ParseXml(context);
        }
        return parseXml;
    }

    //解析Global文件
    public GlobalBean ParseGlobal() throws IOException {
        File xmlFile = new File(Const.GLOBALPATH);
        GlobalBean globalBean = null;
        FileInputStream inputStream = null;
        Time start_time = null;
        Time stop_time = null;
        boolean ispCtrl = false;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            ArrayList<ChannelBean> liveList = null;
            ArrayList<String> initC = null;
            ArrayList<ChannelBean> pctrlList = null;
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("Params".equals(parser.getName())) {
                            globalBean = new GlobalBean();
                        } else if ("Server".equals(parser.getName())) {
                            globalBean.setCenterIP(parser.getAttributeValue("", "CenterIP"));
                            globalBean.setCmdServiceIP(parser.getAttributeValue("", "CmdServiceIP"));
                            globalBean.setCmdServicePort(parser.getAttributeValue("", "CmdServicePort"));
                            globalBean.setUpgradeIP(parser.getAttributeValue("", "UpgradeIP"));
                        } else if ("Line".equals(parser.getName())) {
                            globalBean.setLineCode(parser.getAttributeValue("", "Code"));
                        } else if ("Station".equals(parser.getName())) {
                            globalBean.setIsTrain(parser.getAttributeValue("", "IsTrain"));
                            globalBean.setStationCode(parser.getAttributeValue("", "Code"));
                            String startTime = parser.getAttributeValue("", "StartTime");
                            String stopTime = parser.getAttributeValue("", "StopTime");
                            int hour = 0;
                            int minute = 0;
                            int sec = 0;
                            if (!TextUtils.isEmpty(startTime)) {
                                String[] split_time = startTime.split(":");
                                if (split_time.length == 3) {
                                    hour = Integer.valueOf(split_time[0]);
                                    minute = Integer.valueOf(split_time[1]);
                                    sec = Integer.valueOf(split_time[2]);
                                }
                            }
                            start_time = new Time();
                            start_time.set(sec, minute, hour, 0, 0, 0); // 设置开始的时分秒，日期无效，设置为零。
                            globalBean.setOpenScreenTime(start_time);
                            int hours = 0;
                            int minutes = 0;
                            int secs = 0;
                            if (!TextUtils.isEmpty(stopTime)) {
                                String[] split_time = stopTime.split(":");
                                if (split_time.length == 3) {
                                    hours = PubUtil.parseInt(split_time[0]);
                                    minutes = PubUtil.parseInt(split_time[1]);
                                    secs = PubUtil.parseInt(split_time[2]);
                                }
                                stop_time = new Time();
                                stop_time.set(secs, minutes, hours, 0, 0, 0); // 设置开始的时分秒，日期无效，设置为零。
                                globalBean.setCloseScreenTime(stop_time);
                            }
                            globalBean.setStationName(parser.getAttributeValue("", "Name"));
                            globalBean.setStationEnName(parser.getAttributeValue("", "EnName"));
                        } else if ("Device".equals(parser.getName()) && !ispCtrl) {
                            globalBean.setDeviceCode(parser.getAttributeValue("", "Code"));
                            globalBean.setDevicePos(parser.getAttributeValue("", "Pos"));
                            globalBean.setDeviceType(parser.getAttributeValue("", "Type"));
                        } else if ("ATS".equals(parser.getName())) {
                            globalBean.setMCIP(parser.getAttributeValue("", "MCIP"));
                            globalBean.setMCPort(parser.getAttributeValue("", "MCPort"));
                            globalBean.setAtsID(parser.getAttributeValue("", "AtsID"));
                            globalBean.setPlatform(parser.getAttributeValue("", "Platform"));
                            int id = PubUtil.parseInt(parser.getAttributeValue("", "AtsID"));
                            Const.atsId = id;
                        } else if ("LiveClient".equals(parser.getName())) {
                            globalBean.setForwardMode(parser.getAttributeValue("", "ForwardMode"));
                        } else if ("LiveList".equals(parser.getName())) {
                            String count = parser.getAttributeValue("", "Count");
                            //                            int counts = PubUtil.parseInt(count);
                            liveList = new ArrayList<ChannelBean>();
                        } else if (parser.getName().startsWith("Channel")) {
                            ChannelBean channelBean = new ChannelBean();
                            channelBean.setName(parser.getAttributeValue("", "Name"));
                            channelBean.setSrcIP(parser.getAttributeValue("", "SrcIP"));
                            channelBean.setSrcPort(parser.getAttributeValue("", "SrcPort"));
                            channelBean.setPacketSize(parser.getAttributeValue("", "PacketSize"));
                            channelBean.setLiveIP(parser.getAttributeValue("", "LiveIP"));
                            channelBean.setLivePort(parser.getAttributeValue("", "LivePort"));
                            channelBean.setTrainIP(parser.getAttributeValue("", "TrainIP"));
                            channelBean.setTrainPort(parser.getAttributeValue("", "TrainPort"));
                            liveList.add(channelBean);
                        } else if (parser.getName().equals("InfoMonitor")) {
                            initC = new ArrayList<String>();
                            int count = PubUtil.parseInt(parser.getAttributeValue("", "Count"));
                        } else if (parser.getName().startsWith("Item")) {
                            String name = parser.getAttributeValue("", "File");
                            initC.add(name);
                        } else if (parser.getName().equals("PCtrl")) {
                            pctrlList = new ArrayList<ChannelBean>();
                            pctrlList.clear();
                            //pctrlType,pctrlCount,pctrlEnable,pctrlPort,pctrlOn,pctrOff,pctrlCom,pctrlOut
                            globalBean.setPctrlType(PubUtil.parseInt(parser.getAttributeValue("", "type")));
                            globalBean.setPctrlCount(PubUtil.parseInt(parser.getAttributeValue("", "Count")));
                            globalBean.setPctrlEnable(PubUtil.parseInt(parser.getAttributeValue("", "Enable")));
                            globalBean.setPctrlCom(parser.getAttributeValue("", "Com"));
                            globalBean.setPctrlPort(PubUtil.parseInt(parser.getAttributeValue("", "Port")));
                            globalBean.setPctrlOut(parser.getAttributeValue("", "Out"));
                            globalBean.setPctrlOn(PubUtil.parseInt(parser.getAttributeValue("", "on")));
                            globalBean.setPctrOff(PubUtil.parseInt(parser.getAttributeValue("", "off")));
                            Const.PORT = parser.getAttributeValue("", "Com");
                            Const.BAUDRATE = PubUtil.parseInt(parser.getAttributeValue("", "Port"));
                            ispCtrl = true;
                        } else if (parser.getName().startsWith("") && ispCtrl) {
                            ChannelBean bean = new ChannelBean();
                            bean.setAddr(parser.getAttributeValue("", "Addr"));
                            bean.setFirstPort(parser.getAttributeValue("", "FirstPort"));
                            bean.setLastPort(parser.getAttributeValue("", "LastPort"));
                            pctrlList.add(bean);
                        }
                        //                        else if (parser.getName().equals("Version_City")) {
                        //                            String name = parser.getAttributeValue("", "city");
                        //                            if (name.equals("nj")) {
                        //                                Const.PORT = "ttymxc2"; //南京控屏端口号
                        //                                Const.BAUDRATE = 38400;
                        //                                Const.versionName = "nanjing";
                        //                            } else if (name.equals("sh")) {
                        //                                Const.PORT = "ttymxc4"; //上海控屏端口号
                        //                                Const.BAUDRATE = 38400;
                        //                                Const.versionName = "shanghai";
                        //                            } else if (name.equals("chengdu")) {
                        //                                Const.PORT = "ttymxc2"; //上海控屏端口号
                        //                                Const.BAUDRATE = 9600;
                        //                                Const.versionName = "chengdu";
                        //                            }
                        //                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("LiveList")) {
                            globalBean.setLiveList(liveList);
                        } else if (parser.getName().equals("InfoMonitor")) {
                            globalBean.setInitC(initC);
                        } else if (parser.getName().equals("PCtrl")) {
                            ispCtrl = false;
                            globalBean.setPctrlList(pctrlList);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取globalparam文件出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return globalBean;
    }

    /**
     * 解析playlist播表文件
     */
    public PlayDateBean getPlayDate() throws IOException {
        File xmlFile = null;
        FileInputStream inputStream = null;
        XmlPullParser parser = Xml.newPullParser();
        PlayDateBean playDateBean = null;
        ArrayList<PlayDayBean> playlist = null;
        ArrayList<PlayDayBean> medialist = null;
        boolean isPlaylist = false;
        boolean isMedia = false;
        try {
            xmlFile = new File(Const.PLAYDATEPATH);
            inputStream = new FileInputStream(xmlFile);
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            playDateBean = new PlayDateBean();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("PlayList".equals(parser.getName())) {
                            playlist = new ArrayList<PlayDayBean>();
                            int count = PubUtil.parseInt(parser.getAttributeValue("", "Count"), 0);
                            if (count > 0) {
                                isPlaylist = true;
                            } else {
                                isPlaylist = false;
                            }
                        } else if (!parser.getName().equals("day") && parser.getName().startsWith("d") && isPlaylist) {
                            PlayDayBean dayBean = new PlayDayBean();
                            String playDate = parser.getName();
                            dayBean.setdTime(playDate);
                            dayBean.setFile(parser.getAttributeValue("", "File"));
                            dayBean.setPlayFlag(parser.getAttributeValue("", "PlayFlag"));
                            playlist.add(dayBean);
                        } else if ("VideoList".equals(parser.getName())) {
                            medialist = new ArrayList<PlayDayBean>();
                            int count = PubUtil.parseInt(parser.getAttributeValue("", "Count"), 0);
                            if (count > 0) {
                                isMedia = true;
                            } else {
                                isMedia = false;
                            }
                        } else if (!parser.getName().equals("day") && parser.getName().startsWith("d") && isMedia) {
                            PlayDayBean dayBean = new PlayDayBean();
                            String playDate = parser.getName();
                            dayBean.setdTime(playDate);
                            dayBean.setFile(parser.getAttributeValue("", "File"));
                            dayBean.setPlayFlag(parser.getAttributeValue("", "PlayFlag"));
                            medialist.add(dayBean);
                        } else if (parser.getName().equals("DefaultList")) {
                            ArrayList<PlayDayBean> playDateList = new ArrayList<PlayDayBean>();
                            playDateList.clear();
                            PlayDayBean pd = new PlayDayBean();
                            pd.setFile(parser.getAttributeValue("", "File"));
                            playDateList.add(pd);
                            playDateBean.setDefaultlist(playDateList);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("PlayList")) {
                            playDateBean.setDayList(playlist);
                            isPlaylist = false;
                        } else if ("VideoList".equals(parser.getName())) {
                            playDateBean.setMediaList(medialist);
                            isMedia = false;
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取playlist文件出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        LogUtil.e("--playDateBean---", playDateBean.toString());
        return playDateBean;
    }

    /**
     * 解析字体文件
     */
    public ArrayList<FontXml> getFonts() throws IOException {
        File xmlFile = null;
        FileInputStream inputStream = null;
        XmlPullParser parser = Xml.newPullParser();
        ArrayList<FontXml> fontList = new ArrayList<FontXml>();
        int count = 0;
        try {
            xmlFile = new File(Const.FONTPATH);
            inputStream = new FileInputStream(xmlFile);
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while ((type != XmlPullParser.END_DOCUMENT)) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("Params".equals(parser.getName())) {
                            fontList.clear();
                            count = PubUtil.parseInt(parser.getAttributeValue("", "Count"));
                        } else if (parser.getName().startsWith("Font")) {
                            String name = parser.getAttributeValue("", "Name");
                            String value = parser.getAttributeValue("", "Value");
                            FontXml bean = new FontXml( name, value);
                            fontList.add(bean);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("Params".equals(parser.getName())) {

                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("解析font字体文件出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return fontList;
    }

    /**
     * 解析staion.xml目的码
     */
    public ArrayList<Stationxml> getStation() throws IOException {
        File xmlFile = null;
        FileInputStream inputStream = null;
        XmlPullParser parser = Xml.newPullParser();
        ArrayList<Stationxml> stationList = new ArrayList<Stationxml>();
        int count = 0;
        try {
            xmlFile = new File(Const.STATIONPATH);
            inputStream = new FileInputStream(xmlFile);
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while ((type != XmlPullParser.END_DOCUMENT)) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("Params".equals(parser.getName())) {
                            stationList.clear();
                            count = PubUtil.parseInt(parser.getAttributeValue("", "Count"));
                        } else if (parser.getName().startsWith("Station")) {
                            String code = parser.getAttributeValue("", "Code");
                            String map = parser.getAttributeValue("", "Map");
                            String name = parser.getAttributeValue("", "Name");
                            String enName = parser.getAttributeValue("", "EnName");
                            Stationxml bean = new Stationxml(count, code, map, name, enName);
                            stationList.add(bean);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("Params".equals(parser.getName())) {

                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("解析station目的码文件出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return stationList;
    }

    /**
     * 解析升级信息
     */
    public UpgradeBean getUpgrade() throws IOException {
        File xmlFile = null;
        FileInputStream inputStream = null;
        UpgradeBean upgrade = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            xmlFile = new File(Const.UPGRADE);
            inputStream = new FileInputStream(xmlFile);
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("item".equals(parser.getName())) {
                            upgrade = new UpgradeBean();
                            String types = parser.getAttributeValue("", "type");
                            if (types != null && types.equals("apk")) {
                                upgrade.setType("apk");
                                String fileName = parser.getAttributeValue("", "file");
                                File upgradeFile = new File(fileName);
                                upgrade.setFile(upgradeFile);
                                upgrade.setInstall(parser.getAttributeValue("", "install"));

                                DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                String edit_time_str = parser.getAttributeValue("", "updatetime");
                                Date updateTime = formater.parse(edit_time_str);
                                upgrade.setTime(updateTime);
                            }
                        } else if ("".equals(parser.getName())) {

                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取upgrade文件出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return upgrade;
    }

    /**
     * 解析当天播放的播表文件
     */
    public PlayList getPlayList(String path) throws IOException {
        File xmlFile = null;
        FileInputStream inputStream = null;

        XmlPullParser parser = Xml.newPullParser();
        PlayList playList = new PlayList();
        DayList dayList = new DayList();
        List<DayList> dayListList = null;
        LayoutBean layoutBean = null;
        List<LayoutBean> layoutLists = null;
        Time start_time = null;
        Time stop_time = null;
        try {
            xmlFile = new File(path);
            inputStream = new FileInputStream(xmlFile);
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("List".equals(parser.getName())) {
                            playList = new PlayList();
                            layoutLists = new ArrayList<LayoutBean>();
                            dayListList = new ArrayList<DayList>();

                            DateFormat formater = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String edit_time_str = parser.getAttributeValue("", "UpdateTime");
                            Date edit_time = formater.parse(edit_time_str);
                            playList.setUpdateTime(edit_time);

                            playList.setDayNum(parser.getAttributeValue(1));
                        } else if (parser.getName().substring(0, 3).equals("Day")) {
                            dayList = new DayList();
                            dayList.setDate(parser.getAttributeValue(0));
                            dayList.setLayoutNum(PubUtil.parseInt(parser.getAttributeValue(1)));
                            //                            Log.e("--dayList---", dayList.getDate() + "--" + dayList.getLayoutNum());
                        } else if (parser.getName().substring(0, 6).equals("Layout")) {
                            //                            <Layout0 File="南京车站1920X1080.XML" StartTime="00:00:01" StopDate="4382" StopTime="" />
                            layoutBean = new LayoutBean();
                            layoutBean.setFileName(parser.getAttributeValue(0).toLowerCase());
                            layoutBean.setStopDate(parser.getAttributeValue(2));

                            String start_time_str = parser.getAttributeValue(1); // layout标签的第二个属性值（开始时间）
                            int hour = 0;
                            int minute = 0;
                            int sec = 0;
                            if (!TextUtils.isEmpty(start_time_str)) {
                                if (start_time_str.length() == 3) {
                                    String[] split_time = start_time_str.split(":");
                                    hour = Integer.valueOf(split_time[0]);
                                    minute = Integer.valueOf(split_time[1]);
                                    sec = Integer.valueOf(split_time[2]);
                                }
                            }
                            start_time = new Time();
                            start_time.set(sec, minute, hour, 0, 0, 0); // 设置开始的时分秒，日期无效，设置为零。
                            layoutBean.setStartTime(start_time);

                            String stop_time_str = parser.getAttributeValue(3); // layout标签的第二个属性值（开始时间）
                            int hours = 0;
                            int minutes = 0;
                            int secs = 0;
                            if (!TextUtils.isEmpty(stop_time_str)) {
                                String[] split_time = stop_time_str.split(":");
                                if (split_time.length == 3) {
                                    hours = PubUtil.parseInt(split_time[0]);
                                    minutes = PubUtil.parseInt(split_time[1]);
                                    secs = PubUtil.parseInt(split_time[2]);
                                    stop_time = new Time();
                                    stop_time.set(secs, minutes, hours, 0, 0, 0); // 设置开始的时分秒，日期无效，设置为零。
                                    layoutBean.setStopTime(stop_time);
                                }
                            }
                            layoutLists.add(layoutBean);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().substring(0, 3).equals("Day")) {
                            dayList.setLayoutLists(layoutLists);
                        } else if (parser.getName().equals("List")) {
                            dayListList.add(dayList);
                            playList.setDayListList(dayListList);
                            playList.setLayoutBeanList(dayList.getLayoutLists());
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取playlist文件出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        LogUtil.e("--- playList--", playList.toString());
        return playList;
    }


    /**
     * 解析Task_list信息
     */
    public ArrayList<TaskBean> getTask() throws IOException {
        File xmlFile = null;
        FileInputStream inputStream = null;
        ArrayList<TaskBean> taskBeenList = new ArrayList<TaskBean>();
        taskBeenList.clear();
        XmlPullParser parser = Xml.newPullParser();
        try {
            xmlFile = new File(Const.TASK_LIST);
            inputStream = new FileInputStream(xmlFile);
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("t1".equals(parser.getName())) {
                            TaskBean taskBean = new TaskBean();
                            //                            <t1 enable="0" time="12:52:50" time_limit="5">4</t1>
                            taskBean.setEnable(parser.getAttributeValue("", "enable"));
                            String start_time_str = parser.getAttributeValue("", "time"); // layout标签的第二个属性值（开始时间）
                            int hour = 0;
                            int minute = 0;
                            int sec = 0;
                            if (!TextUtils.isEmpty(start_time_str)) {
                                String[] split_time = start_time_str.split(":");
                                if (split_time.length == 3) {
                                    hour = Integer.valueOf(split_time[0]);
                                    minute = Integer.valueOf(split_time[1]);
                                    sec = Integer.valueOf(split_time[2]);
                                }
                            }
                            Time time = new Time();
                            time.set(sec, minute, hour, 0, 0, 0); // 设置开始的时分秒，日期无效，设置为零。
                            taskBean.setTime(time);
                            taskBean.setTime_limit(parser.getAttributeValue("", "time_limit"));
                            taskBean.setContent(parser.nextText());
                            taskBeenList.add(taskBean);
                        } else if ("t2".equals(parser.getName())) {
                            TaskBean taskBean = new TaskBean();
                            //                            <t1 enable="0" time="12:52:50" time_limit="5">4</t1>
                            taskBean.setEnable(parser.getAttributeValue("", "enable"));
                            String start_time_str = parser.getAttributeValue("", "time"); // layout标签的第二个属性值（开始时间）
                            int hour = 0;
                            int minute = 0;
                            int sec = 0;
                            if (!TextUtils.isEmpty(start_time_str)) {
                                String[] split_time = start_time_str.split(":");
                                if (split_time.length == 3) {
                                    hour = Integer.valueOf(split_time[0]);
                                    minute = Integer.valueOf(split_time[1]);
                                    sec = Integer.valueOf(split_time[2]);
                                }
                            }
                            Time time = new Time();
                            time.set(sec, minute, hour, 0, 0, 0); // 设置开始的时分秒，日期无效，设置为零。
                            taskBean.setTime(time);
                            taskBean.setTime_limit(parser.getAttributeValue("", "time_limit"));
                            taskBean.setContent(parser.nextText());
                            taskBeenList.add(taskBean);
                        } else if ("t3".equals(parser.getName())) {
                            TaskBean taskBean = new TaskBean();
                            //                            <t1 enable="0" time="12:52:50" time_limit="5">4</t1>
                            taskBean.setEnable(parser.getAttributeValue("", "enable"));
                            String start_time_str = parser.getAttributeValue("", "time"); // layout标签的第二个属性值（开始时间）
                            int hour = 0;
                            int minute = 0;
                            int sec = 0;
                            if (!TextUtils.isEmpty(start_time_str)) {
                                String[] split_time = start_time_str.split(":");
                                if (split_time.length == 3) {
                                    hour = Integer.valueOf(split_time[0]);
                                    minute = Integer.valueOf(split_time[1]);
                                    sec = Integer.valueOf(split_time[2]);
                                }
                            }
                            Time time = new Time();
                            time.set(sec, minute, hour, 0, 0, 0); // 设置开始的时分秒，日期无效，设置为零。
                            taskBean.setTime(time);
                            taskBean.setTime_limit(parser.getAttributeValue("", "time_limit"));
                            taskBean.setContent(parser.nextText());
                            taskBeenList.add(taskBean);
                        } else if ("t4".equals(parser.getName())) {
                            TaskBean taskBean = new TaskBean();
                            //                            <t1 enable="0" time="12:52:50" time_limit="5">4</t1>
                            taskBean.setEnable(parser.getAttributeValue("", "enable"));
                            String start_time_str = parser.getAttributeValue("", "time"); // layout标签的第二个属性值（开始时间）
                            int hour = 0;
                            int minute = 0;
                            int sec = 0;
                            if (!TextUtils.isEmpty(start_time_str)) {
                                String[] split_time = start_time_str.split(":");
                                if (split_time.length == 3) {
                                    hour = Integer.valueOf(split_time[0]);
                                    minute = Integer.valueOf(split_time[1]);
                                    sec = Integer.valueOf(split_time[2]);
                                }
                            }
                            Time time = new Time();
                            time.set(sec, minute, hour, 0, 0, 0); // 设置开始的时分秒，日期无效，设置为零。
                            taskBean.setTime(time);
                            taskBean.setTime_limit(parser.getAttributeValue("", "time_limit"));
                            taskBean.setContent(parser.nextText());
                            taskBeenList.add(taskBean);
                        } else if ("t5".equals(parser.getName())) {
                            TaskBean taskBean = new TaskBean();
                            //                            <t1 enable="0" time="12:52:50" time_limit="5">4</t1>
                            taskBean.setEnable(parser.getAttributeValue("", "enable"));
                            String start_time_str = parser.getAttributeValue("", "time"); // layout标签的第二个属性值（开始时间）
                            int hour = 0;
                            int minute = 0;
                            int sec = 0;
                            if (!TextUtils.isEmpty(start_time_str)) {
                                String[] split_time = start_time_str.split(":");
                                if (split_time.length == 3) {
                                    hour = Integer.valueOf(split_time[0]);
                                    minute = Integer.valueOf(split_time[1]);
                                    sec = Integer.valueOf(split_time[2]);
                                }
                            }
                            Time time = new Time();
                            time.set(sec, minute, hour, 0, 0, 0); // 设置开始的时分秒，日期无效，设置为零。
                            taskBean.setTime(time);
                            taskBean.setTime_limit(parser.getAttributeValue("", "time_limit"));
                            taskBean.setContent(parser.nextText());
                            taskBeenList.add(taskBean);
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取Task文件出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return taskBeenList;
    }


    /**
     * 解析模版文件
     *
     * @param path
     * @return
     */
    public MsgBean getMsgXml(String path) throws IOException {
        File xmlFile = new File(path);
        FileInputStream inStream = null;
        XmlPullParser parser = Xml.newPullParser();
        MsgBean bean = null;
        ArrayList<MsgModel> msgModels = null;
        ArrayList<MsgModel> emmsgModels = null;
        int count = 0;
        boolean isModuleList = false;
        boolean isEmModuleList = false;
        try {
            inStream = new FileInputStream(xmlFile);
            parser.setInput(inStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        Log.e("---START_TAG---", parser.getName());
                        if ("Layout".equals(parser.getName())) {
                            bean = new MsgBean();
                            bean.setUpdateTime(parser.getAttributeValue(0));
                            bean.setWidth(PubUtil.parseInt(parser.getAttributeValue(1)));
                            bean.setHeight(PubUtil.parseInt(parser.getAttributeValue(2)));
                            bean.setBkType(PubUtil.parseInt(parser.getAttributeValue(3)));
                            bean.setBkFIle(parser.getAttributeValue(4).toLowerCase());
                        } else if ("ModuleList".equals(parser.getName())) {
                            isModuleList = true;
                            count = (PubUtil.parseInt(parser.getAttributeValue(null, "Count")));
                            bean.setMsgCount(count);
                            msgModels = new ArrayList<MsgModel>();
                        } else if ("Item".equals(parser.getName().substring(0, 4)) && isModuleList) {
                            MsgModel model = new MsgModel();
                            model.setModule(parser.getAttributeValue(0));
                            model.setIndex(PubUtil.parseInt(parser.getAttributeValue(1)));
                            model.setLeft(PubUtil.parseInt(parser.getAttributeValue(2)));
                            model.setRight(PubUtil.parseInt(parser.getAttributeValue(3)));
                            model.setTop(PubUtil.parseInt(parser.getAttributeValue(4)));
                            model.setBottom(PubUtil.parseInt(parser.getAttributeValue(5)));
                            model.setModuleName(parser.getAttributeValue(6));
                            msgModels.add(model);
                        } else if ("EmModuleList".equals(parser.getName())) {
                            isEmModuleList = true;
                            count = (PubUtil.parseInt(parser.getAttributeValue(null, "Count")));
                            emmsgModels = new ArrayList<MsgModel>();
                            bean.setEmMSgCount(count);
                        } else if ("Item".equals(parser.getName().substring(0, 4)) && isEmModuleList) {
                            MsgModel model = new MsgModel();
                            model.setModule(parser.getAttributeValue(0));
                            model.setLeft(PubUtil.parseInt(parser.getAttributeValue(1)));
                            model.setRight(PubUtil.parseInt(parser.getAttributeValue(2)));
                            model.setTop(PubUtil.parseInt(parser.getAttributeValue(3)));
                            model.setBottom(PubUtil.parseInt(parser.getAttributeValue(4)));
                            model.setModuleName(parser.getAttributeValue(5));
                            emmsgModels.add(model);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("ModuleList".equals(parser.getName())) {
                            isModuleList = false;
                            bean.setMsgModels(msgModels);
                        } else if ("EmModuleList".equals(parser.getName())) {
                            isEmModuleList = false;
                            bean.setEmMsgModels(emmsgModels);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取版式文件出错", e.toString());
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }
        return bean;
    }

    //解析文本文文件
    public TitleView ParseTxt(String path) throws IOException {
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        TitleView titleView = null;
        BGParam backgroud = null;
        FontParam fontParam = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            titleView = new TitleView();
                            titleView.setModule_type(ConstantValue.MODULE_TYPE_TEXT);
                            titleView.setModule_name(parser.getAttributeValue(0));
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            titleView.setModule_Pos(pos);
                        } else if ("BGParam".equals(parser.getName())) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                        } else if ("Pure".equals(parser.getName())) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName())) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            RGBA color = new RGBA();
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName())) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            String dir = xmlFile.getParent();
                            dir = dir + "/" + bkfile_name;
                            File bkfile = new File(dir);
                            backgroud.setBkfile(bkfile);
                        } else if ("Font".equals(parser.getName())) {
                            fontParam = new FontParam();
                            String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                            fontParam.setName(name);
                            String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                            int size = Integer.valueOf(size_str);
                            fontParam.setSize((int) (size * Const.ScaleX));
                            String width_str = parser.getAttributeValue("", "Width"); // Width
                            int width = Integer.valueOf(width_str);
                            fontParam.setWidth(width);
                            String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                            int kerning = Integer.valueOf(kerning_str);
                            fontParam.setKerning(kerning);
                            String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                            int spacing = Integer.valueOf(spacing_str);
                            fontParam.setSpacing(spacing);
                        } else if ("Style".equals(parser.getName())) {
                            String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
                            int escapement = Integer.valueOf(escapement_str);
                            fontParam.setEscapement(escapement);
                            String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
                            int orientation = Integer.valueOf(orientation_str);
                            fontParam.setOrientation(orientation);
                            String bold_str = parser.getAttributeValue("", "Bold"); // Bold
                            int bold = Integer.valueOf(bold_str);
                            if (bold == 0) {
                                fontParam.setBold(false);
                            } else {
                                fontParam.setBold(true);
                            }
                            String italic_str = parser.getAttributeValue("", "Italic"); // Italic
                            int italic = Integer.valueOf(italic_str);
                            if (italic == 0) {
                                fontParam.setItalic(false);
                            } else {
                                fontParam.setItalic(true);
                            }
                            String underline_str = parser.getAttributeValue("", "Underline"); // Underline
                            int underline = Integer.valueOf(underline_str);
                            if (underline == 0) {
                                fontParam.setUnderline(false);
                            } else {
                                fontParam.setUnderline(true);
                            }
                        } else if ("Face".equals(parser.getName())) {
                            int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
                            int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
                            fontParam.setFaceColor(initXmlColor(fcolor, falpha));
                        } else if ("Edge".equals(parser.getName())) {
                            String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
                            int edgewidth = Integer.valueOf(edgewidth_str);
                            fontParam.setEdgewidth(edgewidth);
                            int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
                            int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
                        } else if ("Shadow".equals(parser.getName())) {
                            String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
                            int shadowWidth = Integer.valueOf(shadowWidth_str);
                            fontParam.setShadowWidth(shadowWidth);
                            int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
                            int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
                            String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
                            int shadowAngle = Integer.valueOf(shadowAngle_str);
                            fontParam.setShadowAngle(shadowAngle);
                        } else if ("Content".equals(parser.getName())) {
                            titleView.setShowStr(parser.getAttributeValue("", "Top"));
                            titleView.setBottomStr(parser.getAttributeValue("", "Bottom"));
                        } else if ("Effect".equals(parser.getName())) {
                            int align = PubUtil.parseInt(parser.getAttributeValue("", "AlignType"));
                            if (align == 0) {
                                titleView.setAlign(AlignMode.MIDDLELEFT);
                            } else if (align == 1) {
                                titleView.setAlign(AlignMode.MIDDLERIGHT);
                            } else if (align == 2) {
                                titleView.setAlign(AlignMode.CENTER);
                            }
                            titleView.setTop_margin(PubUtil.parseInt(parser.getAttributeValue("", "VMargin")));
                            titleView.setLeft_margin(PubUtil.parseInt(parser.getAttributeValue("", "HMargin")));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("BGParam".equals(parser.getName())) {
                            titleView.setBackgroud(backgroud);
                        } else if ("Font".equals(parser.getName())) {
                            titleView.setFont(fontParam);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取文本模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return titleView;
    }

    //解析snap切换文本模块
    //    public SnapView ParseSnapTxt(String path) throws IOException {
    //        File xmlFile = new File(path);
    //        FileInputStream inputStream = null;
    //        SnapView snapView = null;
    //        BGParam backgroud = null;
    //        FontParam fontParam = null;
    //        try {
    //            inputStream = new FileInputStream(xmlFile);
    //            XmlPullParser parser = Xml.newPullParser();
    //            parser.setInput(inputStream, "UTF-8");
    //            int type = parser.getEventType();
    //            while (type != XmlPullParser.END_DOCUMENT) {
    //                switch (type) {
    //                    case XmlPullParser.START_TAG:
    //                        if ("System".equals(parser.getName())) {
    //                            snapView = new SnapView();
    //                            snapView.setModule_type(ConstantValue.MODULE_TYPE_SNAPTEXT);
    //                            snapView.setModule_name(parser.getAttributeValue(0));
    //                            POS pos = new POS();
    //                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
    //                            pos.setWidth(loc[0]);
    //                            pos.setHeight(loc[1]);
    //                            pos.setLeft(loc[2]);
    //                            pos.setTop(loc[3]);
    //                            snapView.setModule_Pos(pos);
    //                        } else if ("BGParam".equals(parser.getName())) {
    //                            backgroud = new BGParam();
    //                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
    //                            if (bgType == 0) {
    //                                backgroud.setType(BackType.PURECOLOR);
    //                            } else if (bgType == 1) {
    //                                backgroud.setType(BackType.GRADIENTCOLOR);
    //                            } else if (bgType == 2) {
    //                                backgroud.setType(BackType.PICTURE);
    //                            } else if (bgType == 3) {
    //                                backgroud.setType(BackType.NOTSHOW);
    //                            }
    //                        } else if ("Pure".equals(parser.getName())) {
    //                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
    //                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
    //                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
    //                        } else if ("Blend".equals(parser.getName())) {
    //                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
    //                            if (colorType == 0) {
    //                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
    //                            } else {
    //                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
    //                            }
    //                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
    //                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
    //                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
    //                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
    //                            RGBA color = new RGBA();
    //                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
    //                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
    //                        } else if ("Picture".equals(parser.getName())) {
    //                            String bkfile_name = parser.getAttributeValue("", "File");
    //设置图片路径
    //    String dir = xmlFile.getParent();
    //    dir = dir + "/" + bkfile_name;
    //    File bkfile = new File(dir);
    //                            backgroud.setBkfile(bkfile);
    //                        } else if ("Font".equals(parser.getName())) {
    //                            fontParam = new FontParam();
    //                            String name = parser.getAttributeValue("", "Name"); // Name="宋体"
    //                            fontParam.setName(name);
    //                            String size_str = parser.getAttributeValue("", "Size"); // Size="40"
    //                            int size = Integer.valueOf(size_str);
    //                            fontParam.setSize((int) (size * Const.ScaleX));
    //                            String width_str = parser.getAttributeValue("", "Width"); // Width
    //                            int width = Integer.valueOf(width_str);
    //                            fontParam.setWidth(width);
    //                            String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
    //                            int kerning = Integer.valueOf(kerning_str);
    //                            fontParam.setKerning(kerning);
    //                            String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
    //                            int spacing = Integer.valueOf(spacing_str);
    //                            fontParam.setSpacing(spacing);
    //                        } else if ("Style".equals(parser.getName())) {
    //                            String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
    //                            int escapement = Integer.valueOf(escapement_str);
    //                            fontParam.setEscapement(escapement);
    //                            String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
    //                            int orientation = Integer.valueOf(orientation_str);
    //                            fontParam.setOrientation(orientation);
    //                            String bold_str = parser.getAttributeValue("", "Bold"); // Bold
    //                            int bold = Integer.valueOf(bold_str);
    //                            if (bold == 0) {
    //                                fontParam.setBold(false);
    //                            } else {
    //                                fontParam.setBold(true);
    //                            }
    //                            String italic_str = parser.getAttributeValue("", "Italic"); // Italic
    //                            int italic = Integer.valueOf(italic_str);
    //                            if (italic == 0) {
    //                                fontParam.setItalic(false);
    //                            } else {
    //                                fontParam.setItalic(true);
    //                            }
    //                            String underline_str = parser.getAttributeValue("", "Underline"); // Underline
    //                            int underline = Integer.valueOf(underline_str);
    //                            if (underline == 0) {
    //                                fontParam.setUnderline(false);
    //                            } else {
    //                                fontParam.setUnderline(true);
    //                            }
    //                        } else if ("Face".equals(parser.getName())) {
    //                            int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
    //                            int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
    //                            fontParam.setFaceColor(initXmlColor(fcolor, falpha));
    //                        } else if ("Edge".equals(parser.getName())) {
    //                            String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
    //                            int edgewidth = Integer.valueOf(edgewidth_str);
    //                            fontParam.setEdgewidth(edgewidth);
    //                            int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
    //                            int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
    //                            fontParam.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
    //                        } else if ("Shadow".equals(parser.getName())) {
    //                            String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
    //                            int shadowWidth = Integer.valueOf(shadowWidth_str);
    //                            fontParam.setShadowWidth(shadowWidth);
    //                            int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
    //                            int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
    //                            fontParam.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
    //                            String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
    //                            int shadowAngle = Integer.valueOf(shadowAngle_str);
    //                            fontParam.setShadowAngle(shadowAngle);
    //                        } else if ("Content".equals(parser.getName())) {
    //                            titleView.setShowStr(parser.getAttributeValue("", "Top"));
    //                            titleView.setBottomStr(parser.getAttributeValue("", "Bottom"));
    //                        } else if ("Effect".equals(parser.getName())) {
    //                            int align = PubUtil.parseInt(parser.getAttributeValue("", "AlignType"));
    //                            if (align == 0) {
    //                                titleView.setAlign(AlignMode.MIDDLELEFT);
    //                            } else if (align == 1) {
    //                                titleView.setAlign(AlignMode.MIDDLERIGHT);
    //                            } else if (align == 2) {
    //                                titleView.setAlign(AlignMode.CENTER);
    //                            }
    //                            titleView.setTop_margin(PubUtil.parseInt(parser.getAttributeValue("", "VMargin")));
    //                            titleView.setLeft_margin(PubUtil.parseInt(parser.getAttributeValue("", "HMargin")));
    //                        }
    //                        break;
    //                    case XmlPullParser.END_TAG:
    //                        if ("BGParam".equals(parser.getName())) {
    //                            titleView.setBackgroud(backgroud);
    //                        } else if ("Font".equals(parser.getName())) {
    //                            titleView.setFont(fontParam);
    //                        }
    //                        break;
    //                }
    //                type = parser.next();
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        } finally {
    //            if (inputStream != null) {
    //                inputStream.close();
    //            }
    //        }
    //        return titleView;
    //    }


    //解析时间Date
    public DateView ParseDate(String path) throws IOException {
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        DateView dateView = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            dateView = new DateView();
            FontParam date_font = null;
            FontParam week_font = null;
            BGParam date_bg = null;
            BGParam week_bg = null;
            dateView.setModule_type(ConstantValue.MODULE_TYPE_DATE);
            boolean isDate = false;
            boolean isWeek = false;
            boolean isFontStyle = false;
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("Date".equals(parser.getName())) {
                            String show = parser.getAttributeValue("", "Show");
                            int show_date = PubUtil.parseInt(show);
                            if (show_date == 0) {
                                dateView.setShow_date(false);
                            } else {
                                dateView.setShow_date(true);
                            }
                            isDate = true;
                            isWeek = false;
                        } else if ("Week".equals(parser.getName())) {
                            String show = parser.getAttributeValue("", "Show");
                            int show_date = PubUtil.parseInt(show);
                            if (show_date == 0) {
                                dateView.setShow_week(false);
                            } else {
                                dateView.setShow_week(true);
                            }
                            isDate = false;
                            isWeek = true;
                        } else if ("Font".equals(parser.getName())) {
                            isFontStyle = true;
                            if (isDate) {
                                date_font = new FontParam();
                                String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                                date_font.setName(name);
                                String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                                int size = Integer.valueOf(size_str);
                                date_font.setSize((int) (size * Const.ScaleX));
                                String width_str = parser.getAttributeValue("", "Width"); // Width
                                int width = Integer.valueOf(width_str);
                                date_font.setWidth(width);
                                String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                                int kerning = Integer.valueOf(kerning_str);
                                date_font.setKerning(kerning);
                                String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                                int spacing = Integer.valueOf(spacing_str);
                                date_font.setSpacing(spacing);
                            } else if (isWeek) {
                                week_font = new FontParam();
                                String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                                week_font.setName(name);
                                String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                                int size = Integer.valueOf(size_str);
                                week_font.setSize((int) (size * Const.ScaleX));
                                String width_str = parser.getAttributeValue("", "Width"); // Width
                                int width = Integer.valueOf(width_str);
                                week_font.setWidth(width);
                                String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                                int kerning = Integer.valueOf(kerning_str);
                                week_font.setKerning(kerning);
                                String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                                int spacing = Integer.valueOf(spacing_str);
                                week_font.setSpacing(spacing);
                            }
                        } else if ("Style".equals(parser.getName())) {
                            if (isDate && isFontStyle) {
                                String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
                                int escapement = Integer.valueOf(escapement_str);
                                date_font.setEscapement(escapement);
                                String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
                                int orientation = Integer.valueOf(orientation_str);
                                date_font.setOrientation(orientation);
                                String bold_str = parser.getAttributeValue("", "Bold"); // Bold
                                int bold = Integer.valueOf(bold_str);
                                if (bold == 0) {
                                    date_font.setBold(false);
                                } else {
                                    date_font.setBold(true);
                                }
                                String italic_str = parser.getAttributeValue("", "Italic"); // Italic
                                int italic = Integer.valueOf(italic_str);
                                if (italic == 0) {
                                    date_font.setItalic(false);
                                } else {
                                    date_font.setItalic(true);
                                }
                                String underline_str = parser.getAttributeValue("", "Underline"); // Underline
                                int underline = Integer.valueOf(underline_str);
                                if (underline == 0) {
                                    date_font.setUnderline(false);
                                } else {
                                    date_font.setUnderline(true);
                                }
                            } else if (isDate && !isFontStyle) {
                                //显示类型
                                String date_Lang_str = parser.getAttributeValue("", "Type");
                                int date_Lang = PubUtil.parseInt(date_Lang_str);
                                if (date_Lang == 0) {
                                    dateView.setDate_Lang(ShowLang.ENGLISH);
                                } else if (date_Lang == 1) {
                                    dateView.setDate_Lang(ShowLang.CHINESE);
                                } else if (date_Lang == 2) {
                                    dateView.setDate_Lang(ShowLang.DIGITAL);
                                }
                                DateParam date_param = new DateParam();
                                String show_seq_str = parser.getAttributeValue("", "Order");
                                int show_seq = Integer.valueOf(show_seq_str);
                                if (show_seq == 0) {
                                    date_param.setShow_seq(DateShow.YMD);
                                } else if (show_seq == 1) {
                                    date_param.setShow_seq(DateShow.MDY);
                                } else if (show_seq == 2) {
                                    date_param.setShow_seq(DateShow.DMY);
                                }
                                String year_show_str = parser.getAttributeValue("", "YearMethod");
                                int year_show = PubUtil.parseInt(year_show_str);
                                if (year_show == 0) {
                                    date_param.setYear_show(YearShow.SHOWFOUR);
                                } else if (year_show == 1) {
                                    date_param.setYear_show(YearShow.SHOWTWO);
                                } else if (year_show == 2) {
                                    date_param.setYear_show(YearShow.NOTSHOW);
                                }
                                String mD_Show_str = parser.getAttributeValue("", "MonthMethod");
                                int mD_Show = Integer.valueOf(mD_Show_str);
                                if (mD_Show == 1) {
                                    date_param.setMD_Show(MDShow.NOZERO);
                                } else if (mD_Show == 2) {
                                    date_param.setMD_Show(MDShow.ADDZERO);
                                }
                                String year_tip = parser.getAttributeValue("", "Year");
                                date_param.setYear_tip(year_tip);
                                String month_tip = parser.getAttributeValue("", "Month");
                                date_param.setMonth_tip(month_tip);
                                String day_tip = parser.getAttributeValue("", "Day");
                                date_param.setDay_tip(day_tip);
                                dateView.setDate_param(date_param);
                            } else if (isWeek && isFontStyle) {
                                String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
                                int escapement = Integer.valueOf(escapement_str);
                                week_font.setEscapement(escapement);
                                String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
                                int orientation = Integer.valueOf(orientation_str);
                                week_font.setOrientation(orientation);
                                String bold_str = parser.getAttributeValue("", "Bold"); // Bold
                                int bold = Integer.valueOf(bold_str);
                                if (bold == 0) {
                                    week_font.setBold(false);
                                } else {
                                    week_font.setBold(true);
                                }
                                String italic_str = parser.getAttributeValue("", "Italic"); // Italic
                                int italic = Integer.valueOf(italic_str);
                                if (italic == 0) {
                                    week_font.setItalic(false);
                                } else {
                                    week_font.setItalic(true);
                                }
                                String underline_str = parser.getAttributeValue("", "Underline"); // Underline
                                int underline = Integer.valueOf(underline_str);
                                if (underline == 0) {
                                    week_font.setUnderline(false);
                                } else {
                                    week_font.setUnderline(true);
                                }
                            } else if (isWeek && !isFontStyle) {
                                String week_lang_str = parser.getAttributeValue("", "Type");
                                int week_lang = PubUtil.parseInt(week_lang_str);
                                if (week_lang == 0) {
                                    dateView.setWeek_lang(ShowLang.ENGLISH);
                                } else if (week_lang == 1) {
                                    dateView.setWeek_lang(ShowLang.CHINESE);
                                }
                                String spacenum = parser.getAttributeValue("", "SpaceNum");
                                dateView.setSpaceNum(PubUtil.parseInt(spacenum));
                                int weekPos = PubUtil.parseInt(parser.getAttributeValue("", "Pos"));
                                dateView.setWeekPos(weekPos);
                            }
                        } else if ("Face".equals(parser.getName())) {
                            if (isDate) {
                                int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
                                int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
                                date_font.setFaceColor(initXmlColor(fcolor, falpha));

                            } else if (isWeek) {
                                int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
                                int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
                                week_font.setFaceColor(initXmlColor(fcolor, falpha));
                            }
                        } else if ("Edge".equals(parser.getName())) {
                            if (isDate) {
                                String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
                                int edgewidth = Integer.valueOf(edgewidth_str);
                                date_font.setEdgewidth(edgewidth);
                                int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
                                int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                                date_font.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
                            } else if (isWeek) {
                                String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
                                int edgewidth = Integer.valueOf(edgewidth_str);
                                week_font.setEdgewidth(edgewidth);
                                int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
                                int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                                week_font.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
                            }
                        } else if ("Shadow".equals(parser.getName())) {
                            if (isDate) {
                                String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
                                int shadowWidth = Integer.valueOf(shadowWidth_str);
                                date_font.setShadowWidth(shadowWidth);
                                int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
                                int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                                date_font.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
                                String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
                                int shadowAngle = Integer.valueOf(shadowAngle_str);
                                date_font.setShadowAngle(shadowAngle);
                            } else if (isWeek) {
                                String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
                                int shadowWidth = Integer.valueOf(shadowWidth_str);
                                week_font.setShadowWidth(shadowWidth);
                                int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
                                int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                                week_font.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
                                String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
                                int shadowAngle = Integer.valueOf(shadowAngle_str);
                                week_font.setShadowAngle(shadowAngle);
                            }
                        } else if ("Pos".equals(parser.getName())) {
                            if (isDate) {
                                POS pos = new POS();
                                int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                                pos.setWidth(loc[0]);
                                pos.setHeight(loc[1]);
                                pos.setLeft(loc[2]);
                                pos.setTop(loc[3]);
                                dateView.setModule_Pos(pos);
                                dateView.setdPos(pos);
                            } else if (isWeek) {
                                POS pos = new POS();
                                int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                                pos.setWidth(loc[0]);
                                pos.setHeight(loc[1]);
                                pos.setLeft(loc[2]);
                                pos.setTop(loc[3]);
                                dateView.setwPos(pos);
                            }
                        } else if ("BGParam".equals(parser.getName())) {
                            if (isDate) {
                                date_bg = new BGParam();
                                int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                                if (bgType == 0) {
                                    date_bg.setType(BackType.PURECOLOR);
                                } else if (bgType == 1) {
                                    date_bg.setType(BackType.GRADIENTCOLOR);
                                } else if (bgType == 2) {
                                    date_bg.setType(BackType.PICTURE);
                                } else if (bgType == 3) {
                                    date_bg.setType(BackType.NOTSHOW);
                                }
                            } else if (isWeek) {
                                week_bg = new BGParam();
                                int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                                if (bgType == 0) {
                                    week_bg.setType(BackType.PURECOLOR);
                                } else if (bgType == 1) {
                                    week_bg.setType(BackType.GRADIENTCOLOR);
                                } else if (bgType == 2) {
                                    week_bg.setType(BackType.PICTURE);
                                } else if (bgType == 3) {
                                    week_bg.setType(BackType.NOTSHOW);
                                }
                            }
                        } else if ("Pure".equals(parser.getName())) {
                            if (isDate) {
                                int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                                int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                                date_bg.setPurecolor(initXmlColor(purecolor, purealpha));
                            } else if (isWeek) {
                                int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                                int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                                week_bg.setPurecolor(initXmlColor(purecolor, purealpha));
                            }
                        } else if ("Blend".equals(parser.getName())) {
                            if (isDate) {
                                int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                                if (colorType == 0) {
                                    date_bg.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                                } else {
                                    date_bg.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                                }
                                int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                                int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                                int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                                int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                                date_bg.setGradientcolor1(initXmlColor(color1, alpha1));
                                date_bg.setGradientcolor2(initXmlColor(color2, alpha2));
                            } else if (isWeek) {
                                int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                                if (colorType == 0) {
                                    week_bg.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                                } else {
                                    week_bg.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                                }
                                int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                                int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                                int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                                int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                                RGBA color = new RGBA();
                                week_bg.setGradientcolor1(initXmlColor(color1, alpha1));
                                week_bg.setGradientcolor2(initXmlColor(color2, alpha2));
                            }
                        } else if ("Picture".equals(parser.getName())) {
                            if (isDate) {
                                String bkfile_name = parser.getAttributeValue("", "File");
                                //设置图片路径
                                String dir = xmlFile.getParent();
                                dir = dir + "/" + bkfile_name;
                                File bkfile = new File(dir);
                                date_bg.setBkfile(bkfile);
                            } else if (isWeek) {
                                String bkfile_name = parser.getAttributeValue("", "File");
                                //设置图片路径
                                String dir = xmlFile.getParent();
                                dir = dir + "/" + bkfile_name;
                                File bkfile = new File(dir);
                                week_bg.setBkfile(bkfile);
                            }
                        } else if ("Effect".equals(parser.getName())) {
                            //                            dateView.setUpdateInterval(parser.getAttributeValue("", "UpdateInterval"));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("Week".equals(parser.getName())) {
                            isDate = false;
                            isWeek = false;
                        } else if ("Date".equals(parser.getName())) {
                            isDate = false;
                            isWeek = true;
                        } else if ("Font".equals(parser.getName()) && isDate) {
                            isFontStyle = false;
                            dateView.setDate_font(date_font);
                        } else if ("BGParam".equals(parser.getName()) && isDate) {
                            dateView.setBackgroud(date_bg);
                        } else if ("Font".equals(parser.getName()) && isWeek) {
                            isFontStyle = false;
                            dateView.setWeek_font(week_font);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取时间DATE模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return dateView;
    }

    //解析Time
    public TimeView ParseTime(String path) throws IOException {
        File xmlFile = new File(path);
        TimeView timeView = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            timeView = new TimeView();
            BGParam backgroud = null;
            FontParam fontParam = null;
            timeView.setModule_type(ConstantValue.MODULE_TYPE_TIME);
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            timeView.setModule_name(parser.getAttributeValue(0));
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            timeView.setModule_Pos(pos);
                        } else if ("BGParam".equals(parser.getName())) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                        } else if ("Pure".equals(parser.getName())) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName())) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName())) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            String dir = xmlFile.getParent();
                            dir = dir + "/" + bkfile_name;
                            File bkfile = new File(dir);
                            backgroud.setBkfile(bkfile);
                        } else if ("Font".equals(parser.getName())) {
                            fontParam = new FontParam();
                            String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                            fontParam.setName(name);
                            String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                            int size = Integer.valueOf(size_str);
                            fontParam.setSize((int) (size * Const.ScaleX));
                            String width_str = parser.getAttributeValue("", "Width"); // Width
                            int width = Integer.valueOf(width_str);
                            fontParam.setWidth(width);
                            String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                            int kerning = Integer.valueOf(kerning_str);
                            fontParam.setKerning(kerning);
                            String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                            int spacing = Integer.valueOf(spacing_str);
                            fontParam.setSpacing(spacing);
                        } else if ("Style".equals(parser.getName())) {
                            String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
                            int escapement = Integer.valueOf(escapement_str);
                            fontParam.setEscapement(escapement);
                            String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
                            int orientation = Integer.valueOf(orientation_str);
                            fontParam.setOrientation(orientation);
                            String bold_str = parser.getAttributeValue("", "Bold"); // Bold
                            int bold = Integer.valueOf(bold_str);
                            if (bold == 0) {
                                fontParam.setBold(false);
                            } else {
                                fontParam.setBold(true);
                            }
                            String italic_str = parser.getAttributeValue("", "Italic"); // Italic
                            int italic = Integer.valueOf(italic_str);
                            if (italic == 0) {
                                fontParam.setItalic(false);
                            } else {
                                fontParam.setItalic(true);
                            }
                            String underline_str = parser.getAttributeValue("", "Underline"); // Underline
                            int underline = Integer.valueOf(underline_str);
                            if (underline == 0) {
                                fontParam.setUnderline(false);
                            } else {
                                fontParam.setUnderline(true);
                            }
                        } else if ("Face".equals(parser.getName())) {
                            int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
                            int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
                            fontParam.setFaceColor(initXmlColor(fcolor, falpha));
                        } else if ("Edge".equals(parser.getName())) {
                            String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
                            int edgewidth = Integer.valueOf(edgewidth_str);
                            fontParam.setEdgewidth(edgewidth);
                            int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
                            int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
                        } else if ("Shadow".equals(parser.getName())) {
                            String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
                            int shadowWidth = Integer.valueOf(shadowWidth_str);
                            fontParam.setShadowWidth(shadowWidth);
                            int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
                            int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
                            String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
                            int shadowAngle = Integer.valueOf(shadowAngle_str);
                            fontParam.setShadowAngle(shadowAngle);
                        } else if ("DigitalClock".equals(parser.getName())) {
                            int show_second = PubUtil.parseInt(parser.getAttributeValue("", "ShowSecond"));
                            if (show_second == 0) {
                                timeView.setShow_second(false);
                            } else {
                                timeView.setShow_second(true);
                            }
                            int align = PubUtil.parseInt(parser.getAttributeValue("", "AlignType"));
                            if (align == 0) {
                                timeView.setAlign(AlignMode.MIDDLELEFT);
                            } else if (align == 1) {
                                timeView.setAlign(AlignMode.MIDDLERIGHT);
                            } else if (align == 2) {
                                timeView.setAlign(AlignMode.CENTER);
                            }
                            timeView.setTop_margin(PubUtil.parseInt(parser.getAttributeValue("", "MarginBottom")));
                            timeView.setLeft_margin(PubUtil.parseInt(parser.getAttributeValue("", "MarginH")));
                        } else if ("AnalogClock".equals(parser.getName())) {
                            //                            String anaClockFile_name = parser.getAttributeValue("", "File");
                            //                            String anaClockFile_path = ConstantValue.ROOT_DIR + "media/" + anaClockFile_name;
                            //                            File analogclock = new File(anaClockFile_path);
                            //                            timeView.setAnalogclock(analogclock);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("BGParam".equals(parser.getName())) {
                            timeView.setBackgroud(backgroud);
                        } else if ("Font".equals(parser.getName())) {
                            timeView.setFont(fontParam);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取时间TIME模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return timeView;
    }

    /**
     * 解析week模块
     */
    public WeekView parseWeek(String path) throws IOException {
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        WeekView weekView = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            BGParam backgroud = null;
            FontParam fontParam = null;
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            weekView = new WeekView();
                            weekView.setModule_type(ConstantValue.MODULE_TYPE_WEEK);
                            weekView.setModule_name(parser.getAttributeValue(0));
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weekView.setModule_Pos(pos);
                        } else if ("BGParam".equals(parser.getName())) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                        } else if ("Pure".equals(parser.getName())) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName())) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            RGBA color = new RGBA();
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName())) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            File bkfile = new File(bkfile_name);
                            backgroud.setBkfile(bkfile);
                        } else if ("Week".equals(parser.getName())) {
                            String week0 = parser.getAttributeValue("", "Week0");
                            String week1 = parser.getAttributeValue("", "Week1");
                            String week2 = parser.getAttributeValue("", "Week2");
                            String week3 = parser.getAttributeValue("", "Week3");
                            String week4 = parser.getAttributeValue("", "Week4");
                            String week5 = parser.getAttributeValue("", "Week5");
                            String week6 = parser.getAttributeValue("", "Week6");
                            HashMap<Integer, String> week = new HashMap<Integer, String>();
                            week.put(0, week0);
                            week.put(1, week1);
                            week.put(2, week2);
                            week.put(3, week3);
                            week.put(4, week4);
                            week.put(5, week5);
                            week.put(6, week6);
                            weekView.setWeekStrC(week);
                        } else if ("EnWeek".equals(parser.getName())) {
                            String week0 = parser.getAttributeValue("", "Week0");
                            String week1 = parser.getAttributeValue("", "Week1");
                            String week2 = parser.getAttributeValue("", "Week2");
                            String week3 = parser.getAttributeValue("", "Week3");
                            String week4 = parser.getAttributeValue("", "Week4");
                            String week5 = parser.getAttributeValue("", "Week5");
                            String week6 = parser.getAttributeValue("", "Week6");
                            HashMap<Integer, String> week = new HashMap<Integer, String>();
                            week.put(0, week0);
                            week.put(1, week1);
                            week.put(2, week2);
                            week.put(3, week3);
                            week.put(4, week4);
                            week.put(5, week5);
                            week.put(6, week6);
                            weekView.setWeekStrEn(week);
                        } else if ("Font".equals(parser.getName())) {
                            fontParam = new FontParam();
                            String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                            fontParam.setName(name);
                            String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                            int size = Integer.valueOf(size_str);
                            fontParam.setSize((int) (size * Const.ScaleX));
                            String width_str = parser.getAttributeValue("", "Width"); // Width
                            int width = Integer.valueOf(width_str);
                            fontParam.setWidth(width);
                            String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                            int kerning = Integer.valueOf(kerning_str);
                            fontParam.setKerning(kerning);
                            String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                            int spacing = Integer.valueOf(spacing_str);
                            fontParam.setSpacing(spacing);
                        } else if ("EnFont".equals(parser.getName())) {
                            fontParam = new FontParam();
                            String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                            fontParam.setName(name);
                            String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                            int size = Integer.valueOf(size_str);
                            fontParam.setSize((int) (size * Const.ScaleX));
                            String width_str = parser.getAttributeValue("", "Width"); // Width
                            int width = Integer.valueOf(width_str);
                            fontParam.setWidth(width);
                            String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                            int kerning = Integer.valueOf(kerning_str);
                            fontParam.setKerning(kerning);
                            String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                            int spacing = Integer.valueOf(spacing_str);
                            fontParam.setSpacing(spacing);
                        } else if ("Style".equals(parser.getName())) {
                            String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
                            int escapement = Integer.valueOf(escapement_str);
                            fontParam.setEscapement(escapement);
                            String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
                            int orientation = Integer.valueOf(orientation_str);
                            fontParam.setOrientation(orientation);
                            String bold_str = parser.getAttributeValue("", "Bold"); // Bold
                            int bold = Integer.valueOf(bold_str);
                            if (bold == 0) {
                                fontParam.setBold(false);
                            } else {
                                fontParam.setBold(true);
                            }
                            String italic_str = parser.getAttributeValue("", "Italic"); // Italic
                            int italic = Integer.valueOf(italic_str);
                            if (italic == 0) {
                                fontParam.setItalic(false);
                            } else {
                                fontParam.setItalic(true);
                            }
                            String underline_str = parser.getAttributeValue("", "Underline"); // Underline
                            int underline = Integer.valueOf(underline_str);
                            if (underline == 0) {
                                fontParam.setUnderline(false);
                            } else {
                                fontParam.setUnderline(true);
                            }
                        } else if ("Face".equals(parser.getName())) {
                            int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
                            int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
                            fontParam.setFaceColor(initXmlColor(fcolor, falpha));
                        } else if ("Edge".equals(parser.getName())) {
                            String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
                            int edgewidth = Integer.valueOf(edgewidth_str);
                            fontParam.setEdgewidth(edgewidth);
                            int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
                            int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
                        } else if ("Shadow".equals(parser.getName())) {
                            String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
                            int shadowWidth = Integer.valueOf(shadowWidth_str);
                            fontParam.setShadowWidth(shadowWidth);
                            int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
                            int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
                            String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
                            int shadowAngle = Integer.valueOf(shadowAngle_str);
                            fontParam.setShadowAngle(shadowAngle);
                        } else if ("Effect".equals(parser.getName())) {
                            int ShowMethod = PubUtil.parseInt(parser.getAttributeValue("", "ShowMethod"));
                            if (ShowMethod == 0) {
                                weekView.setShowFontType(ShowFontType.CHINESE);
                            } else if (ShowMethod == 1) {
                                weekView.setShowFontType(ShowFontType.ENGLISH);
                            } else if (ShowMethod == 2) {
                                weekView.setShowFontType(ShowFontType.DIGITAL);
                            }
                            int interval = PubUtil.parseInt(parser.getAttributeValue("", "ShowMethod"));
                            weekView.setInterval(interval);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("BGParam".equals(parser.getName())) {
                            weekView.setBackgroud(backgroud);
                        } else if ("Font".equals(parser.getName())) {
                            weekView.setFont(fontParam);
                        } else if ("EnFont".equals(parser.getName())) {
                            weekView.setFontEn(fontParam);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return weekView;
    }

    /**
     * 首末班车
     */
    public TrainInfoView ParseTrainInfo(String path) throws IOException {
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        TrainInfoView mtraininfoStrain = null;
        BGParam backgroud = null;
        FontParam fontParam = null;
        TrainInfoBean trainInfoBean = null;
        StationViewBean stationViewBean = null;
        TrainInfoItem trainInfoItem = null;
        TrainInfoData trainInfoData = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            trainInfoBean = new TrainInfoBean();
                            mtraininfoStrain = new TrainInfoView();
                            mtraininfoStrain.setModule_type(ConstantValue.MODULE_TYPE_TRAININFO);
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            trainInfoBean.setPos(pos);
                        } else if ("BGParam".equals(parser.getName())) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                        } else if ("Pure".equals(parser.getName())) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName())) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName())) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            File bkfile = new File(bkfile_name);
                            backgroud.setBkfile(bkfile);
                        } else if ("Up".equals(parser.getName())) {
                            trainInfoData = new TrainInfoData();
                            trainInfoData.setId(parser.getName());
                        } else if ("Down".equals(parser.getName())) {
                            trainInfoData = new TrainInfoData();
                            trainInfoData.setId(parser.getName());
                        } else if ("Terminate".equals(parser.getName())) {
                            trainInfoItem = new TrainInfoItem();
                            trainInfoItem.setIds(trainInfoData.getId() + parser.getName());
                        } else if ("FirstTrain".equals(parser.getName())) {
                            trainInfoItem = new TrainInfoItem();
                            trainInfoItem.setIds(trainInfoData.getId() + parser.getName());
                        } else if ("LastTrain".equals(parser.getName())) {
                            trainInfoItem = new TrainInfoItem();
                            trainInfoItem.setIds(trainInfoData.getId() + parser.getName());
                        } else if ("Title".equals(parser.getName())) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainInfoItem.getIds() + parser.getName());
                            stationViewBean.setAlignMode(setAlign(parser));
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                        } else if ("EnTitle".equals(parser.getName())) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainInfoItem.getIds() + parser.getName());
                            stationViewBean.setAlignMode(setAlign(parser));
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                        } else if ("Dest".equals(parser.getName())) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainInfoItem.getIds() + parser.getName());
                            stationViewBean.setAlignMode(setAlign(parser));
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt("");
                        } else if ("EnDest".equals(parser.getName())) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainInfoItem.getIds() + parser.getName());
                            stationViewBean.setAlignMode(setAlign(parser));
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt("");
                        } else if ("Time".equals(parser.getName())) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainInfoItem.getIds() + parser.getName());
                            stationViewBean.setAlignMode(setAlign(parser));
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt("");
                        } else if ("Font".equals(parser.getName())) {
                            fontParam = new FontParam();
                            setFont(fontParam, parser);
                        } else if ("Style".equals(parser.getName())) {
                            setStyle(fontParam, parser);
                        } else if ("Face".equals(parser.getName())) {
                            setFace(fontParam, parser);
                        } else if ("Edge".equals(parser.getName())) {
                            setEdge(fontParam, parser);
                        } else if ("Shadow".equals(parser.getName())) {
                            setShadow(fontParam, parser);
                        } else if ("Rect".equals(parser.getName())) {
                            POS pos = setRect(parser);
                            stationViewBean.setRectPos(pos);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("BGParam".equals(parser.getName())) {
                            trainInfoBean.setBgParam(backgroud);
                        } else if ("Terminate".equals(parser.getName())) {
                            trainInfoData.setTerminate(trainInfoItem);
                        } else if ("FirstTrain".equals(parser.getName())) {
                            trainInfoData.setFirstTrain(trainInfoItem);
                        } else if ("LastTrain".equals(parser.getName())) {
                            trainInfoData.setLastTrain(trainInfoItem);
                        } else if ("Title".equals(parser.getName())) {
                            trainInfoItem.setTitleView(stationViewBean);
                        } else if ("EnTitle".equals(parser.getName())) {
                            trainInfoItem.setTitleEnView(stationViewBean);
                        } else if ("Dest".equals(parser.getName())) {
                            trainInfoItem.setDestView(stationViewBean);
                        } else if ("EnDest".equals(parser.getName())) {
                            trainInfoItem.setDestEnView(stationViewBean);
                        } else if ("Time".equals(parser.getName())) {
                            trainInfoItem.setDestView(stationViewBean);
                            trainInfoItem.setDestEnView(stationViewBean);
                        } else if ("Font".equals(parser.getName())) {
                            stationViewBean.setFontParam(fontParam);
                        } else if ("Up".equals(parser.getName())) {
                            trainInfoBean.setUp(trainInfoData);
                        } else if ("Down".equals(parser.getName())) {
                            trainInfoBean.setDown(trainInfoData);
                        } else if ("System".equals(parser.getName())) {
                            mtraininfoStrain.setTrainInfoBean(trainInfoBean);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("解析首末班车模块错误", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return mtraininfoStrain;
    }

    /**
     * 解析视频模块
     */
    public LocalVideoView ParsePlayer(String path) throws IOException {
        LocalVideoView localVideoView = null;
        File xmlFile = new File(path);
        PlayerBean playerBean = new PlayerBean();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            localVideoView = new LocalVideoView();
            localVideoView.setModule_type(ConstantValue.MODULE_TYPE_VIDEO);
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            Log.e("--player--", parser.getName());
                            playerBean.setName(parser.getAttributeValue("", "Name"));
                            localVideoView.setModule_name(parser.getAttributeValue("", "Name"));
                            playerBean.setX(parser.getAttributeValue("", "x"));
                            playerBean.setY(parser.getAttributeValue("", "y"));
                            playerBean.setW(parser.getAttributeValue("", "w"));
                            playerBean.setH(parser.getAttributeValue("", "h"));
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            localVideoView.setModule_Pos(pos);
                            playerBean.setFullScreen(parser.getAttributeValue("", "FullScreen"));
                            playerBean.setChannel(parser.getAttributeValue("", "Channel"));
                            playerBean.setOutputMode(parser.getAttributeValue("", "OutputMode"));
                        } else if ("PlaySrc".equals(parser.getName())) {
                            LocalPlayItem localPlayItem = new LocalPlayItem();
                            playerBean.setType(parser.getAttributeValue("", "Type"));
                            String live = parser.getAttributeValue("", "Type");
                            if (live.equals("0")) {
                                localVideoView.setLive(false);
                            } else if (live.equals("1")) {
                                localVideoView.setLive(true);
                            }
                            playerBean.setLiveName(parser.getAttributeValue("", "LiveName"));
                            localVideoView.setLivename(parser.getAttributeValue("", "LiveName"));
                            playerBean.setFile(parser.getAttributeValue("", "File"));
                            String fileName = parser.getAttributeValue("", "File");
                            String dir = xmlFile.getParent();
                            String fileDir = dir + "/" + fileName;
                            File file = new File(fileDir);
                            if (!file.exists()) {
                                String utf8 = new String(fileName.getBytes("UTF-8"));
                                fileDir = "/sata/media/" + fileName;
                                file = new File(fileDir);
                            }
                            ArrayList<LocalPlayItem> localPlayItems = readMediaTxt(fileDir);
                            localVideoView.setLocal_list(localPlayItems);
                            Log.e("--fileDir--", fileDir);
                            localPlayItem.setFile(file);
                        } else if ("Volume".equals(parser.getName())) {
                            playerBean.setLeft(parser.getAttributeValue("", "Left"));
                            playerBean.setRight(parser.getAttributeValue("", "Right"));
                            int left = PubUtil.parseInt(parser.getAttributeValue("", "Left"));
                            int volume = left*255/100;
                            localVideoView.setVolume(volume);
                            localVideoView.setLive_volume(volume);
                            Const.AudioVolume = volume+"";
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取视频模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return localVideoView;
    }

    //读取文本文件
    public ArrayList<LocalPlayItem> readMediaTxt2(String path) {
        ArrayList<LocalPlayItem> localPlayItems = null;
        try {
            localPlayItems = new ArrayList<LocalPlayItem>();
            localPlayItems.clear();
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = mimeTypeLine;
                Log.e("--str--", str);
                LocalPlayItem localPlayItem = setMedia(str);
                localPlayItems.add(localPlayItem);
            }
        } catch (Exception e) {
            LogUtil.e("读取文本文件出错", e.toString());
        }
        return localPlayItems;
    }

    //读取文本文件
    public ArrayList<LocalPlayItem> readMediaTxt(String path) {
        ArrayList<LocalPlayItem> localPlayItems = null;
        try {
            localPlayItems = new ArrayList<LocalPlayItem>();
            localPlayItems.clear();
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "GBK");
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = mimeTypeLine;
                Log.e("--str--", str);
                LocalPlayItem localPlayItem = setMedia(str);
                localPlayItems.add(localPlayItem);
            }
        } catch (Exception e) {
            LogUtil.e("读取文本文件出错", e.toString());
        }
        return localPlayItems;
    }

    private LocalPlayItem setMedia(String str) {
        String regex = "\\s+";
        String strAry[] = str.split(regex);
        LocalPlayItem localPlayItem = null;
        if (strAry != null && strAry.length == 5) {
            Frame frame;
            Log.e("--垫片文件--", strAry[4]);
            localPlayItem = new LocalPlayItem();
            File file = null;
            String mpath = Const.VIDEOPATH + strAry[4];
            try {
                file = new File(mpath);
            } catch (Exception e) {
                LogUtil.e("读取视频文件出错", e.toString());
            }
            localPlayItem.setFile(file);
            frame = new Frame();
            initXmlFrame(strAry[1], frame);
            localPlayItem.setStart_frame(frame); // 开始播放时间

            frame = new Frame();
            initXmlFrame(strAry[2], frame);
            localPlayItem.setIn_point(frame); //

            frame = new Frame();
            initXmlFrame(strAry[3], frame);
            localPlayItem.setPlay_length(frame); // 开始播放时间
        }
        return localPlayItem;
    }

    //解析水平滚动条模块
    public ScrollView ParseCrawl(String path) throws IOException {
        File xmlFile = new File(path);
        ScrollView scrollInfo = new ScrollView();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            scrollInfo.setModule_type(ConstantValue.MODULE_TYPE_SCROLL); // 设置类型的标志位
            BGParam backgroud = null;
            FontParam fontParam = null;
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            scrollInfo.setModule_name(parser.getAttributeValue(0));
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            scrollInfo.setPos(pos);
                        } else if ("BGParam".equals(parser.getName())) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                        } else if ("Pure".equals(parser.getName())) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName())) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            RGBA color = new RGBA();
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName())) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            String dir = xmlFile.getParent();
                            dir = dir + "/" + bkfile_name;
                            File bkfile = new File(dir);
                            backgroud.setBkfile(bkfile);
                        } else if ("Font".equals(parser.getName())) {
                            fontParam = new FontParam();
                            String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                            fontParam.setName(name);
                            String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                            int size = Integer.valueOf(size_str);
                            fontParam.setSize((int) (size * Const.ScaleX));
                            String width_str = parser.getAttributeValue("", "Width"); // Width
                            int width = Integer.valueOf(width_str);
                            fontParam.setWidth(width);
                            String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                            int kerning = Integer.valueOf(kerning_str);
                            fontParam.setKerning(kerning);
                            String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                            int spacing = Integer.valueOf(spacing_str);
                            fontParam.setSpacing(spacing);
                        } else if ("Style".equals(parser.getName())) {
                            String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
                            int escapement = Integer.valueOf(escapement_str);
                            fontParam.setEscapement(escapement);
                            String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
                            int orientation = Integer.valueOf(orientation_str);
                            fontParam.setOrientation(orientation);
                            String bold_str = parser.getAttributeValue("", "Bold"); // Bold
                            int bold = Integer.valueOf(bold_str);
                            if (bold == 0) {
                                fontParam.setBold(false);
                            } else {
                                fontParam.setBold(true);
                            }
                            String italic_str = parser.getAttributeValue("", "Italic"); // Italic
                            int italic = Integer.valueOf(italic_str);
                            if (italic == 0) {
                                fontParam.setItalic(false);
                            } else {
                                fontParam.setItalic(true);
                            }
                            String underline_str = parser.getAttributeValue("", "Underline"); // Underline
                            int underline = Integer.valueOf(underline_str);
                            if (underline == 0) {
                                fontParam.setUnderline(false);
                            } else {
                                fontParam.setUnderline(true);
                            }
                        } else if ("Face".equals(parser.getName())) {
                            int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
                            int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
                            fontParam.setFaceColor(initXmlColor(fcolor, falpha));
                        } else if ("Edge".equals(parser.getName())) {
                            String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
                            int edgewidth = Integer.valueOf(edgewidth_str);
                            fontParam.setEdgewidth(edgewidth);
                            int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
                            int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
                        } else if ("Shadow".equals(parser.getName())) {
                            String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
                            int shadowWidth = Integer.valueOf(shadowWidth_str);
                            fontParam.setShadowWidth(shadowWidth);
                            int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
                            int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
                            String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
                            int shadowAngle = Integer.valueOf(shadowAngle_str);
                            fontParam.setShadowAngle(shadowAngle);
                        } else if ("Content".equals(parser.getName())) {
                            String fileName = parser.getAttributeValue("", "File");
                            String dir = xmlFile.getParent();
                            String fileDir = dir + "/" + fileName;
                            ArrayList text_list = new ArrayList<String>();
                            text_list.clear();
                            text_list = readTxtList(fileDir);
                            //读取/sata/config/Emerg.txt文件,判断是否有当前需要播放的滚动内容
                            if (!TextUtils.isEmpty(PubUtil.getContent(Const.EMERG_PATH))) {
                                String txt = PubUtil.getContent(Const.EMERG_PATH);
                                String[] split = txt.split("&");
                                ArrayList<String> msg = parseDBEmer(split[1]);
                                if (msg != null && msg.size() > 0) {
                                    text_list.clear();
                                    text_list = msg;
                                }
                            }
                            scrollInfo.setText_list(text_list);
                        } else if ("Effect".equals(parser.getName())) {
                            scrollInfo.setScroll_speed(PubUtil.parseInt(parser.getAttributeValue("", "Speed")));
                            String bottom = parser.getAttributeValue("", "BottomMargin");
                            scrollInfo.setTop_margin(PubUtil.parseInt(bottom));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("BGParam".equals(parser.getName())) {
                            scrollInfo.setBgParam(backgroud);
                        } else if ("Font".equals(parser.getName())) {
                            scrollInfo.setFont(fontParam);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取滚动模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return scrollInfo;
    }

    public ArrayList<String> readTxtList(String path) {
        ArrayList<String> txtList = null;
        try {
            txtList = new ArrayList<String>();
            txtList.clear();
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "GB2312");
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = mimeTypeLine;
                Log.e("--str--", str);
                txtList.add(str);
            }
        } catch (Exception e) {
            LogUtil.e("读取txt文本出错", e.toString());
        }
        return txtList;
    }

    //解析图片模块
    public MyImgView parseImg(String path) throws IOException {
        File xmlFile = new File(path);
        MyImgView imgView = new MyImgView();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            imgView = new MyImgView();
            imgView.setModule_type(ConstantValue.MODULE_TYPE_PIC);
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            imgView.setModule_type(ConstantValue.MODULE_TYPE_PIC); // 设置类型的标志位
                            POS pos = new POS();
                            int x = PubUtil.parseInt(parser.getAttributeValue("", "x"));
                            int y = PubUtil.parseInt(parser.getAttributeValue("", "y"));
                            int w = PubUtil.parseInt(parser.getAttributeValue("", "w"));
                            int h = PubUtil.parseInt(parser.getAttributeValue("", "h"));
                            pos.setLeft(x);
                            pos.setTop(y);
                            pos.setWidth(w);
                            pos.setHeight(h);
                            imgView.setModule_Pos(pos);
                        } else if ("Content".equals(parser.getName())) {
                            String file = parser.getAttributeValue("", "File");
                            String dir = xmlFile.getParent();
                            imgView.setFilePath(dir + "/" + file);
                        } else if ("Effect".equals(parser.getName())) {
                            imgView.setLogoSofton(parser.getAttributeValue("", "LogoSofton"));
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取图片模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return imgView;
    }

    //解析紧急消息
    public ExigentInfo parseEmcr(String path) throws IOException {
        ExigentInfo exigentInfo = null;
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            boolean isShowParam = false;
            boolean isShowParamFull = false;
            BGParam backgroud = null;
            FontParam fontParam = null;
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            exigentInfo = new ExigentInfo();
                            exigentInfo.setModule_type(ConstantValue.MODULE_TYPE_EXIGENT); // 设置类型的标志位
                            exigentInfo.setModule_name(parser.getAttributeValue("", "InfoTitle"));
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            exigentInfo.setPos(pos);
                        } else if ("ShowParam".equals(parser.getName())) {
                            isShowParam = true;
                            int show_type = PubUtil.parseInt(parser.getAttributeValue("", "Mode"));
                            if (show_type == 0) { //横滚
                                //                                exigentInfo.setShow_type(ShowType.LEFTSCROLL);
                            } else if (show_type == 1) { //纵滚
                                //                                exigentInfo.setShow_type(ShowType.UPSCROLL);
                            }
                        } else if ("BGParam".equals(parser.getName()) && isShowParam) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                            exigentInfo.setBgParam(backgroud);
                        } else if ("Pure".equals(parser.getName()) && isShowParam) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName()) && isShowParam) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            RGBA color = new RGBA();
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName()) && isShowParam) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            String dir = xmlFile.getParent();
                            dir = dir + "/" + bkfile_name;
                            File bkfile = new File(dir);
                            backgroud.setBkfile(bkfile);
                        } else if ("Font".equals(parser.getName()) && isShowParam) {
                            fontParam = new FontParam();
                            String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                            fontParam.setName(name);
                            String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                            int size = Integer.valueOf(size_str);
                            fontParam.setSize((int) (size * Const.ScaleX));
                            String width_str = parser.getAttributeValue("", "Width"); // Width
                            int width = Integer.valueOf(width_str);
                            fontParam.setWidth(width);
                            String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                            int kerning = Integer.valueOf(kerning_str);
                            fontParam.setKerning(kerning);
                            String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                            int spacing = Integer.valueOf(spacing_str);
                            fontParam.setSpacing(spacing);
                        } else if ("Style".equals(parser.getName()) && isShowParam) {
                            String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
                            int escapement = Integer.valueOf(escapement_str);
                            fontParam.setEscapement(escapement);
                            String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
                            int orientation = Integer.valueOf(orientation_str);
                            fontParam.setOrientation(orientation);
                            String bold_str = parser.getAttributeValue("", "Bold"); // Bold
                            int bold = Integer.valueOf(bold_str);
                            if (bold == 0) {
                                fontParam.setBold(false);
                            } else {
                                fontParam.setBold(true);
                            }
                            String italic_str = parser.getAttributeValue("", "Italic"); // Italic
                            int italic = Integer.valueOf(italic_str);
                            if (italic == 0) {
                                fontParam.setItalic(false);
                            } else {
                                fontParam.setItalic(true);
                            }
                            String underline_str = parser.getAttributeValue("", "Underline"); // Underline
                            int underline = Integer.valueOf(underline_str);
                            if (underline == 0) {
                                fontParam.setUnderline(false);
                            } else {
                                fontParam.setUnderline(true);
                            }
                        } else if ("Face".equals(parser.getName()) && isShowParam) {
                            int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
                            int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
                            fontParam.setFaceColor(initXmlColor(fcolor, falpha));
                        } else if ("Edge".equals(parser.getName()) && isShowParam) {
                            //                            <Edge Type="-1" Width="0" Color="13158600" Alpha="255" />
                            String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
                            int edgewidth = Integer.valueOf(edgewidth_str);
                            fontParam.setEdgewidth(edgewidth);
                            int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
                            int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
                        } else if ("Shadow".equals(parser.getName()) && isShowParam) {
                            //                            <Shadow Type="0" Width="0" Color="0" Alpha="255" Angle="315" Softness="4" />
                            String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
                            int shadowWidth = Integer.valueOf(shadowWidth_str);
                            fontParam.setShadowWidth(shadowWidth);
                            int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
                            int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
                            String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
                            int shadowAngle = Integer.valueOf(shadowAngle_str);
                            fontParam.setShadowAngle(shadowAngle);
                        } else if ("Crawl".equals(parser.getName()) && isShowParam) {
                            exigentInfo.setFont(fontParam);
                            //                            <Crawl> 横滚 间距单位为空格数
                            //                                    <Logo 开始图标 ShowStart="0" 结束图标 ShowEnd="0" 图标宽度 Width="30" 图标高度 Height="30" 图标间距 Space="1" 图标文件 File="" />
                            //                            <Effect 滚动速度 Speed="20" 滚动方式：计次，循环 CrawlMethod="1" 滚动次数 LoopNum="1" 行间距 LineSpace="4" 底边距 BottomMargin="10" />
                            //                            </Crawl>
                        } else if ("Effect".equals(parser.getName()) && isShowParam) {
                            int scroll_speed = PubUtil.parseInt(parser.getAttributeValue("", "Speed"));
                            exigentInfo.setScroll_speed(scroll_speed);
                        } else if ("Roll".equals(parser.getName()) && isShowParam) {
                            //Roll 纵滚  速度 Speed="10" 行间距 LineSpace="10" 左边距 LeftMargin="30" 右边距 RightMargin="30" />
                            int scroll_speed = PubUtil.parseInt(parser.getAttributeValue("", "Speed"));
//                            exigentInfo.setScroll_speed(scroll_speed);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("ShowParam".equals(parser.getName())) {
                            isShowParam = false;
                        } else if ("ShowParamFull".equals(parser.getName())) {
                            isShowParamFull = false;
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取紧急消息模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return exigentInfo;
    }


    //紧急消息解析两次，第一次返回局部参数，第二次返回全屏
    public ExigentInfo_Full parseExigent_full(String path) throws IOException {
        ExigentInfo_Full exigentInfo_full = null;
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            boolean isShowParam = false;
            boolean isShowParamFull = false;
            BGParam backgroud = null;
            FontParam fontParam = null;

            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            exigentInfo_full = new ExigentInfo_Full();
                            exigentInfo_full.setModule_type(ConstantValue.MODULE_TYPE_EXIGENT_FULL); // 设置类型的标志位
                            exigentInfo_full.setModule_name(parser.getAttributeValue("", "InfoTitle"));

                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            exigentInfo_full.setModule_Pos(pos);
                        } else if ("ShowParam".equals(parser.getName())) {
                            isShowParamFull = false;
                        } else if ("ShowParamFull".equals(parser.getName())) {
                            isShowParamFull = true;
                            int show_type = PubUtil.parseInt(parser.getAttributeValue("", "Mode"));
                            if (show_type == 0) { //横滚
                                exigentInfo_full.setShow_type(ShowType.LEFTSCROLL);
                            } else if (show_type == 1) { //纵滚
                                exigentInfo_full.setShow_type(ShowType.UPSCROLL);
                            } else if (show_type == 2) { //静止 切换
                                exigentInfo_full.setShow_type(ShowType.NOSCROLL);
                            }
                        } else if ("BGParam".equals(parser.getName()) && isShowParamFull) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                            exigentInfo_full.setBackgroud(backgroud);
                        } else if ("Pure".equals(parser.getName()) && isShowParamFull) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName()) && isShowParamFull) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            RGBA color = new RGBA();
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName()) && isShowParamFull) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            String dir = xmlFile.getParent();
                            dir = dir + "/" + bkfile_name;
                            File bkfile = new File(dir);
                            backgroud.setBkfile(bkfile);
                        } else if ("Font".equals(parser.getName()) && isShowParamFull) {
                            fontParam = new FontParam();
                            String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                            fontParam.setName(name);
                            String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                            int size = Integer.valueOf(size_str);
                            fontParam.setSize((int) (size * Const.ScaleX));
                            String width_str = parser.getAttributeValue("", "Width"); // Width
                            int width = Integer.valueOf(width_str);
                            fontParam.setWidth(width);
                            String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                            int kerning = Integer.valueOf(kerning_str);
                            fontParam.setKerning(kerning);
                            String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                            int spacing = Integer.valueOf(spacing_str);
                            fontParam.setSpacing(spacing);
                        } else if ("Style".equals(parser.getName()) && isShowParamFull) {
                            String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
                            int escapement = Integer.valueOf(escapement_str);
                            fontParam.setEscapement(escapement);
                            String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
                            int orientation = Integer.valueOf(orientation_str);
                            fontParam.setOrientation(orientation);
                            String bold_str = parser.getAttributeValue("", "Bold"); // Bold
                            int bold = Integer.valueOf(bold_str);
                            if (bold == 0) {
                                fontParam.setBold(false);
                            } else {
                                fontParam.setBold(true);
                            }
                            String italic_str = parser.getAttributeValue("", "Italic"); // Italic
                            int italic = Integer.valueOf(italic_str);
                            if (italic == 0) {
                                fontParam.setItalic(false);
                            } else {
                                fontParam.setItalic(true);
                            }
                            String underline_str = parser.getAttributeValue("", "Underline"); // Underline
                            int underline = Integer.valueOf(underline_str);
                            if (underline == 0) {
                                fontParam.setUnderline(false);
                            } else {
                                fontParam.setUnderline(true);
                            }
                        } else if ("Face".equals(parser.getName()) && isShowParamFull) {
                            int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
                            int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
                            fontParam.setFaceColor(initXmlColor(fcolor, falpha));
                        } else if ("Edge".equals(parser.getName()) && isShowParamFull) {
                            //                            <Edge Type="-1" Width="0" Color="13158600" Alpha="255" />
                            String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
                            int edgewidth = Integer.valueOf(edgewidth_str);
                            fontParam.setEdgewidth(edgewidth);
                            int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
                            int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
                        } else if ("Shadow".equals(parser.getName()) && isShowParamFull) {
                            //                            <Shadow Type="0" Width="0" Color="0" Alpha="255" Angle="315" Softness="4" />
                            String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
                            int shadowWidth = Integer.valueOf(shadowWidth_str);
                            fontParam.setShadowWidth(shadowWidth);
                            int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
                            int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
                            String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
                            int shadowAngle = Integer.valueOf(shadowAngle_str);
                            fontParam.setShadowAngle(shadowAngle);
                        } else if ("Crawl".equals(parser.getName()) && isShowParamFull) {
                            exigentInfo_full.setFont(fontParam);
                        } else if ("Effect".equals(parser.getName()) && isShowParamFull) {
                            int scroll_speed = PubUtil.parseInt(parser.getAttributeValue("", "Speed"));
                            exigentInfo_full.setScroll_speed(scroll_speed);
                        } else if ("Roll".equals(parser.getName()) && isShowParamFull) {
                            int scroll_speed = PubUtil.parseInt(parser.getAttributeValue("", "Speed"));
//                            exigentInfo_full.setScroll_speed(scroll_speed);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("ShowParam".equals(parser.getName())) {
                            isShowParamFull = false;
                        } else if ("ShowParamFull".equals(parser.getName())) {
                            isShowParamFull = false;
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取全屏紧急消息模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return exigentInfo_full;
    }

    private RGBA initXmlColor(int purecolor, int purealpha) {
        int red = purecolor & 255;
        int green = purecolor >> 8 & 255;
        int blue = purecolor >> 16 & 255;
        RGBA rgba = new RGBA();
        rgba.setRed(red);
        rgba.setBlue(blue);
        rgba.setGreen(green);
        rgba.setAlpha(purealpha);

        return rgba;
    }

    private void initXmlFrame(String frame_str, Frame time_frame) {
        int hour = 0;
        int minute = 0;
        int sec = 0;
        int frame = 0;
        if (!TextUtils.isEmpty(frame_str)) {
            String[] split_time = frame_str.split(":");
            hour = Integer.valueOf(split_time[0]);
            minute = Integer.valueOf(split_time[1]);
            sec = Integer.valueOf(split_time[2]);
            frame = Integer.valueOf(split_time[3]);
        }
        time_frame.setHour(hour);
        time_frame.setMinute(minute);
        time_frame.setSec(sec);
        time_frame.setFrame(frame);
    }

    /**
     * 车载ATS
     *
     * @param path
     * @return
     * @throws IOException
     */
    public AtsStrainView parseAtsStrain(String path) throws IOException {
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        AtsStrainView mAtsStrain = null;
        BGParam backgroud = null;
        FontParam fontParam = null;
        SystemBean systemBean = null;
        boolean nextStation = false, dstStation = false;
        TrainItem trainItem = null;
        StationViewBean stationViewBean = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            systemBean = new SystemBean();
                            mAtsStrain = new AtsStrainView();
                            mAtsStrain.setModule_type(ConstantValue.MODULE_TYPE_ATS_STRAIN);
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            systemBean.setPos(pos);
                        } else if ("BGParam".equals(parser.getName())) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                        } else if ("Pure".equals(parser.getName())) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName())) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName())) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            String dir = xmlFile.getParent();
                            dir = dir + "/" + bkfile_name;
                            File bkfile = new File(dir);
                            backgroud.setBkfile(bkfile);
                        } else if ("DstStation".equals(parser.getName())) {
                            dstStation = true;
                            trainItem = new TrainItem();
                            trainItem.setIds(parser.getName());
                        } else if ("NextStation".equals(parser.getName())) {
                            nextStation = true;
                            trainItem = new TrainItem();
                            trainItem.setIds(parser.getName());
                        } else if (parser.getName().equals("Title")) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("Info")) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                            stationViewBean.setTxt("");
                        } else if (parser.getName().equals("TitleEn")) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());

                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("InfoEn")) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if ("Font".equals(parser.getName())) {
                            fontParam = new FontParam();
                            setFont(fontParam, parser);
                        } else if ("Style".equals(parser.getName())) {
                            setStyle(fontParam, parser);
                        } else if ("Face".equals(parser.getName())) {
                            setFace(fontParam, parser);
                        } else if ("Edge".equals(parser.getName())) {
                            setEdge(fontParam, parser);
                        } else if ("Shadow".equals(parser.getName())) {
                            setShadow(fontParam, parser);
                        } else if ("Rect".equals(parser.getName())) {
                            POS pos = setRect(parser);
                            stationViewBean.setRectPos(pos);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("BGParam".equals(parser.getName())) {
                            systemBean.setBgParam(backgroud);
                        } else if ("DstStation".equals(parser.getName())) {
                            dstStation = false;
                            systemBean.setStrainDstStation(trainItem);
                        } else if ("NextStation".equals(parser.getName())) {
                            nextStation = false;
                            systemBean.setStrainNextStation(trainItem);
                        } else if (parser.getName().equals("Title")) {
                            trainItem.setTitleView(stationViewBean);
                        } else if (parser.getName().equals("Info")) {
                            trainItem.setInfoView(stationViewBean);
                        } else if (parser.getName().equals("TitleEn")) {
                            trainItem.setTitleEnView(stationViewBean);
                        } else if (parser.getName().equals("InfoEn")) {
                            trainItem.setInfoEnView(stationViewBean);
                        } else if ("BGParam".equals(parser.getName())) {
                            systemBean.setBgParam(backgroud);
                        } else if ("Font".equals(parser.getName())) {
                            stationViewBean.setFontParam(fontParam);
                        } else if ("System".equals(parser.getName())) {
                            mAtsStrain.setSystemBean(systemBean);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取车载ATS模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return mAtsStrain;
    }

    public AtsStationView parseStation(String path) throws IOException {
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        BGParam backgroud = null;
        FontParam fontParam = null;
        boolean curStation = false;
        boolean time = false, next = false, dst = false;
        String TrainName = "";
        SystemBean systemBean = null;
        TrainData trainData = null;
        TrainItem trainItem = null;
        StationViewBean stationViewBean = null;
        AtsStationView atsStationView = new AtsStationView();
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            systemBean = new SystemBean();
                            atsStationView.setModule_type(ConstantValue.MODULE_TYPE_ATS_STATION);
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            atsStationView.setModule_Pos(pos);
                            systemBean.setPos(pos);
                        } else if ("BGParam".equals(parser.getName())) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                        } else if ("Pure".equals(parser.getName())) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName())) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName())) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            String dir = xmlFile.getParent();
                            dir = dir + "/" + bkfile_name;
                            File bkfile = new File(dir);
                            backgroud.setBkfile(bkfile);
                        } else if (parser.getName().startsWith("CurStation")) {
                            curStation = true;
                            trainItem = new TrainItem();
                            trainItem.setIds(parser.getName());
                        } else if (parser.getName().startsWith("Train")) {
                            TrainName = parser.getName();
                            trainData = new TrainData();
                            if (TrainName.endsWith("0")) {
                                trainData.setId(parser.getName());
                            } else if (TrainName.endsWith("1")) {
                                trainData.setId(parser.getName());
                            } else if (TrainName.endsWith("2")) {
                                trainData.setId(parser.getName());
                            } else if (TrainName.endsWith("3")) {
                                trainData.setId(parser.getName());
                            }
                        } else if (parser.getName().startsWith("Time") && !curStation) {
                            time = true;
                            trainItem = new TrainItem();
                            trainItem.setIds(trainData.getId() + parser.getName());
                        } else if (parser.getName().startsWith("Next") && !curStation) {
                            next = true;
                            trainItem = new TrainItem();
                            trainItem.setIds(trainData.getId() + parser.getName());
                        } else if (parser.getName().startsWith("Dst") && !curStation) {
                            dst = true;
                            trainItem = new TrainItem();
                            trainItem.setIds(trainData.getId() + parser.getName());
                        } else if (parser.getName().equals("Title") && !curStation && time) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("Info") && !curStation && time) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                            stationViewBean.setTxt("");
                        } else if (parser.getName().equals("TitleEn") && !curStation && time) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("InfoEn") && !curStation && time) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                            stationViewBean.setTxt("");
                        } else if (parser.getName().equals("Title") && !curStation && next) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("Info") && !curStation && next) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                            stationViewBean.setTxt("");
                        } else if (parser.getName().equals("TitleEn") && !curStation && next) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("InfoEn") && !curStation && next) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                            stationViewBean.setTxt("");
                        } else if (parser.getName().equals("Title") && !curStation && dst) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("Info") && !curStation && dst) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                            stationViewBean.setTxt("");
                        } else if (parser.getName().equals("TitleEn") && !curStation && dst) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("InfoEn") && !curStation && dst) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                            stationViewBean.setTxt("");
                        } else if ("Font".equals(parser.getName())) {
                            fontParam = new FontParam();
                            setFont(fontParam, parser);
                        } else if ("Style".equals(parser.getName())) {
                            setStyle(fontParam, parser);
                        } else if ("Face".equals(parser.getName())) {
                            setFace(fontParam, parser);
                        } else if ("Edge".equals(parser.getName())) {
                            setEdge(fontParam, parser);
                        } else if ("Shadow".equals(parser.getName())) {
                            setShadow(fontParam, parser);
                        } else if ("Rect".equals(parser.getName())) {
                            POS pos = setRect(parser);
                            stationViewBean.setRectPos(pos);
                        } else if (parser.getName().equals("Title") && curStation) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("Info") && curStation) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                            stationViewBean.setTxt("");
                        } else if (parser.getName().equals("TitleEn") && curStation) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setTxt(parser.getAttributeValue("", "Name"));
                            stationViewBean.setAlignMode(setAlign(parser));
                        } else if (parser.getName().equals("InfoEn") && curStation) {
                            stationViewBean = new StationViewBean();
                            stationViewBean.setIds(trainItem.getIds() + parser.getName());
                            int show = PubUtil.parseInt(parser.getAttributeValue("", "Show"));
                            if (show == 1) {
                                stationViewBean.setShow(true);
                            } else {
                                stationViewBean.setShow(false);
                            }
                            stationViewBean.setAlignMode(setAlign(parser));
                            stationViewBean.setTxt("");
                        } else if ("FontNum".equals(parser.getName())) {
                            fontParam = new FontParam();
                            setFont(fontParam, parser);
                        } else if ("FontTrans".equals(parser.getName())) {
                            fontParam = new FontParam();
                            setFont(fontParam, parser);
                        } else if ("FontTransEn".equals(parser.getName())) {
                            fontParam = new FontParam();
                            setFont(fontParam, parser);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("Train0")) {
                            systemBean.setTrain0(trainData);
                        } else if (parser.getName().equals("Train1")) {
                            systemBean.setTrain1(trainData);
                        } else if (parser.getName().equals("Train2")) {
                            systemBean.setTrain2(trainData);
                        } else if (parser.getName().equals("Train3")) {
                            systemBean.setTrain3(trainData);
                        } else if (parser.getName().startsWith("Time")) {
                            time = false;
                            trainData.setTimeData(trainItem);
                        } else if (parser.getName().startsWith("Next")) {
                            next = false;
                            trainData.setNextData(trainItem);
                        } else if (parser.getName().startsWith("Dst")) {
                            dst = false;
                            trainData.setDstData(trainItem);
                        } else if (parser.getName().equals("Title")) {
                            trainItem.setTitleView(stationViewBean);
                        } else if (parser.getName().equals("Info")) {
                            trainItem.setInfoView(stationViewBean);
                        } else if (parser.getName().equals("TitleEn")) {
                            trainItem.setTitleEnView(stationViewBean);
                        } else if (parser.getName().equals("InfoEn")) {
                            trainItem.setInfoEnView(stationViewBean);
                        } else if ("BGParam".equals(parser.getName())) {
                            systemBean.setBgParam(backgroud);
                        } else if ("Font".equals(parser.getName())) {
                            stationViewBean.setFontParam(fontParam);
                        } else if (parser.getName().startsWith("CurStation")) {
                            curStation = false;
                            systemBean.setCurStation(trainItem);
                        } else if ("FontNum".equals(parser.getName())) {
                            trainData.setFontNum(fontParam);
                        } else if ("FontTrans".equals(parser.getName())) {
                            trainData.setFontTrans(fontParam);
                        } else if ("FontTransEn".equals(parser.getName())) {
                            trainData.setFontTransEn(fontParam);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取车站ATS模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        atsStationView.setSystemBean(systemBean);
        return atsStationView;
    }

    //解析advertisement广告模块
    public AdvertisementView parseAdvertisement(String path) throws IOException {
        AdvertisementView advertisementView = null;
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            advertisementView = new AdvertisementView();
            advertisementView.setModule_type(ConstantValue.MODULE_TYPE_ADVERTISEMENT);
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            advertisementView.setModule_name(parser.getAttributeValue("", "Name"));
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            advertisementView.setModule_Pos(pos);
                        } else if ("Effect".equals(parser.getName())) {
                            //                            <Effect Type="0" Duration="5000" Direction="0" DisplayMethod="0" LogoSofton="1" />
                            int stype = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            int duration = PubUtil.parseInt(parser.getAttributeValue("", "Duration"));
                            advertisementView.setDuration(duration);
                            //硬切，百叶窗，马赛克，滚动，淡入，划向，栅条
                            if (stype == 0) {
                                advertisementView.setStype(ImgChange.Hardcut);
                            } else if (stype == 1) {
                                advertisementView.setStype(ImgChange.louver);
                            } else if (stype == 2) {
                                advertisementView.setStype(ImgChange.mosaic);
                            } else if (stype == 3) {
                                advertisementView.setStype(ImgChange.roll);
                            } else if (type == 4) {
                                advertisementView.setStype(ImgChange.fade);
                            } else if (type == 5) {
                                advertisementView.setStype(ImgChange.stroke);
                            } else if (type == 6) {
                                advertisementView.setStype(ImgChange.gridbar);
                            }
                            //解析ad.txt文本
                            String fileName = "ad.txt";
                            String dir = xmlFile.getParent();
                            String fileDir = dir + "/" + fileName;
                            File file = new File(fileDir);
                            if (file.exists()) {
                                ArrayList<String> adlist = new ArrayList<String>();
                                adlist.clear();
                                adlist = readAdTxt(dir, fileDir);
                                advertisementView.setImgPaths(adlist);
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取广告模块出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return advertisementView;
    }

    /**
     * 解析天气预报组播端口
     */
    public void getWeather() throws IOException {
        File xmlFile = null;
        FileInputStream inputStream = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            xmlFile = new File(Const.WEATHERPATH);
            inputStream = new FileInputStream(xmlFile);
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while ((type != XmlPullParser.END_DOCUMENT)) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("WEATHER".equals(parser.getName())) {
                            Const.WeatherIp = parser.getAttributeValue("", "MCIP");
                            Const.WeatherPort = PubUtil.parseInt(parser.getAttributeValue("", "MCPort"));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("解析天气预报地址错误", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 天气预报模块
     */
    public WeatherView parseWeather(String path) throws IOException {
        File xmlFile = new File(path);
        FileInputStream inputStream = null;
        BGParam backgroud = null;
        FontParam fontParam = null;
        WeatherView weatherView = null;
        String bgWidth = "";
        String bgHeight = "";
        String zoneWidth = "";
        String zoneHeight = "";
        try {
            inputStream = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("System".equals(parser.getName())) {
                            weatherView = new WeatherView();
                            weatherView.setModule_type(ConstantValue.MODULE_TYPE_WEATHER);
                            weatherView.setModule_name(parser.getAttributeValue(0));
                            bgWidth = parser.getAttributeValue("", "w");
                            bgHeight = parser.getAttributeValue("", "h");
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setModule_Pos(pos);
                        } else if ("BGParam".equals(parser.getName())) {
                            backgroud = new BGParam();
                            int bgType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (bgType == 0) {
                                backgroud.setType(BackType.PURECOLOR);
                            } else if (bgType == 1) {
                                backgroud.setType(BackType.GRADIENTCOLOR);
                            } else if (bgType == 2) {
                                backgroud.setType(BackType.PICTURE);
                            } else if (bgType == 3) {
                                backgroud.setType(BackType.NOTSHOW);
                            }
                        } else if ("Pure".equals(parser.getName())) {
                            int purecolor = PubUtil.parseInt(parser.getAttributeValue("", "Color"));
                            int purealpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            backgroud.setPurecolor(initXmlColor(purecolor, purealpha));
                        } else if ("Blend".equals(parser.getName())) {
                            int colorType = PubUtil.parseInt(parser.getAttributeValue("", "Type"));
                            if (colorType == 0) {
                                backgroud.setColorType(GradientColorType.HORIZONTALGRADIENT); // 水平渐变
                            } else {
                                backgroud.setColorType(GradientColorType.VERTICALGRADIENT); // 垂直渐变
                            }
                            int color1 = PubUtil.parseInt(parser.getAttributeValue("", "Color1"));
                            int color2 = PubUtil.parseInt(parser.getAttributeValue("", "Color2"));
                            int alpha1 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha1"));
                            int alpha2 = PubUtil.parseInt(parser.getAttributeValue("", "Alpha2"));
                            RGBA color = new RGBA();
                            backgroud.setGradientcolor1(initXmlColor(color1, alpha1));
                            backgroud.setGradientcolor2(initXmlColor(color2, alpha2));
                        } else if ("Picture".equals(parser.getName())) {
                            String bkfile_name = parser.getAttributeValue("", "File");
                            //设置图片路径
                            String dir = xmlFile.getParent();
                            dir = dir + "/" + bkfile_name;
                            File bkfile = new File(dir);
                            backgroud.setBkfile(bkfile);
                        } else if ("WeatherLogo".equals(parser.getName())) {
                            POS pos = new POS();
                            //                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setWeatherPos(pos);
                        } else if ("Temperature".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(bgWidth), PubUtil.parseInt(bgHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "yright")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setTemperaturePos(pos);
                        } else if ("Humidity".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(bgWidth), PubUtil.parseInt(bgHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setHumidityPos(pos);
                        } else if ("Wind".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(bgWidth), PubUtil.parseInt(bgHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setWindPos(pos);
                        } else if ("WarningZone".equals(parser.getName())) {
                            zoneWidth = parser.getAttributeValue("", "w");
                            zoneHeight = parser.getAttributeValue("", "h");
                        } else if ("Zone1Rect".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(zoneWidth), PubUtil.parseInt(zoneHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setZone1Rect(pos);
                        } else if ("Zone1LogoRect".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(zoneWidth), PubUtil.parseInt(zoneHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setZone1Logo(pos);
                        } else if ("Zone2Rect".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(zoneWidth), PubUtil.parseInt(zoneHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setZone2Rect(pos);
                        } else if ("Zone2LogoRect".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(zoneWidth), PubUtil.parseInt(zoneHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setZone2Logo(pos);
                        } else if ("Zone3Rect".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(zoneWidth), PubUtil.parseInt(zoneHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setZone3Rect(pos);
                        } else if ("Zone3LogoRect".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(zoneWidth), PubUtil.parseInt(zoneHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setZone3Logo(pos);
                        } else if ("Zone4Rect".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(zoneWidth), PubUtil.parseInt(zoneHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setZone4Rect(pos);
                        } else if ("Zone4LogoRect".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation2(PubUtil.parseInt(zoneWidth), PubUtil.parseInt(zoneHeight), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setZone4Logo(pos);
                        } else if ("MetroLogo".equals(parser.getName())) {
                            POS pos = new POS();
                            int loc[] = PubUtil.getLocation(PubUtil.parseInt(parser.getAttributeValue("", "w")), PubUtil.parseInt(parser.getAttributeValue("", "h")), PubUtil.parseInt(parser.getAttributeValue("", "x")), PubUtil.parseInt(parser.getAttributeValue("", "y")));
                            pos.setWidth(loc[0]);
                            pos.setHeight(loc[1]);
                            pos.setLeft(loc[2]);
                            pos.setTop(loc[3]);
                            weatherView.setMetroLogo(pos);
                        } else if ("exParam".equals(parser.getName())) {
                            int alarmLogoNum = PubUtil.parseInt(parser.getAttributeValue("", "AlarmLogoNum"));
                            int swtichParam = PubUtil.parseInt(parser.getAttributeValue("", "SwtichParam"));
                            int weatherOffLine = PubUtil.parseInt(parser.getAttributeValue("", "WeatherOffLine"));
                            int warningOffLine = PubUtil.parseInt(parser.getAttributeValue("", "WarningOffLine"));
                            weatherView.setAlarmLogoNum(alarmLogoNum);
                            weatherView.setSwtichParam(swtichParam);
                            weatherView.setWeatherOffLine(weatherOffLine * 60 * 1000);
                            weatherView.setWarningOffLine(warningOffLine * 60 * 1000);
                            Const.WeatherOffLine = weatherOffLine * 60 * 1000;
                            Const.WarningOffLine = warningOffLine * 60 * 1000;
                        } else if ("Font".equals(parser.getName())) {
                            fontParam = new FontParam();
                            String name = parser.getAttributeValue("", "Name"); // Name="宋体"
                            fontParam.setName(name);
                            String size_str = parser.getAttributeValue("", "Size"); // Size="40"
                            int size = Integer.valueOf(size_str);
                            fontParam.setSize((int) (size * Const.ScaleX));
                            String width_str = parser.getAttributeValue("", "Width"); // Width
                            int width = Integer.valueOf(width_str);
                            fontParam.setWidth(width);
                            String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
                            int kerning = Integer.valueOf(kerning_str);
                            fontParam.setKerning(kerning);
                            String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
                            int spacing = Integer.valueOf(spacing_str);
                            fontParam.setSpacing(spacing);
                        } else if ("Style".equals(parser.getName())) {
                            String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
                            int escapement = Integer.valueOf(escapement_str);
                            fontParam.setEscapement(escapement);
                            String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
                            int orientation = Integer.valueOf(orientation_str);
                            fontParam.setOrientation(orientation);
                            String bold_str = parser.getAttributeValue("", "Bold"); // Bold
                            int bold = Integer.valueOf(bold_str);
                            if (bold == 0) {
                                fontParam.setBold(false);
                            } else {
                                fontParam.setBold(true);
                            }
                            String italic_str = parser.getAttributeValue("", "Italic"); // Italic
                            int italic = Integer.valueOf(italic_str);
                            if (italic == 0) {
                                fontParam.setItalic(false);
                            } else {
                                fontParam.setItalic(true);
                            }
                            String underline_str = parser.getAttributeValue("", "Underline"); // Underline
                            int underline = Integer.valueOf(underline_str);
                            if (underline == 0) {
                                fontParam.setUnderline(false);
                            } else {
                                fontParam.setUnderline(true);
                            }
                        } else if ("Face".equals(parser.getName())) {
                            int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
                            int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
                            fontParam.setFaceColor(initXmlColor(fcolor, falpha));
                        } else if ("Edge".equals(parser.getName())) {
                            String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
                            int edgewidth = Integer.valueOf(edgewidth_str);
                            fontParam.setEdgewidth(edgewidth);
                            int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
                            int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
                        } else if ("Shadow".equals(parser.getName())) {
                            String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
                            int shadowWidth = Integer.valueOf(shadowWidth_str);
                            fontParam.setShadowWidth(shadowWidth);
                            int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
                            int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
                            fontParam.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
                            String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
                            int shadowAngle = Integer.valueOf(shadowAngle_str);
                            fontParam.setShadowAngle(shadowAngle);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("BGParam".equals(parser.getName())) {
                            weatherView.setBackgroud(backgroud);
                        } else if ("Temperature".equals(parser.getName())) {
                            weatherView.setTemperatureFont(fontParam);
                        } else if ("Humidity".equals(parser.getName())) {
                            weatherView.setHumidityFont(fontParam);
                        } else if ("Wind".equals(parser.getName())) {
                            weatherView.setWindFont(fontParam);
                        } else if ("WarningZone".equals(parser.getName())) {
                            weatherView.setZoneFont(fontParam);
                        }
                        break;
                }
                type = parser.next();
            }
        } catch (Exception e) {
            LogUtil.e("读取天气预报出错", e.toString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return weatherView;
    }

    //读取文本文件但
    public ArrayList<String> readAdTxt(String parentPath, String path) {
        ArrayList<String> imgfilelist = null;
        try {
            imgfilelist = new ArrayList<String>();
            imgfilelist.clear();
            File urlFile = new File(path);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "GB2312");
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = mimeTypeLine;

                String filename = setAd(str);
                if (filename != null) {
                    imgfilelist.add(parentPath + "/" + filename);
                }
            }
        } catch (Exception e) {
            LogUtil.e("读取广告图片路径出错", e.toString());
        }
        Log.e("--ad--", imgfilelist.toString());
        return imgfilelist;
    }

    private String setAd(String str) {
        String regex = "\\s+";
        String strAry[] = str.split(regex);
        String filename = null;
        if (strAry != null && strAry.length == 4) {
            filename = strAry[1];
        }
        return filename;
    }

    private void setFont(FontParam fontParam, XmlPullParser parser) {
        String name = parser.getAttributeValue("", "Name"); // Name="宋体"
        fontParam.setName(name);
        String size_str = parser.getAttributeValue("", "Size"); // Size="40"
        int size = Integer.valueOf(size_str);
        fontParam.setSize((int) (size * Const.ScaleX));
        String width_str = parser.getAttributeValue("", "Width"); // Width
        int width = Integer.valueOf(width_str);
        fontParam.setWidth(width);
        String kerning_str = parser.getAttributeValue("", "Kerning"); // Kerning
        int kerning = Integer.valueOf(kerning_str);
        fontParam.setKerning(kerning);
        String spacing_str = parser.getAttributeValue("", "Leading"); // Spacing
        int spacing = Integer.valueOf(spacing_str);
        fontParam.setSpacing(spacing);
    }

    private void setStyle(FontParam fontParam, XmlPullParser parser) {
        String escapement_str = parser.getAttributeValue("", "Slant"); // Escapement
        int escapement = Integer.valueOf(escapement_str);
        fontParam.setEscapement(escapement);
        String orientation_str = parser.getAttributeValue("", "Rotation"); // Orientation
        int orientation = Integer.valueOf(orientation_str);
        fontParam.setOrientation(orientation);
        String bold_str = parser.getAttributeValue("", "Bold"); // Bold
        int bold = Integer.valueOf(bold_str);
        if (bold == 0) {
            fontParam.setBold(false);
        } else {
            fontParam.setBold(true);
        }
        String italic_str = parser.getAttributeValue("", "Italic"); // Italic
        int italic = Integer.valueOf(italic_str);
        if (italic == 0) {
            fontParam.setItalic(false);
        } else {
            fontParam.setItalic(true);
        }
        String underline_str = parser.getAttributeValue("", "Underline"); // Underline
        int underline = Integer.valueOf(underline_str);
        if (underline == 0) {
            fontParam.setUnderline(false);
        } else {
            fontParam.setUnderline(true);
        }
    }

    private void setFace(FontParam fontParam, XmlPullParser parser) {
        int fcolor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // FaceColor
        int falpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha")); // FaceColor
        fontParam.setFaceColor(initXmlColor(fcolor, falpha));
    }

    private void setEdge(FontParam fontParam, XmlPullParser parser) {
        String edgewidth_str = parser.getAttributeValue("", "Width"); // EdgeWidth
        int edgewidth = Integer.valueOf(edgewidth_str);
        fontParam.setEdgewidth(edgewidth);
        int edgeColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // EdgeColor
        int edgeAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
        fontParam.setEdgeColor(initXmlColor(edgeColor, edgeAlpha));
    }

    private void setShadow(FontParam fontParam, XmlPullParser parser) {
        String shadowWidth_str = parser.getAttributeValue("", "Width"); // ShadowWidth
        int shadowWidth = Integer.valueOf(shadowWidth_str);
        fontParam.setShadowWidth(shadowWidth);
        int shadowColor = PubUtil.parseInt(parser.getAttributeValue("", "Color")); // ShadowColor
        int shadowAlpha = PubUtil.parseInt(parser.getAttributeValue("", "Alpha"));
        fontParam.setShadowColor(initXmlColor(shadowColor, shadowAlpha));
        String shadowAngle_str = parser.getAttributeValue("", "Angle"); // ShadowAngle
        int shadowAngle = Integer.valueOf(shadowAngle_str);
        fontParam.setShadowAngle(shadowAngle);
    }

    private POS setRect(XmlPullParser parser) {
        POS pos = new POS();
        int left = PubUtil.parseInt(parser.getAttributeValue("", "left"));
        int right = PubUtil.parseInt(parser.getAttributeValue("", "right"));
        int top = PubUtil.parseInt(parser.getAttributeValue("", "top"));
        int bottom = PubUtil.parseInt(parser.getAttributeValue("", "bottom"));
        int loc[] = PubUtil.getLocation(right - left, bottom - top, left, top);
        pos.setWidth(loc[0]);
        pos.setHeight(loc[1]);
        pos.setLeft(loc[2]);
        pos.setTop(loc[3]);
        return pos;
    }

    private static AlignMode setAlign(XmlPullParser parser) {
        int align = PubUtil.parseInt(parser.getAttributeValue("", "Align"));
        AlignMode alignMode = null;
        if (align == 0) {
            alignMode = AlignMode.MIDDLELEFT;
        } else if (align == 1) {
            alignMode = AlignMode.MIDDLERIGHT;
        } else if (align == 2) {
            alignMode = AlignMode.CENTER;
        } else {
            alignMode = AlignMode.MIDDLELEFT;
        }
        return alignMode;
    }


    public static boolean UpdateXmlFile(Document document, String filename) {
        boolean flag = true;
        try {
            /** 将document中的内容写入文件中   */
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            /** 编码 */
            //            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);
        } catch (Exception ex) {
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    public static Document load(String filename) {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(filename);
            document = builder.parse(file);
            document.normalize();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return document;
    }

    /**
     * 解析将数据库的紧急消息保存到Emerr.txt的内容
     *
     * @param str
     */
    private ArrayList<String> parseDBEmer(String str) {
        ArrayList<String> recList = null;
        try {
            // JSONObject jsonObject = new JSONObject(str);
            //  int num = jsonObject.getInt("count");
            Gson gson = new Gson();
            EmergBean bean = gson.fromJson(str, EmergBean.class);
            ArrayList beanArrayList = new ArrayList<EmergBean>();
            beanArrayList.clear();
            //            if (num > 0) {
            //                String list = jsonObject.getString("list");
            //                JSONArray jsonArray = new JSONArray(list);
            //                recList = new ArrayList<String>();
            //                recList.clear();
            //                if (jsonArray.length() > 0) {
            //                    for (int i = 0; i < jsonArray.length(); i++) {
            //                        EmergBean bean = new EmergBean();
            //                        JSONObject json = jsonArray.getJSONObject(i);
            ////                        bean.setInfo_id(getString(json, "Info_id"));
            ////                        bean.setInfo_name(getString(json, "Info_name"));
            ////                        bean.setInfo_cotent(PubUtil.getString(json, "Info_cotent"));
            ////                        bean.setInfo_type(PubUtil.getString(json, "Info_type"));
            ////                        bean.setSub_type(PubUtil.getString(json, "Sub_type"));
            ////                        bean.setStart_date(PubUtil.getString(json, "Start_date"));
            ////                        bean.setStop_date(PubUtil.getString(json, "Stop_date"));
            ////                        bean.setStart_time(PubUtil.getString(json, "Start_time"));
            ////                        bean.setStop_time(PubUtil.getString(json, "Stop_time"));
            ////                        bean.setEditor_id(PubUtil.getString(json, "Editor_id"));
            ////                        bean.setEdit_time(PubUtil.getString(json, "Edit_time"));
            ////                        bean.setAudit(PubUtil.getString(json, "Audit"));
            ////                        bean.setDevice_pos(PubUtil.getString(json, "Device_pos"));
            ////                        bean.setLine_code(PubUtil.getString(json, "Line_code"));
            ////                        bean.setStation_code(PubUtil.getString(json, "Station_code"));
            beanArrayList.add(bean);
            //                    }
            //                    //判断当前字符串是否与Emerg.txt中的相同，如果不同则重新写入
            if (beanArrayList != null && beanArrayList.size() > 0) {
                recList = parse(beanArrayList);
            }
            //                }
            //            }
        } catch (Exception e) {
            Log.i("解析错误", e.toString());
        }
        return recList;
    }

    DateFormat formater = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public ArrayList<String> parse(ArrayList<EmergBean> list) {
        ArrayList emergBeen = new ArrayList<String>();
        emergBeen.clear();
        for (int i = 0; i < list.size(); i++) {
            EmergBean bean = list.get(i);
            for (int j = 0; j < bean.getContents().size(); j++) {
                emergBeen.add(bean.getContents().get(j));
                LogUtil.e("文本内容", bean.getContents().get(j));
            }
        }
        //        try {
        //            for (int i = 0; i < list.size(); i++) {
        //                EmergBean bean = list.get(i);
        //                Date start = formater.parse(bean.getStart_date());
        //                Date stop = formater.parse(bean.getStop_date());
        //                Date now = VeDate.getNowDate();
        //                if (now.getTime() >= start.getTime() && now.getTime() <= stop.getTime()) {
        //                    emergBeen.add(bean.getInfo_cotent());
        //                }
        //
        //            }
        //        } catch (Exception e) {
        //
        //        }
        return emergBeen;
    }


    public static boolean UpdateXmlParam(String filename, String name, String value) {
        Document document = load(filename);
        Element root = document.getDocumentElement();
        boolean bSave = false;
        if (root.hasChildNodes()) {
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node subnode = nodes.item(i);
                if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                    String nodename = subnode.getNodeName();
                    if (nodename.equals(name)) {
                        subnode.setTextContent(value);
                        bSave = true;
                        break;
                    }
                }
            }
        }
        if (bSave) {
            return UpdateXmlFile(document, filename);
        }

        return bSave;
    }

    public static boolean UpdateTaskList(String filename, int val, String time) {
        Document document = load(filename);
        Element root = document.getDocumentElement();
        boolean bSave = false;
        /** 如果root有子元素 */
        if (root.hasChildNodes()) {
            NodeList nodes = root.getChildNodes();
            /** 循环取得所有节点 */
            for (int i = 0; i < nodes.getLength(); i++) {
                Node subnode = nodes.item(i);
                if (subnode.getNodeType() == Node.ELEMENT_NODE && subnode.hasAttributes()) {
                    String value = subnode.getFirstChild().getNodeValue();
                    if (value != null && value.matches("[0-9]+") && Integer.parseInt(value) == val) {
                        Element el = (Element) subnode;
                        el.setAttribute("time", time);
                        bSave = true;
                        break;
                    }
                }
            }
        }

        if (bSave) {
            return UpdateXmlFile(document, filename);
        }

        return bSave;
    }


    public static void showLog(boolean bSaveResult, String str, String old, String now) {
        if (bSaveResult) {
            Log.i(str, "由" + old + "保存为" + now + "成功");
        } else {
            Log.i(str, "由" + old + "保存为" + now + "失败");
        }
    }
}
