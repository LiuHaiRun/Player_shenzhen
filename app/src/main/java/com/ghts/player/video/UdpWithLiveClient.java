package com.ghts.player.video;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.ghts.player.inter.WeakHandler;
import com.ghts.player.utils.CommonUtil;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author ljj
 * @version 1.0
 * @Date 2018/3/29
 */
public class UdpWithLiveClient implements Runnable {
    private static final String TAG = "UcpWithLiveClient";
    private static boolean isBegin = false;
    private final int serverPort2 = 12306;
    private String serverIP = "127.0.0.1";
    public static Context context;
    static DatagramSocket socket = null;
    public volatile static boolean threadRun = false;
    private static final int CMD_CHECK_SINGLE = 1;
    private short cmdId;
    static int TTL = 0;
    static int oldTtl = 0;

    public UdpWithLiveClient(Context context) {
        this.context = context;
        mHandler.sendEmptyMessageDelayed(CMD_CHECK_SINGLE, 10000);

    }

    @Override
    public void run() {
        LogUtil.i(TAG, "TcpWithLiveClient.run() -- start!");
        try {
            while (!CommonUtil.isProcessRun("liveclient.bin")) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    LogUtil.e(TAG, e);
                }
            }
            LogUtil.i(TAG, "2");
            byte[] receiveByte = new byte[1316];
             DatagramSocket  socket = new DatagramSocket(PubUtil.parseInt(Const.globalBean.getTrainPort()));
            DatagramPacket dataPacket = new DatagramPacket(receiveByte ,receiveByte.length);
            LogUtil.i(TAG, " read socket begin");
            int size = 0;
            while (threadRun) {
                if (size == 0) {
                    socket.receive(dataPacket);
                    size = dataPacket.getLength();
                    if (size > 0) {
                        sendControInfo(receiveByte);
                        if(TTL < 2147483647){
                            TTL++;
//                            LogUtil.e(TAG+"--ttl---",TTL+"");
                        }else{
                            TTL = 1;
                        }
                        size = 0;
                    }
                }
            }
        }catch (IOException e) {
//            e.printStackTrace();
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                    socket = null;
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e);
            }
        }
    }

    DatagramSocket sendSocket;
    public void sendControInfo(byte[] configInfo) {
        try {
            if(sendSocket == null) {
                sendSocket = new DatagramSocket();
            }
            InetAddress ip = InetAddress.getByName(serverIP);  //即目的IP
            DatagramPacket sendPacket = new DatagramPacket(configInfo, configInfo.length, ip, serverPort2);// 创建发送类型的数据报：
            sendSocket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (sendSocket != null) {
                    sendSocket.close();
                    sendSocket = null;
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e);
            }
        }
    }
    private final Handler mHandler = new MainHandler(this);
    private static class MainHandler extends WeakHandler<UdpWithLiveClient> {
        int countFail = 0;
        public MainHandler(UdpWithLiveClient owner) {
            super(owner);
        }
        @Override
        public void handleMessage(Message msg) {
            UdpWithLiveClient info = getOwner();
            if (info == null)
                return;
            switch (msg.what) {
                case CMD_CHECK_SINGLE:
                    //                        boolean flag3 = info.isHasData;
                    //                        LogUtil.i(TAG+TTL, "检测直播" + oldTtl);
                    if (oldTtl == TTL) {
                        countFail++;
                        if (countFail >= 10) {
//                            LogUtil.e(TAG, "直播中断,播放本地");
                            Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                            mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_PLAYLOCAL);
                            context.sendBroadcast(mIntent);
                        }
                    } else {
                        countFail = 0;
                        //播放直播
//                        LogUtil.e(TAG, "直播恢复,播放直播");
                        Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                        mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_PLAYSTREAM);
                        context.sendBroadcast(mIntent);
                    }
                    oldTtl = TTL;
                    info.mHandler.sendEmptyMessageDelayed(CMD_CHECK_SINGLE, 1000);
                    break;
            }
            super.handleMessage(msg);
        }
    }


};