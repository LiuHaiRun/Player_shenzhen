package com.ghts.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import com.ghts.player.application.MyApplication;
import com.ghts.player.bean.GlobalBean;
import com.ghts.player.data.AtsInfo;
import com.ghts.player.data.CmdPublishStatus;
import com.ghts.player.data.CmdQueryTask;
import com.ghts.player.data.TrainInfo;
import com.ghts.player.data.WeatherInfo;
import com.ghts.player.data_ex.CmdQueryTaskHz;
import com.ghts.player.utils.CommonUtil;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.ParseJson;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.ShellUtils;
import com.ghts.player.utils.UserInfoSingleton;
import com.ghts.player.video.UdpWithLiveClient;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPortUtil;

import static com.ghts.player.activity.PlayerActivity.context;

/**
 * Created by lijingjing on 17-8-8.
 */
public class RecvService extends Service {

    private CmdQueryTask cmdQueryTask;  //检测心跳
    private Thread queryTask_Thread;
    private CmdPublishStatus tcpReceive; //接收cmdService发送的消息
    private Thread tcp_Thread;
    private AtsInfo atsStrainInfo; //接收ATS信息
    private Thread ats_Thread;
    private WatchThread watchThread;//检测守护程序
    private CheckCThread checkCThread;//检测c++程序是否挂起
    private Thread mGpioThread;  //检测GPIO口是否触发紧急消息
    private Thread closeScreenThread = null; //关屏
    private boolean isClose, isOpen;
    private Thread openScreenThread = null; //开屏
    private CmdQueryTaskHz mCmdQueryTask_ex;  //检测心跳
    private UdpWithLiveClient runnable_udpWithLiveclient;
    private Thread thread_udpWithLiveclient;
    //从数据库获取紧急消息
    private static GetParamThread paramThread;
    String IPAddress;
    int port;
    ParseJson parseJson;
    private boolean isSendClose, isSendOpen;  //判断当前是否应该一直发送开关屏

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getHostIP();

        if (Const.globalBean.getIsTrain().equals("0")) {
            isClose = true;
            isSendClose = false;
            if (closeScreenThread == null) {
                closeScreenThread = new CloseScreenThread();
                closeScreenThread.setName("closeScreenThread");
            }
            closeScreenThread.start();
            isOpen = true;
            isSendOpen = false;
            if (openScreenThread == null) {
                openScreenThread = new openScreenThread();
                openScreenThread.setName("openScreenThread");
            }
            openScreenThread.start();
        }
    }

    public void startService() {
        parseJson = new ParseJson();

        String IPAddress = GlobalBean.getInstance().getCmdServiceIP();
        int Port = PubUtil.parseInt(GlobalBean.getInstance().getCmdServicePort());
        tcpReceive = new CmdPublishStatus(this, IPAddress, Port);
        tcp_Thread = new Thread(tcpReceive, "---");
        tcp_Thread.start();

        cmdQueryTask = new CmdQueryTask(this, IPAddress, Port);
        queryTask_Thread = new Thread(cmdQueryTask, "---");
        queryTask_Thread.start();

        //兼容3.8.0.3版本playctrl
        mCmdQueryTask_ex = new CmdQueryTaskHz(this, IPAddress, 7610);
        queryTask_Thread = new Thread(mCmdQueryTask_ex, "---");
        queryTask_Thread.start();

        String atsIp = GlobalBean.getInstance().getMCIP();
        String atsPort = GlobalBean.getInstance().getMCPort();
        atsStrainInfo = new AtsInfo(this, atsIp, PubUtil.parseInt(atsPort));
        atsStrainInfo.isStop = true;
        ats_Thread = new Thread(atsStrainInfo, "---");
        ats_Thread.start();

        checkCThread = new CheckCThread();
        checkCThread.start();

        watchThread = new WatchThread();
        watchThread.start();

        //--------------------福州修改---------------------------------------
        //实时滚动文本
//        paramThread = new GetParamThread();
//        paramThread.start();

//        TrainInfo trainInfo = new TrainInfo(this, "", 7670);
//        Thread thread = new Thread(trainInfo);
//        thread.start();

//        WeatherInfo weatherInfo = new WeatherInfo(this, Const.WeatherIp, Const.WeatherPort);
//        weatherInfo.isStop = true;
//        Thread weatherThread = new Thread(weatherInfo, "---");
//        weatherThread.start();

        if (Const.globalBean.getIsTrain().equals("1") && Const.globalBean.getForwardMode().equals("0")) {
            runnable_udpWithLiveclient = new UdpWithLiveClient(this);
            thread_udpWithLiveclient = new Thread(runnable_udpWithLiveclient, "TCPRecvThread");
            thread_udpWithLiveclient.start();
            UdpWithLiveClient.threadRun = true;
        }
    }

    public String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        startService();
                        Log.e("-IP设置成功-", hostIp);
                        break;
                    } else {
                        //                        Log.e("-等待IP设置成功-", "无法获取IP");
                        Thread.sleep(5000);
                    }
                }
            }
        } catch (Exception e) {

        }
        return hostIp;
    }

    public class GetParamThread extends Thread {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String url = "http://" + Const.globalBean.getCenterIP() + "/getrealtimeinfo.php?linecode=" + Const.globalBean.getLineCode() + "&stationcode=" + Const.globalBean.getStationName();
                //String url = "http://192.168.3.10/getrealtimeinfo.php?linecode=L001&stationcode=111";
                parseJson.xGetParam(url, Const.globalBean.getLineCode(), Const.globalBean.getStationCode());
            }
        };

        @Override
        public void run() {
            super.run();
            timer.schedule(task, 30 * 1000, 1 * 60 * 1000);
        }
    }

    /******************************
     * 定时检测c++程序
     *********************************************************************/
    private void initC() {
        if (Const.globalBean.getInitC() != null && Const.globalBean.getInitC().size() > 0) {
            ArrayList<String> initC = new ArrayList<String>();
            initC.clear();
            initC = Const.globalBean.getInitC();
            for (int i = 0; i < initC.size(); i++) {
                String name = initC.get(i);
                if (!CommonUtil.isProcessRun(name)) {
                    ShellUtils.CommandResult cr = ShellUtils.execCommand("/sata/initc.sh", ShellUtils.checkRootPermission());
                    //LogUtil.i("运行脚本文件", "successMsg=" + cr.successMsg + "-result=" + cr.result + "-errorMsg=" + cr.errorMsg);
                    return;
                }
            }
        }
    }

    /**
     * watchplayer监测
     */
    public class WatchThread extends Thread {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!CommonUtil.isServiceRunning(RecvService.this, "com.ghts.watchplayer.WatchService")) {
                    PubUtil.doStartApplicationWithPackageName(RecvService.this, "com.ghts.watchplayer");
                } else {
                }
            }
        };

        @Override
        public void run() {
            super.run();
            timer.schedule(task, 0, 1000 * 60 * 5);
        }
    }

    /**
     * c++监测
     */
    public class CheckCThread extends Thread {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                initC();
            }
        };

        @Override
        public void run() {
            super.run();
            timer.schedule(task, 2000, 1000 * 60 * 2);
        }
    }

    boolean GPIO1 = false;
    boolean GPIO2 = false;

    private void Handle_GPIO_Input(int i) {
        if (i == 1) {
            int rect = PubUtil.ReadmGpio("ARM_onoff_GPI1");
            //            LogUtil.e("---GPI1状态----", rect + "");
            if (rect == 0) {
                String txt = PubUtil.readTxt("/sata/GPIOEmcrawl0.txt");
                if (!UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES) && !TextUtils.isEmpty(txt)) {
                    GPIO1 = true;
                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    myIntent.putExtra("cmd", ConstantValue.CMD_GPIO_SETPROIRITY);
                    myIntent.putExtra(ConstantValue.EXTRA_OBJ, txt);
                    context.sendBroadcast(myIntent);
                }
            } else if (rect == 1 && GPIO1) {
                if (UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES)) {
                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    myIntent.putExtra("cmd", ConstantValue.CMD_TEXT_STOP);
                    context.sendBroadcast(myIntent);
                    GPIO1 = false;
                }
            }
        } else if (i == 2) {
            int rect2 = PubUtil.ReadmGpio("ARM_onoff_GPI2");
            //            LogUtil.e("---GPI2状态----", rect2 + "");
            if (rect2 == 0) {
                String txt = PubUtil.readTxt("/sata/GPIOEmcrawl1.txt");
                if (!UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES) && !TextUtils.isEmpty(txt)) {
                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    myIntent.putExtra("cmd", ConstantValue.CMD_GPIO_SETPROIRITY);
                    myIntent.putExtra(ConstantValue.EXTRA_OBJ, txt);
                    context.sendBroadcast(myIntent);
                    GPIO2 = true;
                }
            } else if (rect2 == 1 && GPIO2) {
                if (UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES)) {
                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    myIntent.putExtra("cmd", ConstantValue.CMD_TEXT_STOP);
                    context.sendBroadcast(myIntent);
                    GPIO2 = false;
                }
            }
        }
    }

    private class CloseScreenThread extends Thread {
        Time time = Const.globalBean.getCloseScreenTime();
        long currentTimeMillis = 0;
        Time current_time = null;
        @Override
        public void run() {
            while (isClose) {
                currentTimeMillis = System.currentTimeMillis();
                current_time = new Time();
                current_time.set(currentTimeMillis);
                if ((timeToMillis(current_time) >= timeToMillis(time)) && (timeToMillis(current_time) <= timeToMillis(time) + 3 * 60 * 1000)) {
                    //                if (timeToMillis(current_time) == timeToMillis(time)) {
                    isSendClose = true;
//                    if (!SerialPortUtil.flag) {
//                        SerialPortUtil.openSrialPort();
//                    }
//                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
//                    myIntent.putExtra("cmd", ConstantValue.CMD_MT_SCREEN_OFF);
//                    MyApplication.getAppContext().sendBroadcast(myIntent);

                    Intent myIntent2 = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    myIntent2.putExtra("cmd", ConstantValue.MT_POWER_OFF);
                    MyApplication.getAppContext().sendBroadcast(myIntent2);
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                    }
                    LogUtil.e("#####执行关屏#####", "CloseScreenReceiver");
                } else if (timeToMillis(current_time) > timeToMillis(time) + 3 * 60 * 1000) {
                    isClose = false;
                    isSendClose = false;
                    closeViewThread();
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
        }
    }

    private class openScreenThread extends Thread {
        Time time = Const.globalBean.getOpenScreenTime();
        long currentTimeMillis = 0;
        Time current_time = null;

        @Override
        public void run() {
            while (isOpen) {
                currentTimeMillis = System.currentTimeMillis();
                current_time = new Time();
                current_time.set(currentTimeMillis);
                if ((timeToMillis(current_time) >= timeToMillis(time)) && (timeToMillis(current_time) <= timeToMillis(time) + 5 * 60 * 1000)) {
                    //                    if (timeToMillis(current_time) == timeToMillis(time)) {
//                    if (!SerialPortUtil.flag) {
//                        SerialPortUtil.openSrialPort();
//                    }
                    LogUtil.e("#####执行开屏#####", "OpenScreenReceiver");
//                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
//                    myIntent.putExtra("cmd", ConstantValue.CMD_MT_SCREEN_ON);
//                    MyApplication.getAppContext().sendBroadcast(myIntent);

                    Intent myIntent2 = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    myIntent2.putExtra("cmd", ConstantValue.MT_POWER_ON);
                    MyApplication.getAppContext().sendBroadcast(myIntent2);
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                    }
                } else if (timeToMillis(current_time) > timeToMillis(time) + 3 * 60 * 1000) {
                    isOpen = false;
                    isSendOpen = false;
                    openViewThread();
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }
    }
    //    private class CloseScreenThread extends Thread {
    //        Time time = Const.globalBean.getCloseScreenTime();
    //        long currentTimeMillis = 0;
    //        Time current_time = null;
    //
    //        @Override
    //        public void run() {
    //            while (isClose) {
    //                currentTimeMillis = System.currentTimeMillis();
    //                current_time = new Time();
    //                current_time.set(currentTimeMillis);
    //                if (timeToMillis(current_time) == timeToMillis(time)) {
    //                    if (!SerialPortUtil.flag) {
    //                        SerialPortUtil.openSrialPort();
    //                    }
    //                    LogUtil.e("#####执行关屏#####", "CloseScreenReceiver");
    //                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
    //                    myIntent.putExtra("cmd", ConstantValue.CMD_MT_SCREEN_OFF);
    //                    MyApplication.getAppContext().sendBroadcast(myIntent);
    //                    isClose = false;
    //                    closeViewThread();
    //                } else {
    //                    try {
    //                        Thread.sleep(1000);
    //                    } catch (InterruptedException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //            }
    //        }
    //    }
    //
    //    private class openScreenThread extends Thread {
    //        Time time = Const.globalBean.getOpenScreenTime();
    //        long currentTimeMillis = 0;
    //        Time current_time = null;
    //
    //        @Override
    //        public void run() {
    //            while (isOpen) {
    //                currentTimeMillis = System.currentTimeMillis();
    //                current_time = new Time();
    //                current_time.set(currentTimeMillis);
    //                if (timeToMillis(current_time) == timeToMillis(time)) {
    //                    if (!SerialPortUtil.flag) {
    //                        SerialPortUtil.openSrialPort();
    //                    }
    //                    LogUtil.e("#####执行开屏#####", "CloseScreenReceiver");
    //                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
    //                    myIntent.putExtra("cmd", ConstantValue.CMD_MT_SCREEN_ON);
    //                    MyApplication.getAppContext().sendBroadcast(myIntent);
    //                    isOpen = false;
    //                    openViewThread();
    //                } else {
    //                    try {
    //                        Thread.sleep(1000);
    //                    } catch (InterruptedException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //            }
    //        }
    //
    //    }

    private class MyGpioThread extends Thread {
        boolean isfirst = true;
        boolean isCheck = false;

        @Override
        public void run() {
            while (true) {
                if (isCheck && !isfirst) {
                    Handle_GPIO_Input(1);
                    Handle_GPIO_Input(2);
                    isCheck = false;
                } else if (!isCheck && !isfirst) {
                    try {
                        Thread.sleep(10000);
                        isCheck = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (isfirst) {
                    try {
                        Thread.sleep(1000 * 60);
                        isfirst = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private int timeToMillis(Time time) {
        return time.hour * 60 * 60 * 1000 + time.minute * 60 * 1000
                + time.second * 1000;
    }

    public void closeViewThread() {
        try {
            if (null != closeScreenThread) {
                closeScreenThread.interrupt(); //服务销毁时，线程跟着停止。
                closeScreenThread = null;//通过赋0使GC可以回收资源
            }
        } catch (Exception e) {
            LogUtil.e("e", "closeViewThread err", e);
        }
    }

    public void openViewThread() {
        try {
            if (null != openScreenThread) {
                openScreenThread.interrupt(); //服务销毁时，线程跟着停止。
                openScreenThread = null;//通过赋0使GC可以回收资源
            }
        } catch (Exception e) {
            LogUtil.e("e", "closeViewThread err", e);
        }
    }

    //关闭服务
    public void onDestroy() {
        super.onDestroy();
        stopServer();
    }

    public void stopServer() {
        if (tcp_Thread != null) {
            tcp_Thread.interrupt();
            tcp_Thread = null;
        }
        if (queryTask_Thread != null) {
            queryTask_Thread.interrupt();
            queryTask_Thread = null;
        }
        if (ats_Thread != null) {
            ats_Thread.interrupt();
            ats_Thread = null;
        }
        if (checkCThread != null) {
            checkCThread.interrupt();
            checkCThread = null;
        }

        openViewThread();
        closeViewThread();
    }

}