package com.ghts.player.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.ghts.player.bean.ChannelBean;
import com.ghts.player.bean.GlobalBean;
import com.ghts.player.bean.LocalPlayItem;
import com.ghts.player.enumType.POS;
import com.ghts.player.inter.WeakHandler;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.UserInfoSingleton;
import com.ghts.player.widget.BaseModuleInfo;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * 播放本地
 * 播放视频模块的信息类
 */
public class LocalVideoView extends BaseModuleInfo {
    private static final String TAG = "VideoInfo";
    private static Activity activity;
    private LocalPlayItem current_item; // 当前播放视频项
    private LocalPlayItem next_item; // 下一个播放视频项
    // 视频帧率，暂定25
    private int frameRate = 25;
    private Uri uri;
    //当前音量
    private int current = 0;
    //是否正在直播
    public static boolean isStop = false;
    private String live_channel;
    private int live_volume; // 直播音量
    private ArrayList<LocalPlayItem> local_list; // 本地视频播放列表。
    public int volume; // 本地视频播放音量
    private ListIterator<LocalPlayItem> iterator;
    private String path;
    private AudioManager mAudioManager;
    private int max;
    private static Context mContext;
    private Thread receiveThread = null;
    private static boolean isLive = false; // 是否直播
    //开启UDP组播
    private static final int CMD_OPEN_UDP = 0;
    //检测UDP组播
    private static final int CMD_CHECK_UDP = 1;
    private String livename;
    public static String live_url;//地址
    public static boolean isCmdService = false;
    boolean isHasData = false;
    VlcVideo mVideo;

    @Override
    public VlcVideo getView(Context context) {
        mVideo = new VlcVideo(context);
        mContext = context;
        if (mContext instanceof Activity) {
            activity = (Activity) mContext;
        }
        mVideo.setModule_type(module_type);
        mVideo.setzOrder(zOrder);
        mVideo.setModule_name(module_name);
        // 设置控件的位置
        initPosition(mVideo, pos);
        //AudioManager类提供访问控制音量和钤声模式的操作
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, PubUtil.parseInt(Const.AudioVolume) * max / 255, 0);

        mVideo.init(local_list, pos);
        LogUtil.e("---zhibo--" + max, UserInfoSingleton.getIslive());
        if (isLive) {//检测直播
            liveCheck();
        }
        return mVideo;
    }

    public VlcVideo getViewSimple() {
        return mVideo;
    }

    public void liveCheck() {
        if (Const.globalBean == null) {
            Const.globalBean = GlobalBean.getInstance();
        }
        ArrayList<ChannelBean> channelBeen = Const.globalBean.getLiveList();
        if (channelBeen != null && channelBeen.size() > 0) {
            ListIterator<ChannelBean> listIterator = channelBeen.listIterator();
            while (listIterator.hasNext()) {
                ChannelBean channelBean = listIterator.next();
                String channelBeanName = channelBean.getName();
                if (channelBeanName.equals(livename)) {
                    String srcIP = channelBean.getSrcIP();
                    String srcPort = channelBean.getSrcPort();
                    String packetSize = channelBean.getPacketSize();
                    String liveIP = channelBean.getLiveIP();
                    String livePort = channelBean.getLivePort();
                    String trainIP = channelBean.getTrainIP();
                    String trainPort = channelBean.getTrainPort();
                    Const.globalBean.setSrcIP(srcIP);
                    Const.globalBean.setSrcPort(srcPort);
                    Const.globalBean.setPacketSize(packetSize);
                    Const.globalBean.setLiveIP(liveIP);
                    Const.globalBean.setLivePort(livePort);
                    Const.globalBean.setTrainPort(trainPort);
                    Const.globalBean.setTrainIP(trainIP);
                    Const.globalBean.setName(channelBeanName);
                    break;
                }
            }
        }
        String ip, port;
        if (Const.globalBean.getIsTrain().equals("1") && Const.globalBean.getForwardMode().equals("0")) {
            //车载单播
            Const.globalBean.setIsTrain("1");
            Const.globalBean.setForwardMode("0");
            ip = Const.getHostIP();
            port = Const.globalBean.getTrainPort();
            isLive = true;
            live_url = "127.0.0.1:12306";
        } else if (Const.globalBean.getIsTrain().equals("1") && Const.globalBean.getForwardMode().equals("1")) {
            //车载组播
            ip = Const.globalBean.getTrainIP();
            port = Const.globalBean.getTrainPort();
            live_url = ip + ":" + port;
            LogUtil.e("--车载组播--", live_url);
            mHandler.sendEmptyMessageDelayed(CMD_OPEN_UDP, 7000);
            mHandler.sendEmptyMessageDelayed(CMD_CHECK_UDP, 10000);
        } else if (Const.globalBean.getIsTrain().equals("0")) {
            //车站组播
            ip = Const.globalBean.getSrcIP();
            port = Const.globalBean.getSrcPort();
            live_url = ip + ":" + port;
            LogUtil.e("--车站组播--", live_url);
            mHandler.sendEmptyMessageDelayed(CMD_OPEN_UDP, 7000);
            mHandler.sendEmptyMessageDelayed(CMD_CHECK_UDP, 10000);
        }
    }

    /**
     * 关闭检测组播是否正常的线程<br>
     */
    public void closeReceiveThread() {
        try {
            if (receiveThread != null) {
                receiveThread.interrupt();
                try {
                    receiveThread.join(3000);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG, "InterruptedException", e);
                }
                receiveThread = null;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "closeReceiveThread fund error ", e);
        }
    }

    /**
     * 检测直播(组播)是否正常
     */
    public void checkUdp() {
        closeReceiveThread();
        receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MulticastSocket dataSocket = null;
                DatagramPacket dataPacket = null;
                try {
                    if (null == live_url)
                        return;
                    String[] urls = live_url.split(":");
                    LogUtil.e("--live_url--", live_url);
                    if (urls.length < 2)
                        return;
                    InetAddress address = InetAddress.getByName(urls[0]);
                    dataSocket = new MulticastSocket(Integer.parseInt(urls[1]));
                    dataSocket.joinGroup(address);
                    byte[] receiveByte = new byte[1024 * 2];
                    dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
                    LogUtil.i(TAG, "UDP服务启动...");
                    int size = 0;
                    while (!receiveThread.isInterrupted()) {// 无数据，则循环
                        if (size == 0) {
                            isHasData = false;
                            dataSocket.receive(dataPacket);
                            size = dataPacket.getLength();
                            if (size > 0) {
                                isHasData = true;
                                size = 0;
                            } else {
                                LogUtil.i(TAG, "TS流停止发送数据");
                            }
                            Thread.sleep(50);
                        }
                    }
                } catch (InterruptedException e) {
                    LogUtil.e(TAG, "InterruptedException", e);
                } catch (Exception e) {
                    LogUtil.e(TAG, "出现异常", e);
                } finally {
                    if (dataSocket != null) {
                        try {
                            dataSocket.close();
                        } catch (Exception e) {
                            LogUtil.e(TAG, "出现异常2", e);
                        }
                    }
                }
            }
        }, "CheckMulticastThread");
        receiveThread.start();
    }

    private final Handler mHandler = new MainHandler(this);
    private static class MainHandler extends WeakHandler<LocalVideoView> {
        int countFail = 0;
        boolean isLocal = false;

        public MainHandler(LocalVideoView owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            LocalVideoView info = getOwner();
            if (info == null)
                return;
            //            LogUtil.i(TAG, "接收CMD" + msg.what);
            switch (msg.what) {
                case CMD_OPEN_UDP:
                    //ForwardMode="2" 0:单播 1:组播
                    if (Const.globalBean.getIsTrain().equals("1") && Const.globalBean.getForwardMode().equals("1")) {
                        //车载组播
                        info.checkUdp();
                        Log.e("--车载组播--", "车载组播");
                    } else if (Const.globalBean.getIsTrain().equals("0")) {
                        //车站组播
                        info.checkUdp();
                    }
                    break;
                case CMD_CHECK_UDP:
                    if (isLive) {
                        boolean flag3 = info.isHasData;
                        //                        LogUtil.i(TAG, "检测直播" + flag3);
                        if (isCmdService) {
                            if (flag3) {
                                flag3 = false;
                            }
                        }
                        if (!flag3) {
                            countFail++;
                            if (countFail >= 6) {
                                //                                LogUtil.e(TAG, "直播中断,播放本地");
                                Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                                mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_PLAYLOCAL);
                                mContext.sendBroadcast(mIntent);
                            }
                        } else {
                            countFail = 0;
                            //播放直播
                            //                            LogUtil.e(TAG, "直播恢复,播放直播");
                            Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                            mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_PLAYSTREAM);
                            mContext.sendBroadcast(mIntent);
                        }
                        info.mHandler.sendEmptyMessageDelayed(CMD_CHECK_UDP, 1000);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 初始化控件的位置
     */
    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(pos.getTop());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }


    public String getLive_channel() {
        return live_channel;
    }

    public void setLive_channel(String live_channel) {
        this.live_channel = live_channel;
    }

    public int getLive_volume() {
        return live_volume;
    }

    public void setLive_volume(int live_volume) {
        this.live_volume = live_volume;
    }

    public ArrayList<LocalPlayItem> getLocal_list() {
        return local_list;
    }

    public void setLocal_list(ArrayList<LocalPlayItem> local_list) {
        this.local_list = local_list;
    }

    public String getLivename() {
        return livename;
    }

    public void setLivename(String livename) {
        this.livename = livename;
    }

    public VlcVideo getmVideo() {
        return mVideo;
    }

    public void setmVideo(VlcVideo mVideo) {
        this.mVideo = mVideo;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
    }

    public static String getLive_url() {
        return live_url;
    }

}
