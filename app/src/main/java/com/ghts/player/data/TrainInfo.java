package com.ghts.player.data;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ghts.player.bean.InfoBean;
import com.ghts.player.utils.ConstantValue;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class TrainInfo implements Runnable {
    private String ip;
    private int port_server;
    private Context mContext;
    private ByteArrayInputStream baInPacketHeader;
    private DatagramPacket pkt_in;
    private DatagramSocket socket;//车站组播

    public TrainInfo(Context context, String ip, int serverPort) {
        mContext = context;
        this.ip = ip;
        port_server = serverPort;
    }
    @Override
    public void run() {
        byte[] buf_in = new byte[15000];
        try {
            socket = new DatagramSocket(port_server);
            pkt_in = new DatagramPacket(buf_in, buf_in.length);
            while (true) {
                socket.receive(pkt_in);
                int recvLength = pkt_in.getLength();
                 byte[] buf_recv = pkt_in.getData();

                String s = new String(buf_recv,0, recvLength,"GBK");
                Log.e("收到消息",s);
                if (s != null) {
                    Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                    myIntent.putExtra("cmd", ConstantValue.CMD_TEXT_TRAININFO);
                    myIntent.putExtra(ConstantValue.EXTRA_OBJ, s);
                    mContext.sendBroadcast(myIntent);
                 }
                 Log.e("c",s);
                Gson gson = new Gson();
                InfoBean infoBean = gson.fromJson(s, InfoBean.class);
             }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
