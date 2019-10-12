package com.ghts.player.data;

import android.content.Context;
import android.util.Log;

import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lijingjing on 17-8-15.
 * 上传设备的状态
 */
public class CmdPublishStatus extends CmdServiceBase implements Runnable {
    private String IPAddress;
    private int Port;
    private Context context;
    private boolean isSuccess = false;
    Socket socket;
    private InetSocketAddress isa = null;
    private static final int TIMEOUT = 15 * 1000;

    public CmdPublishStatus(Context context, String IPAddress, int port) {
        this.context = context;
        this.IPAddress = IPAddress;
        this.Port = port;
    }

    @Override
    public void run() {
        // 循环发送请求和接收响应
        try{
            buildConnect();   //建立与服务器的连接
            while (true) {
                try {
                    if (isSuccess) {
                        senddata(makelocalPacket(context), socket);
                        acceptdata(socket);
                    }else {
                        buildConnect();
                    }
                    Thread.sleep(Const.buCheckInterval * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 建立与服务器的连接并发送请求
     **/
    public void buildConnect() {
        try {
            LogUtil.i("Data", "tcp connect ,ip = " + IPAddress + "Port = " + Port);
            Log.i("Data", "连接请求已发送");
            if (socket != null && socket.isConnected() && (!socket.isClosed())) {  //表明当前连接成功
                Log.i("Data", "连接成功，准备发送信息");
                isSuccess = true;
            } else {
                try {
                    Log.i("Data", "断开连接，重新连接");
                    Thread.sleep(1000);
                    socket = new Socket();
                    isa = new InetSocketAddress(IPAddress, Port);
                    socket.connect(isa, TIMEOUT);

                    if (socket.isConnected() && (!socket.isClosed())) {
                        Log.i("Data", "尝试重新连接成功");
                        isSuccess = true;
                        acceptdata(socket);
                    } else {
                        Log.i("Data", "尝试重新连接失败");
                        isSuccess = false;
                        return;
                    }
                    return;
                } catch (InterruptedException e) {
                    LogUtil.e("-CmdPublishStatus-", e.toString());
                }
            }
        } catch (IOException e) {
            isSuccess = false;
            LogUtil.e("-CmdPublishStatus-", e.toString());
        }
    }

    /**
     * 发送数据
     **/
    public void senddata(byte[] senddata, Socket connectsocket1) {
        try {
            ByteArrayInputStream byteArrayinputstream = new ByteArrayInputStream(senddata);
            if (byteArrayinputstream != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(byteArrayinputstream));
                if (connectsocket1.isClosed()) {
                    Log.i("Data", "向服务器发送数据,发送时间=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    connectsocket1.close();
                } else {
                    connectsocket1.getOutputStream().write(senddata);
                    Log.i("Data", "发送消息成功" + senddata.length);
                }
            }
        } catch (IOException e) {
            resetSocket();
        }
    }

    /**
     * 接收数据
     **/
    public byte[] acceptdata(Socket connectsocket) {
        byte[] acceptdata = null;
        try {
            InputStream istream = null;
            if (connectsocket.isConnected()) {
                if (!connectsocket.isInputShutdown()) {
                    istream = connectsocket.getInputStream();
                    int datalength = istream.available();
                    Log.i("Data-1", "接收到的字节数" + datalength);
                    if (datalength > 0) {
                        acceptdata = new byte[datalength];
                        istream.read(acceptdata);
                        if (acceptdata != null) {
                            isSuccess = true;
                            //解析接收到的数据
                            Log.i("Data", "the lastresult=" + acceptdata.length);
                            parseHead(acceptdata);
                        }
                    }
                } else {
                    isSuccess = false;
                    Log.i("Data", "connectsocket 失败 isInputShutdown=false");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return acceptdata;
    }

    public void resetSocket() {
        while (isServerClose(socket)) {
            try {
                Thread.sleep(Const.buCheckInterval * 1000);
                socket = new Socket(IPAddress, Port);
            } catch (InterruptedException e) {
            } catch (IOException e) {
                System.out.println("正在重连....");
            }
        }
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     * @param socket
     * @return
     */
    public static Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0);
            return false;
        } catch (Exception se) {
            return true;
        }
    }

}