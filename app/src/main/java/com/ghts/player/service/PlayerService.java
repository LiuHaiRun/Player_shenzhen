package com.ghts.player.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.ghts.player.activity.ExigentActivity;
import com.ghts.player.activity.PlayerActivity;
import com.ghts.player.application.CrashApplication;
import com.ghts.player.bean.ChannelBean;
import com.ghts.player.bean.DayList;
import com.ghts.player.bean.LayoutBean;
import com.ghts.player.bean.MsgBean;
import com.ghts.player.bean.MsgModel;
import com.ghts.player.bean.PlayDateBean;
import com.ghts.player.bean.PlayDayBean;
import com.ghts.player.bean.PlayList;
import com.ghts.player.bean.ScreenBean;
import com.ghts.player.data.CmdDate;
import com.ghts.player.data.StationBean;
import com.ghts.player.data.StrainBean;
import com.ghts.player.data_ex.NET_ES_PARAM;
import com.ghts.player.manager.ViewManager;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.ParseJson;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.ShellUtils;
import com.ghts.player.utils.TargaReader;
import com.ghts.player.utils.UserInfoSingleton;
import com.ghts.player.utils.VeDate;
import com.ghts.player.video.LocalVideoView;
import com.ghts.player.video.MLiveDialog;
import com.ghts.player.widget.AdvertisementView;
import com.ghts.player.widget.AtsStationView;
import com.ghts.player.widget.AtsStrainView;
import com.ghts.player.widget.BaseModuleInfo;
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
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import android_serialport_api.SerialPortUtil;
import static com.ghts.player.manager.ViewManager.viewGroup;
import static com.ghts.player.utils.Const.moduleMap;

/**
 * Created by lijingjing on 17-5-12.
 */
public class PlayerService extends Service {
    // 背景
    Bitmap bk_bitmap = null;
    // 设置背景图片
    Drawable bk_drawable;
    //播表数据
    private static String TAG = "PlayerService";
    private PlayList playList;
    private DayList dayList;
    private LayoutBean layoutBean;
    //版式，模块
    private ScreenBean screenBean;
    private MsgBean msgBean;
    private MsgModel msgModel;
    private MsgModel EmsgModel;
    private LayoutBean next_layout_item; //下一个播放的版式
    private LayoutBean current_layout_item; //当前播放的版式
    private ListIterator<LayoutBean> layoutIterator;
    private List<LayoutBean> layout_list;
    private String playlist_dir;
    private Thread changeViewThread = null; //更新界面线程
    private boolean isChangeView = false;
    private ViewGroup container;
    private AudioManager mAudioManager;
    private ArrayList<BaseModuleInfo> moduleInfoList;//模块集合
    private int current = 0;
    private int max = 0;
    int left, right, top, bottom;
    private static ViewManager viewManager = null;
    boolean isDefault = false;
    static Thread mThread;

    private Thread changeNearViewThread = null; //djx 多版式更新界面线程
    private String nowLayoutName; // djx 记录现在版式名称

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ConstantValue.ACTION_CMD_CONTROL);
        registerReceiver(serviceReceiver, myIntentFilter);
        viewManager = ViewManager.getInstance();
        //获取音频服务 通过mAudioManager可以调整音量和静音设置
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        moduleMap = new HashMap<String, String>();
    }

    private void findPlay() {
        closeThread();
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 30);
                    LogUtil.e("---查找版式--", "-----");
                    handler.sendEmptyMessage(1);
                } catch (Exception e0) {
                    Log.e(TAG, "查找版式失败", e0);
                }
            }
        });
        mThread.start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            init();
        }
    };
    private void closeThread() {
        try {
            if (mThread != null) {
                mThread.interrupt();
                mThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e("--onStart--", "--onStart--");
        init();
    }

    @Override
    public void onDestroy() {
        Log.e("--playerservice--", "--onDestroy--");
        closeViewThread();
        closeThread();
        //关闭本地检测线程
        //        viewManager.getVideoInfo().closeReceiveThread();
        unregisterReceiver(serviceReceiver);
        stopSelf();
        super.onDestroy();
    }


    public void init() {
        setPlayDate();
    }

    /**
     * 判断当天播放哪一个播表
     */
    ListIterator<PlayDayBean> iterator;

    private void setPlayDate() {
        try {
            PlayDateBean playDateBean = Const.parseXml.getPlayDate();
            if (playDateBean.getDayList() != null && playDateBean.getDayList().size() > 0) {
                iterator = playDateBean.getDayList().listIterator();
                while (iterator.hasNext()) {
                    PlayDayBean item = iterator.next();
                    // 获取当前时间
                    String dTime = item.getdTime();
                    String sDate = dTime.substring(1, dTime.length());
                    String yy = sDate.substring(0, 4);
                    String mm = sDate.substring(4, 6);
                    String dd = sDate.substring(6, 8);
                    sDate = yy + "-" + mm + "-" + dd;
                    LogUtil.e("----sDate----" + PubUtil.isCurrentDay(sDate), sDate);
                    if (PubUtil.isCurrentDay(sDate)) {
                        playlist_dir = item.getFile().toLowerCase();
                        String currentP[] = playlist_dir.split("/");
                        Const.currentPlay = currentP[3];
                        isDefault = true;
                        initPlayData(playlist_dir);
                        break;
                    }
                    isDefault = false;
                }
            } else {
                isDefault = false;
            }
            if (!isDefault) {
                iterator = playDateBean.getDefaultlist().listIterator();
                playlist_dir = playDateBean.getDefaultlist().get(0).getFile().toLowerCase();
                String currentP[] = playlist_dir.split("/");
                Const.currentPlay = currentP[3];
                initPlayData(playlist_dir);
            }
        } catch (Exception e) {
            LogUtil.e("无法找到播表", e.toString());
        }
    }

    /**
     * 解析模版
     *
     * @param path 路径
     */
    private void initPlayData(String path) {
        try {
            //初始化播表
            LogUtil.e("----path---", path);
            playList = Const.parseXml.getPlayList(path);
            if (playList != null) {
                if (playList.getLayoutBeanList().size() > 0) {
                    layout_list = playList.getLayoutBeanList();
                    layoutIterator = playList.getLayoutBeanList().listIterator();
                    findNext();//找到下一个layout
                    if (next_layout_item == null) { // 如果一个都没找到，则播放第一个
                        next_layout_item = layout_list.get(0);
                    }
                    current_layout_item = next_layout_item;
                }
                initCurrentLayout();//创建viewGroup
                ViewManager.getInstance().changeContentView(container, moduleInfoList); // 更改界面
                //执行切换播表的线程
                isChangeView = true;
                if (changeViewThread == null) {
                    changeViewThread = new ChangeViewThread();
                    changeViewThread.setName("changeViewThread");
                }
                changeViewThread.start();
            } else {
                findPlay();
            }
        } catch (Exception e) {
            LogUtil.e("无法找到版式", e.toString());
            findPlay();
        }
    }


    /**
     * 解析并显示当前模板
     */
    private void initCurrentLayout() {
        try {
            if (null == next_layout_item)
                return;
            String layout_name = next_layout_item.getFileName();    //当前布局文件的名称
            String dir[] = playlist_dir.split("/");
            String layout_dir = dir[0] + "/" + dir[1] + "/" + dir[2] + "/" + dir[3] + "/" + PubUtil.getFileNameNoEx(layout_name) + "/" + layout_name;
//            LogUtil.e("版本日期:20190507", "版本号:2.4.1");
//            LogUtil.e("版本日期:20190904", "版本号:2.4.2");
//            LogUtil.e("版本日期:20190919", "版本号:2.4.2");
            LogUtil.e("版本日期:20190923", "版本号:2.4.2");

            LogUtil.e("---layout_dir----path-----", layout_dir);
            initMsgData(layout_dir.toLowerCase());
            Const.currentLayout = PubUtil.getFileNameNoEx(layout_name);
        } catch (Exception e) {
            LogUtil.e(TAG, "出现异常", e);
        } finally {
            System.gc();
        }
    }

    /**
     * 找到下一个播放项。
     */
    void findNext() {
        layoutIterator = layout_list.listIterator();
        int[] absTime = new int[layout_list.size()];
        int i = 0;
        int currentNum = -1;
        int smallistAbsTime = 86400000;
        while (iterator.hasNext()) {
            if (i == layout_list.size()) {
                return;
            }
            absTime[i] = -1;
            // 获取第一个模板项
            LayoutBean item = layoutIterator.next();
            // 获取当前时间毫秒值
            long currentTimeMillis = System.currentTimeMillis();
            Time current_time = new Time();
            current_time.set(currentTimeMillis);

            Time start_time = item.getStartTime(); // 开始时间
            if (timeToMillis(current_time) >= timeToMillis(start_time)) { // 如果当前时间晚于或等于本模板开始播放时间
                absTime[i] = timeToMillis(current_time) - timeToMillis(start_time);
                break; // 找到后退出循环
            }
            i++;
        }
        //找到最近时刻的播表
        for (i = 0; i < layout_list.size(); i++) {
            if (absTime[i] < 0) {
                continue;
            } else if (absTime[i] <= smallistAbsTime) {
                smallistAbsTime = absTime[i];
                currentNum = i;
            }
        }
        if (currentNum >= 0) {
            next_layout_item = layout_list.get(currentNum);
        } else {
            next_layout_item = null;
        }
    }

    /**
     * 计算某个时间点的时、分、秒对应的毫秒值
     *
     * @param time 时间点
     * @return 时间的时分秒对应的毫秒值
     */
    private int timeToMillis(Time time) {
        return time.hour * 60 * 60 * 1000 + time.minute * 60 * 1000
                + time.second * 1000;
    }

    private void initBackGround(String bk_file_path) {
        container = new FrameLayout(getApplicationContext()); // 创建控件容器，显示布局
        container.setLayoutParams(new FrameLayout.LayoutParams(Const.screenW, Const.screenH)); // 设置容器的宽高
        LogUtil.e("----bk_file_path---", bk_file_path);
        if (bk_file_path != null && !"".equals(bk_file_path)) {
            String suffix = bk_file_path.substring(bk_file_path.lastIndexOf(".") + 1);
            if (suffix.equals("tga")) {
                bk_bitmap = TargaReader.getImage(bk_file_path);
            } else {
                bk_bitmap = PubUtil.fitSizeImg(bk_file_path);
            }
            // 加入到软引用
            SoftReference<Bitmap> mReference = new SoftReference<Bitmap>(bk_bitmap);
            // 先判断是否已经回收----
            if (bk_bitmap != null && !bk_bitmap.isRecycled()) {
                // 回收并且置为null,释放硬应用
                bk_bitmap = null;
            }
            // 获取软引用中的位图变量
            Bitmap getBitmap = mReference.get();
            if (getBitmap != null) {
                bk_drawable = new BitmapDrawable(getBitmap);
                // 软引用的bitmap为null(已被回收)
                container.setBackgroundDrawable(bk_drawable);
            }
        }
    }

    /************
     * 解析布局文件********@path路径
     ********************************/
    private void initMsgData(String path) {
        String itemPath = "";
        try {
            //初始化播表
            MsgBean msgBean = Const.parseXml.getMsgXml(path);
            if (msgBean != null) {
                int width = msgBean.getWidth();
                int height = msgBean.getHeight();
                String updateTime = msgBean.getUpdateTime();
                int bkType = msgBean.getBkType();
                String bkFile = msgBean.getBkFIle();
                //设置宽高缩放比
                Const.ScaleX = PubUtil.getScaleX(width);
                Const.ScaleY = PubUtil.getScaleY(height);
                moduleInfoList = new ArrayList<BaseModuleInfo>();
                //背景图片路径
                String lastpath = path.substring(0, path.lastIndexOf("/"));
                String layout_dir = lastpath + "/";//当前布局文件的路径
                bkFile = layout_dir + bkFile;
                initBackGround(bkFile); //设置背景图
                for (int i = 0; i < msgBean.getMsgModels().size(); i++) {
                    msgModel = msgBean.getMsgModels().get(i);
                    if (msgModel != null) {
                        String module = msgModel.getModule();
                        int index = msgModel.getIndex();
                        left = msgModel.getLeft();
                        right = msgModel.getRight();
                        top = msgModel.getTop();
                        bottom = msgModel.getBottom();
                        String moduleName = msgModel.getModuleName();
                        moduleName = moduleName.toLowerCase();
                        if (moduleName.contains("ats") && Const.globalBean.getIsTrain().equals("0")) {
                            moduleName = "atsstation0";
                        } else if (moduleName.contains("ats") && Const.globalBean.getIsTrain().equals("1")) {
                            moduleName = "atstrain0";
                        }
                        itemPath = layout_dir + moduleName + "/" + moduleName + ".xml";
                        itemPath = itemPath.toLowerCase();
                        LogUtil.e("--itemPath--", itemPath);
                        //根据moduleName加载每一个Item
                        if (moduleName.startsWith("title")) {
                            TitleView titleView = Const.parseXml.ParseTxt(itemPath);
                            if (titleView != null) {
                                moduleInfoList.add(titleView);
                                Const.moduleMap.put(moduleName, titleView.getShowStr());
                            }
                        } else if (moduleName.startsWith("crawlmsg")) {
                            ScrollView scrollView = Const.parseXml.ParseCrawl(itemPath);
                            if (scrollView != null) {
                                moduleInfoList.add(scrollView);
                                //获取滚动条信息
                                ArrayList<String> sc = scrollView.getText_list();
                                StringBuffer text = new StringBuffer();
                                for (int m = 0; m < sc.size(); m++) {
                                    text.append(sc.get(m) + ";");
                                }
                                ParseJson parseJson = new ParseJson();
                                if (!TextUtils.isEmpty(parseJson.getTimeDate())) {
                                    String timeDate = parseJson.getTimeDate();
                                    Const.scrollingtime = timeDate;
                                }
                                //----------------福州修改------------------------------
                                if (text.toString().length() > 100) {
                                    String substring = text.toString().substring(0, 100);
                                    Const.scrollingtext = moduleName;
                                    Const.moduleMap.put(moduleName, Const.scrollingtime + substring + "...");
                                    //                                    Const.moduleMap.put(moduleName,  substring + "...");
                                } else {
                                    Const.moduleMap.put(moduleName, Const.scrollingtime + text.toString());
                                    //                                    Const.moduleMap.put(moduleName,  text.toString());
                                }
                            }
                        } else if (moduleName.startsWith("time")) {
                            TimeView timeView = Const.parseXml.ParseTime(itemPath);
                            if (timeView != null) {
                                moduleInfoList.add(timeView);
                            }
                        } else if (moduleName.startsWith("date")) {
                            DateView dateView = Const.parseXml.ParseDate(itemPath);
                            if (dateView != null) {
                                moduleInfoList.add(dateView);
                            }
                        } else if (moduleName.startsWith("icon")) {
                            MyImgView imgView = Const.parseXml.parseImg(itemPath);
                            if (imgView != null) {
                                moduleInfoList.add(imgView);
                                Const.moduleMap.put(moduleName, imgView.getFilePath());
                            }
                        } else if (moduleName.startsWith("ch1player")) {
                            LocalVideoView localVideoView = Const.parseXml.ParsePlayer(itemPath);
                            if (localVideoView != null) {
                                moduleInfoList.add(localVideoView);
                            }
                        } else if (moduleName.startsWith("week")) {
                            WeekView weekView = Const.parseXml.parseWeek(itemPath);
                            if (weekView != null) {
                                moduleInfoList.add(weekView);
                            }
                        } else if (moduleName.startsWith("traininfo")) {
                            TrainInfoView traininfoStrainView = Const.parseXml.ParseTrainInfo(itemPath);
                            if (traininfoStrainView != null) {
                                moduleInfoList.add(traininfoStrainView);
                            }
                        } else if (moduleName.contains("ats")) {
                            //判断是车站还是车载的
                            if (Const.globalBean.getIsTrain().equals("0")) {
                                AtsStationView stationView = Const.parseXml.parseStation(itemPath);
                                if (stationView != null) {
                                    moduleInfoList.add(stationView);
                                    Const.moduleMap.put(moduleName, "车站ATS");
                                }
                            } else if (Const.globalBean.getIsTrain().equals("1")) {
                                AtsStrainView atsStrainView = Const.parseXml.parseAtsStrain(itemPath);
                                if (atsStrainView != null) {
                                    moduleInfoList.add(atsStrainView);
                                    Const.moduleMap.put(moduleName, "车载ATS");
                                }
                            }
                        } else if (moduleName.startsWith("advertisement")) {//广告模块
                            AdvertisementView advertisementView = Const.parseXml.parseAdvertisement(itemPath);
                            if (advertisementView != null) {
                                moduleInfoList.add(advertisementView);
                                ArrayList<String> imgs = advertisementView.getImgPaths();
                                StringBuffer text = new StringBuffer();
                                for (int m = 0; m < imgs.size(); m++) {
                                    text.append(imgs.get(m) + ";");
                                }
                                Const.moduleMap.put(moduleName, text.toString());
                            }
                        } else if (moduleName.startsWith("weather")) {
                            WeatherView weatherView = Const.parseXml.parseWeather(itemPath);
                            if (weatherView != null) {
                                moduleInfoList.add(weatherView);
                            }
                        }
                    }
                }
                //紧急消息模块
                for (int i = 0; i < msgBean.getEmMsgModels().size(); i++) {
                    EmsgModel = msgBean.getEmMsgModels().get(i);
                    left = msgModel.getLeft();
                    right = msgModel.getRight();
                    top = msgModel.getTop();
                    bottom = msgModel.getBottom();
                    String moduleName = EmsgModel.getModule().toLowerCase();
                    //LogUtil.e("---紧急消息模块----", moduleName);
                    if (moduleName.startsWith("emcrawl")) {
                        moduleName = layout_dir + moduleName + "/" + moduleName + i + ".xml";
                        moduleName = moduleName.toLowerCase();
                        ExigentInfo_Full exigentInfo_full = Const.parseXml.parseExigent_full(moduleName);
                        if (exigentInfo_full != null) {
                            moduleInfoList.add(exigentInfo_full);
                        }
                        ExigentInfo exigentInfo = Const.parseXml.parseEmcr(moduleName);
                        if (exigentInfo != null) {
                            moduleInfoList.add(exigentInfo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("模块解析失败", e.toString());
        } finally {
            System.gc();
        }
    }

    /**********************
     * 消息处理
     **********/
    private final BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String type = intent.getStringExtra(ConstantValue.EXTRA_type);
            short cmd = intent.getShortExtra("cmd", (short) 0);
            switch (cmd) {
                case ConstantValue.CMD_CONTROL_SOUNDOFF://静音
                    UserInfoSingleton.putBooleanAndCommit(Const.GlobalAudioStatus, true);
                    UserInfoSingleton.putIntAndCommit(Const.AudioStatus, ConstantValue.AUDIOSTATUS_NO);
                    current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    break;
                case ConstantValue.CMD_CONTROL_SETVOLUME://设置音量
                    if (type.equals("old")) {
                        Bundle bundle = intent.getBundleExtra(ConstantValue.EXTRA_BUNDLE);
                        int volume = bundle.getInt("volume");
                        UserInfoSingleton.putIntAndCommit(Const.AudioVolume, volume);
                        Const.GlobalAudioVolume = true;
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume * max / 255, 0);
                        Log.e("--设置音量--" + Const.AudioVolume + volume, volume * max / 255 + "");
                        Const.AudioVolume = volume + "";
                    } else if (type.equals("new")) {
                        Bundle bundle = intent.getBundleExtra(ConstantValue.EXTRA_BUNDLE);
                        byte volume = bundle.getByte("volume");
                        int v = PubUtil.byteToInt(volume);
                        UserInfoSingleton.putIntAndCommit(Const.AudioVolume, v);
                        Const.GlobalAudioVolume = true;
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, v * max / 255, 0);
                        Log.e("--设置音量--" + Const.AudioVolume + v, v * max / 255 + "");
                        Const.AudioVolume = v + "";
                    }
                    break;
                case ConstantValue.CMD_CONTROL_SOUNDON://开启声音
                    UserInfoSingleton.putBooleanAndCommit(Const.GlobalAudioStatus, false);
                    UserInfoSingleton.putIntAndCommit(Const.AudioStatus, ConstantValue.AUDIOSTATUS_YES);
                    if (current != 0) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                current, 0);
                    }
                    break;
                case ConstantValue.CMD_CONTROL_PLAYSTREAM:
                    String flag = UserInfoSingleton.getIslive();
                    if (flag.equals(ConstantValue.PLAY_LIVE)) {
                    } else {
                        LogUtil.e(TAG, "启动直播播放");
                        UserInfoSingleton.setIslive(ConstantValue.PLAY_LIVE);
                        changeStream();
                    }
                    break;
                case ConstantValue.CMD_CONTROL_PLAYLOCAL:
                    String flag1 = UserInfoSingleton.getIslive();
                    if (flag1.equals(ConstantValue.PLAY_LOCAL)) {
                    } else {
                        LogUtil.e(TAG, "启动本地播放");
                        UserInfoSingleton.setIslive(ConstantValue.PLAY_LOCAL);
                        changeLocal();
                    }
                    break;
                case ConstantValue.CMD_TEXT_SETPROIRITY:
                    LogUtil.e("发布紧急信息命令", "-------");
                    if (type.equals("new")) {
                        CmdDate data = (CmdDate) intent.getSerializableExtra(ConstantValue.EXTRA_OBJ);
                        if (data != null) {
                            startExigent(data);
                        }
                    } else if (type.equals("old")) {
                        NET_ES_PARAM data = (NET_ES_PARAM) intent.getSerializableExtra(ConstantValue.EXTRA_OBJ);
                        if (data != null) {
                            startExigent(data);
                        }
                    }
                    break;
                case ConstantValue.CMD_TEXT_STOP:
                    LogUtil.e("取消紧急信息命令", "-------");
                    stopExigent();
                    break;
                case ConstantValue.CMD_TEXT_TRAININFO:
                    LogUtil.e("接收到首末班车信息", "-------");
                    String s = (String) intent.getSerializableExtra(ConstantValue.EXTRA_OBJ);
                    viewManager.getTrainInfoView().upDataTrainInfo(s);
                    break;
                case ConstantValue.CMD_TEXT_ATSSTRAIN:
                    LogUtil.e("ATSSTRAIN", "-------");
                    StrainBean strainBean = (StrainBean) intent.getSerializableExtra(ConstantValue.EXTRA_OBJ);
                    byte stype = intent.getByteExtra(ConstantValue.EXTRA_BUNDLE, (byte) 0);
                    if (strainBean != null) {
                        viewManager.getmAtsStrain().receiveControlCmd(strainBean, stype);
                    }
                    break;
                case ConstantValue.CMD_TEXT_ATSSTATION:
                    ArrayList<StationBean> arrayLists = (ArrayList<StationBean>) intent.getSerializableExtra(ConstantValue.EXTRA_OBJ);
                    byte show = intent.getByteExtra(ConstantValue.EXTRA_BUNDLE, (byte) 0);
                    if (arrayLists != null && viewManager.getmAtsStationView() != null) {
                        viewManager.getmAtsStationView().receiveControlCmd(arrayLists, show);
                    }
                    break;
                case ConstantValue.CMD_CLEAR_ATSSTRAIN:
                    /**
                     * 十分钟接收不到车载信息，清空ATS信息
                     */
                    if (viewManager.getmAtsStrain() != null) {
                        viewManager.getmAtsStrain().clearControlCmd();
                    }
                    break;
                case ConstantValue.CMD_CLEAR_ATSSTATION:
                    /**
                     * 十分钟接收不到车站信息，清空ATS信息
                     */
                    if (ViewManager.getInstance().getmAtsStationView() != null) {
                        ViewManager.getInstance().getmAtsStationView().clearControlCmd();
                    }
                    break;
                case ConstantValue.CMD_MT_LIVE_ON:
                    flag = UserInfoSingleton.getIslive();
                    if (flag.equals(ConstantValue.PLAY_LIVE)) {
                    } else {
                        LogUtil.e(TAG, "启动直播播放");
                        UserInfoSingleton.setIslive(ConstantValue.PLAY_LIVE);
                        LocalVideoView.isCmdService = false;
                        changeStream();
                    }
                    break;
                case ConstantValue.CMD_MT_LIVE_OFF:
                    flag1 = UserInfoSingleton.getIslive();
                    if (flag1.equals(ConstantValue.PLAY_LOCAL)) {
                    } else {
                        LogUtil.e(TAG, "启动本地播放");
                        UserInfoSingleton.setIslive(ConstantValue.PLAY_LOCAL);
                        LocalVideoView.isCmdService = true;
                        changeLocal();
                    }
                    break;
                case ConstantValue.CMD_MT_SCREEN_ON:
                    //开屏信息 0
                    if (!SerialPortUtil.flag) {
                        SerialPortUtil.openSrialPort();
                    }
                    if (Const.globalBean.getPctrlEnable() == 1) {
                        pctrl(1);
                        UserInfoSingleton.setIsScreenOff(ConstantValue.IsScreenOff_NO + "");
                        LogUtil.e("----", "打开屏幕");
                    }
                    break;
                case ConstantValue.CMD_MT_SCREEN_OFF:
                    //关屏信息 1
                    if (!SerialPortUtil.flag) {
                        SerialPortUtil.openSrialPort();
                    }
                    if (Const.globalBean.getPctrlEnable() == 1) {
                        pctrl(2);
                        LogUtil.e("----", "关闭屏幕");
                        UserInfoSingleton.setIsScreenOff(ConstantValue.IsScreenOff_YES + "");
                    }
                    break;
                case ConstantValue.CMD_MT_RESET_DEVICE: //重启设备
                    ShellUtils.reboot();
                    break;
                case ConstantValue.MT_POWER_ON: //开机
                    LogUtil.e("--开机--", "---");
                    PubUtil.WriteMyGPIO(11);
                    UserInfoSingleton.setIsPowerOff(ConstantValue.IsPowerOff_NO + "");
                    break;
                case ConstantValue.MT_POWER_OFF: //关机
                    LogUtil.e("--关机--", "---");
                    PubUtil.WriteMyGPIO(10);
                    UserInfoSingleton.setIsPowerOff(ConstantValue.IsPowerOff_YES + "");
                    break;
                case ConstantValue.UPDATE_EMERGENCY: //更新紧急消息内容 数据库
                    ArrayList<String> emerg = intent.getStringArrayListExtra(ConstantValue.EXTRA_OBJ);
                    if (emerg.toString().length() > 100) {
                        String substring = emerg.toString().substring(0, 100);
                        Const.moduleMap.put(Const.scrollingtext, Const.scrollingtime + substring);
                    } else {
                        Const.moduleMap.put(Const.scrollingtext, Const.scrollingtime + emerg.toString());

                    }
                    Log.e("接收到的紧急消息", emerg.size() + "" + emerg.toString());
                    if (viewManager.getScrollInfo() != null) {
                        try {
                            viewManager.getScrollInfo().setVisibility(false);
                            if (emerg != null && emerg.size() > 0) {
                                viewManager.getScrollInfo().receiveControlCmd(emerg);
                            }
                            Thread.sleep(1000);
                            viewManager.getScrollInfo().setVisibility(true);
                            LogUtil.e("---执行紧急消息---", "---执行");
                        } catch (Exception e) {

                        }
                    }
                    break;
                case ConstantValue.CMD_FILE_SETLIST:
                    try {
                        //findNext_take();
                        current_layout_item = next_layout_item;
                        initCurrentLayout();
                        ViewManager.getInstance().changeContentView(viewGroup, moduleInfoList); // 更改界面
                    } catch (Exception e) {
                        LogUtil.e(TAG, e);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 开关屏
     * type： 1：开屏 2：关屏 3：查询
     */
    public void pctrl(int type) {
        /**
         * pctrlType 0是我们家机柜式的，1是我们家独立式的，2是老潘那边机柜式的，3是他们家独立式的
         独立式的协议都是一样的
         */
        int pctrlType = Const.globalBean.getPctrlType();
        int pctrlCount = Const.globalBean.getPctrlCount();
        String com = Const.globalBean.getPctrlCom();
        if (pctrlCount > 0) {
            ArrayList<ChannelBean> pctrlList = new ArrayList();
            pctrlList.clear();
            pctrlList = Const.globalBean.getPctrlList();
            switch (pctrlType) {
                case 0: //机柜式
                    if (com.startsWith("tty")) {
                        try {
                            for (int i = 0; i < pctrlList.size(); i++) {
                                ChannelBean bean = pctrlList.get(i);
                                String addr = (bean.getAddr());
                                DecimalFormat df = new DecimalFormat("00");
                                addr = df.format(PubUtil.parseInt(addr));
                                String code = "";
                                //AB 地址 命令01 校验和  独立式的
                                if (type == 1) {
                                    code = "5A" + addr + "01";
                                } else if (type == 2) {
                                    code = "5A" + addr + "02";
                                } else if (type == 3) {
                                    code = "5A" + addr + "03";
                                }
                                String num = PubUtil.makeChecksum(code);
                                String ml = code + num;
                                SerialPortUtil.sendSerialPort(ml);
                                Thread.sleep(1000);
                            }
                        } catch (Exception e) {
                            LogUtil.e("发送开屏命令失败", e.toString());
                        }
                    }
                    break;
                case 1: //独立式
                    if (com.startsWith("tty")) {
                        try {
                            for (int i = 0; i < pctrlList.size(); i++) {
                                ChannelBean bean = pctrlList.get(i);
                                String addr = (bean.getAddr());
                                DecimalFormat df = new DecimalFormat("00");
                                addr = df.format(PubUtil.parseInt(addr));
                                String code = "";
                                //AB 地址 命令01 校验和  独立式的
                                if (type == 1) {
                                    code = "AB" + addr + "01";
                                } else if (type == 2) {
                                    code = "AB" + addr + "02";
                                } else if (type == 3) {
                                    code = "AB" + addr + "03";
                                }
                                String num = PubUtil.makeChecksum(code);
                                String ml = code + num;
                                SerialPortUtil.sendSerialPort(ml);
                                Thread.sleep(1000);
                            }
                        } catch (Exception e) {
                            LogUtil.e("发送开屏命令失败", e.toString());
                        }
                    }
                    break;
                case 2:
                    if (com.startsWith("tty")) {
                        try {
                            for (int i = 0; i < pctrlList.size(); i++) {
                                ChannelBean bean = pctrlList.get(i);
                                String addr = bean.getAddr();
                                DecimalFormat df2 = new DecimalFormat("00");
                                addr = df2.format(PubUtil.parseInt(addr));
                                int firstPort = PubUtil.parseInt(bean.getFirstPort());
                                int lastPort = PubUtil.parseInt(bean.getLastPort());
                                String code = "";
                                if (type == 1) {
                                    code = "01";
                                } else if (type == 2) {
                                    code = "02";
                                } else if (type == 3) {
                                    code = "03";
                                }
                                StringBuffer devices = new StringBuffer();
                                //  AB 00  02 08 01 02 02 02 03 02 04 02 C7  机柜
                                for (int m = firstPort; m <= lastPort; m++) {
                                    LogUtil.e("电源控制器数量", m + "");
                                    String aa = m + "";
                                    DecimalFormat df = new DecimalFormat("00");
                                    aa = df.format(PubUtil.parseInt(aa));

                                    devices.append(aa + code);
                                }
                                Log.e("电源控制器", devices.toString());
                                String num = PubUtil.makeChecksum("AB" + addr + "0208" + devices.toString());
                                String ml = "AB" + addr + "0208" + devices.toString() + num;
                                SerialPortUtil.sendSerialPort(ml);
                                Log.e("发送电源控制器命令", ml);
                                Thread.sleep(1000);
                            }
                        } catch (Exception e) {
                            LogUtil.e("发送开屏命令失败", e.toString());
                        }
                    }
                    break;
                case 3:
                    if (com.startsWith("tty")) {
                        try {
                            for (int i = 0; i < pctrlList.size(); i++) {
                                ChannelBean bean = pctrlList.get(i);
                                String addr = (bean.getAddr());
                                DecimalFormat df = new DecimalFormat("00");
                                addr = df.format(PubUtil.parseInt(addr));
                                String code = "";
                                //AB 地址 命令01 校验和  独立式的
                                if (type == 1) {
                                    code = "AB" + addr + "01";
                                } else if (type == 2) {
                                    code = "AB" + addr + "02";
                                } else if (type == 3) {
                                    code = "AB" + addr + "03";
                                }
                                String num = PubUtil.makeChecksum(code);
                                String ml = code + num;
                                SerialPortUtil.sendSerialPort(ml);
                                Thread.sleep(1000);
                            }
                        } catch (Exception e) {
                            LogUtil.e("发送开屏命令失败", e.toString());
                        }
                    }
                    break;
            }
        }
    }

    /**************
     * 直播本地视频切换*******1.转为播放直播视频 2.转为播放本地视频
     ******/
    public void changeStream() {
        //        localPlayerPause();//暂停本地
        if (viewManager.getlVideo() != null) {
            viewManager.getlVideo().pause();
        }
        //如果当前是全屏紧急消息，则稍后再切直播
        if (UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES) && UserInfoSingleton.getIsEmFull()) {
            LocalVideoView.isCmdService = true;
        } else {
            Intent intent = new Intent(this, MLiveDialog.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void changeLocal() {
        if (MLiveDialog.instance != null) {
            MLiveDialog.instance.finish();
            //            localPlayerReplay();
            if (viewManager.getlVideo() != null) {
                viewManager.getlVideo().restart();
            }
        }
    }

    /**************************
     * 紧急消息模块*****开始，停止
     ************************************************/
    public void startExigent(NET_ES_PARAM data) {
        boolean state = data.getbFullScreen();
        if (!UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES)) {
            if (state) {
                if (viewManager.getExigentInfo_full() != null && ExigentActivity.instance == null) {
                    LogUtil.e("---全屏显示---", "----");
                    Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_SOUNDOFF);
                    sendBroadcast(mIntent);

                    UserInfoSingleton.setIsEmFull(true);
                    UserInfoSingleton.setEmergency(data.getSzContent());

                    Intent intent = new Intent(this, ExigentActivity.class);
                    intent.putExtra("exigent", data.getSzContent());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else if (!state) {
                LogUtil.e("---局部滚动---", "----");
                if (viewManager.getExigentInfo() != null) {
                    UserInfoSingleton.setIsEmFull(false);
                    if (viewManager.getScrollInfo() != null) {
                        viewManager.getScrollInfo().setVisibility(false);
                    }
                    if (viewManager.getExigentInfo() != null) {
                        viewManager.getExigentInfo().receiveControlCmd(data.getSzContent());
                        viewManager.getExigentInfo().setVisibility(true);
                    }
                    UserInfoSingleton.setEmergency(data.getSzContent());
                    UserInfoSingleton.setIsExigent(ConstantValue.EMERGSTATUS_YES + "");
                }
            }
        } else {
            //如果当前是紧急状态
            if (UserInfoSingleton.getIsEmFull()) {
                if (state) {
                    if (ExigentActivity.instance != null) {
                        ExigentActivity.instance.receiveMsg(data.getSzContent());
                        UserInfoSingleton.setEmergency(data.getSzContent());
                    }
                } else {
                }
            } else if (!UserInfoSingleton.getIsEmFull()) {
                if (state) {
                    //取消声音
                    Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_SOUNDOFF);
                    sendBroadcast(mIntent);
                    //启动页面
                    Intent intent = new Intent(this, ExigentActivity.class);
                    intent.putExtra("exigent", data.getSzContent());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //设置变量
                    UserInfoSingleton.setIsEmFull(true);
                    UserInfoSingleton.setEmergency(data.getSzContent());
                    if (viewManager.getExigentInfo() != null) {
                        viewManager.getExigentInfo().setVisibility(false);
                    }
                    if (viewManager.getScrollInfo() != null) {
                        viewManager.getScrollInfo().setVisibility(true);
                    }
                } else if (!state) {
                    if (viewManager.getExigentInfo() != null) {
                        viewManager.getExigentInfo().receiveControlCmd(data.getSzContent());
                        UserInfoSingleton.setEmergency(data.getSzContent());
                    }
                }
            }
        }
    }

    /**************************
     * 紧急消息模块*****开始，停止
     ************************************************/
    public void startExigent(CmdDate data) {
        int state = data.getTast_set().getiFullScreen(); // 1:全屏 0：局部
        if (!UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES)) {
            if (state == 1) {
                if (viewManager.getExigentInfo_full() != null && ExigentActivity.instance == null) {
                    LogUtil.e("---全屏显示---", "----");
                    Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_SOUNDOFF);
                    sendBroadcast(mIntent);

                    UserInfoSingleton.setIsEmFull(true);
                    UserInfoSingleton.setEmergency(data.getTast_set().getSzEmergencyInfo());

                    Intent intent = new Intent(this, ExigentActivity.class);
                    intent.putExtra("exigent", data.getTast_set().getSzEmergencyInfo());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else if (state == 0) {
                LogUtil.e("---局部滚动---", "----");
                if (viewManager.getExigentInfo() != null) {
                    UserInfoSingleton.setIsEmFull(false);
                    if (viewManager.getScrollInfo() != null) {
                        viewManager.getScrollInfo().setVisibility(false);
                    }
                    if (viewManager.getExigentInfo() != null) {
                        viewManager.getExigentInfo().receiveControlCmd(data);
                        viewManager.getExigentInfo().setVisibility(true);
                    }
                    UserInfoSingleton.setEmergency(data.getTast_set().getSzEmergencyInfo());
                    UserInfoSingleton.setIsExigent(ConstantValue.EMERGSTATUS_YES + "");
                }
            }
        } else {
            //如果当前是紧急状态
            if (UserInfoSingleton.getIsEmFull()) {
                if (state == 1) {
                    if (ExigentActivity.instance != null) {
                        ExigentActivity.instance.receiveMsg(data.getTast_set().getSzEmergencyInfo());
                        UserInfoSingleton.setEmergency(data.getTast_set().getSzEmergencyInfo());
                    }
                } else {

                }
            } else if (!UserInfoSingleton.getIsEmFull()) {
                if (state == 1) {
                    //取消声音
                    Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_SOUNDOFF);
                    sendBroadcast(mIntent);
                    //启动页面
                    Intent intent = new Intent(this, ExigentActivity.class);
                    intent.putExtra("exigent", data.getTast_set().getSzEmergencyInfo());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //设置变量
                    UserInfoSingleton.setIsEmFull(true);
                    UserInfoSingleton.setEmergency(data.getTast_set().getSzEmergencyInfo());
                    if (viewManager.getExigentInfo() != null) {
                        viewManager.getExigentInfo().setVisibility(false);
                    }
                    if (viewManager.getScrollInfo() != null) {
                        viewManager.getScrollInfo().setVisibility(true);
                    }
                } else if (state == 0) {
                    if (viewManager.getExigentInfo() != null) {
                        viewManager.getExigentInfo().receiveControlCmd(data);
                        UserInfoSingleton.setEmergency(data.getTast_set().getSzEmergencyInfo());
                    }
                }
            }
        }
    }

    public void startGpioExigent(String txt) {
        if (!UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES)) {
            if (viewManager.getExigentInfo_full() != null) {
                LogUtil.e("---全屏显示---", "----");
                Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_SOUNDOFF);
                sendBroadcast(mIntent);
                UserInfoSingleton.setIsEmFull(true);
                Intent intent = new Intent(this, ExigentActivity.class);
                intent.putExtra("exigent", txt);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    /**
     * 停止紧急消息<br>
     */
    public void stopExigent() {
        if (UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES)) {
            if (UserInfoSingleton.getIsEmFull()) {
                Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_SOUNDON);
                sendBroadcast(mIntent);
                UserInfoSingleton.setIsExigent(ConstantValue.EMERGSTATUS_NO + "");
                UserInfoSingleton.setIsEmFull(false);
                UserInfoSingleton.setEmergency("");
                if (ExigentActivity.instance != null) {
                    ExigentActivity.instance.finish();
                }

                if (LocalVideoView.isCmdService) {
                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    myIntent.putExtra("cmd", ConstantValue.CMD_MT_LIVE_ON);
                    sendBroadcast(myIntent);
                 }
            } else {
                if (viewManager.getExigentInfo() != null) {
                    viewManager.getExigentInfo().setVisibility(false);
                }
                if (viewManager.getScrollInfo() != null) {
                    viewManager.getScrollInfo().setVisibility(true);
                }
                Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_SOUNDON);
                sendBroadcast(mIntent);
                UserInfoSingleton.setEmergency("");
                UserInfoSingleton.setIsExigent(ConstantValue.EMERGSTATUS_NO + "");
                UserInfoSingleton.setIsEmFull(false);
            }
        }
    }

    /*******
     * 根据模板文件的显示时间， 开启一个线程每隔一秒判断一次当前需解析并显示哪一个模板文件
     *********************/
    private class ChangeViewThread extends Thread {
        @Override
        public void run() {
            while (isChangeView) {
                String playTxt = readPlaylist();
                if (!Const.currentPlay.equals(playTxt)) {
                    Const.currentPlay = play;
                    ((CrashApplication) PlayerActivity.context.getApplicationContext()).removeAllActivity();
                    LogUtil.e("切换播表", play);
                }
                try {
                    Thread.sleep(1000 * 60 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 实时读取playlisy.xml,判断当前应该播放哪个播表，如果与当前播放的播表不同，则切换
     */
    String play = "";
    private String readPlaylist() {
        try {
            PlayDateBean playDateBean = Const.parseXml.getPlayDate();
            if (playDateBean.getDayList() != null && playDateBean.getDayList().size() > 0) {
                iterator = playDateBean.getDayList().listIterator();
                while (iterator.hasNext()) {
                    PlayDayBean item = iterator.next();
                    // 获取当前时间
                    String dTime = item.getdTime();
                    String sDate = dTime.substring(1, dTime.length());
                    String yy = sDate.substring(0, 4);
                    String mm = sDate.substring(4, 6);
                    String dd = sDate.substring(6, 8);
                    sDate = yy + "-" + mm + "-" + dd;
                    if (PubUtil.isCurrentDay(sDate)) {
                        playlist_dir = item.getFile().toLowerCase();
                        String currentP[] = playlist_dir.split("/");
                        //                        Const.currentPlay = currentP[3];
                        play = currentP[3];
                        isDefault = true;
                        //                        initPlayData(playlist_dir);
                        break;
                    }
                    isDefault = false;
                }
            } else {
                isDefault = false;
            }
            if (!isDefault) {
                iterator = playDateBean.getDefaultlist().listIterator();
                playlist_dir = playDateBean.getDefaultlist().get(0).getFile().toLowerCase();
                String currentP[] = playlist_dir.split("/");
                play = currentP[3];
                //Const.currentPlay = currentP[3];
                //initPlayData(playlist_dir);
            }
            //}
        } catch (Exception e) {
            LogUtil.e("无法找到播表", e.toString());
        }
        return play;
    }

    /**
     * 关闭线程changeViewThread<br>
     */
    public void closeViewThread() {
        try {
            if (null != changeViewThread) {
                isChangeView = false;
                changeViewThread.interrupt(); //服务销毁时，线程跟着停止。
                try {
                    changeViewThread.join(3000);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG, e);
                }
                changeViewThread = null;//通过赋0使GC可以回收资源
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "closeViewThread err", e);
        }
    }

//    private void stopVedio(){
//        if (MLiveDialog.instance != null) {
//            MLiveDialog.instance.finish();//关闭直播
//        }
//        if ( ViewManager.getInstance().getVideoInfo() != null) {
//            LogUtil.e("-----更新布局-----","=========关闭直播");
//            ViewManager.getInstance().getVideoInfo().getmVideo().onStop();
//            ViewManager.getInstance().getVideoInfo().getmVideo().setVisibility(View.GONE);
//        }
//        if ( ViewManager.getInstance().getSurfaceInfo() != null){
//            LogUtil.e("-----更新布局-----","=========关闭录播");
//            ViewManager.getInstance().getSurfaceInfo().onstop();
//            ViewManager.getInstance().getSurfaceInfo().getView(viewManager.getActivity()).setVisibility(View.GONE);
//        }
//        container.removeAllViews();
//    }
//
//    /**
//     * 判断是否有直播播放
//     */
//    private void startVedio(){
//        String flag = UserInfoSingleton.getIslive();
//        if (flag.equals(ConstantValue.PLAY_LIVE)) {
//            LogUtil.e(TAG, "启动直播播放");
//            UserInfoSingleton.setIslive(ConstantValue.PLAY_LIVE);
//            changeStream();
//
//        }
//    }

}
