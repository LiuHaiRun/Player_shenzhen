package com.ghts.player.data_ex;

import android.util.Log;

import com.ghts.player.utils.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * 发送消息线程
 */
public class SendMsg implements Runnable {

    private byte[] msgType;
    private SocketAddress mSocketAddress;

    public SendMsg(byte[] msgType, SocketAddress mSocketAddress) {
        this.msgType = msgType;
        this.mSocketAddress = mSocketAddress;
    }

    @Override
    public void run() {
        try {
            if (msgType != null) {
                DatagramSocket sendS = new DatagramSocket();
                sendS.setReuseAddress(true);
                DatagramPacket packet2 = new DatagramPacket(msgType, msgType.length, mSocketAddress);//
                sendS.send(packet2);
                LogUtil.e("--回复数据成功--", "");
                sendS.close();
             }
        } catch (Exception e) {
            Log.e("--返回数据失败--",e.toString());
        }
    }


}
