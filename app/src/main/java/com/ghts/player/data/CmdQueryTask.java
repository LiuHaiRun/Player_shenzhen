package com.ghts.player.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lijingjing on 17-8-24.
 * 查询自己的操作任务
 */
public class CmdQueryTask extends CmdServiceBase implements Runnable {
    private String IPAddress;
    private int Port;
    private Context context;
    private boolean isSuccess = false;
    private static long INTERVAL_TIME = 15 * 1000;
    private Socket socket;
    private static String szHead;//头部标识，固定为"Cmd"
    private static int iDataSize;//本结构以及后续所有数据的字节大小
    //内容类型，由CMD_TYPE描述,1表示命令的答复，0表示原始命令,命令服务器设定的监控查询间隔，单位秒
    static byte buType, buAck, buCheckInterval;
    static int iNum;//后续TASK_LIST或者DEVICE_STATUS结构的数量,操作任务的个数
    static String szTaskID;//针对CMD_QUERY_TASK_RESULT查询命令有效.
    static String szDeviceID;//针对CMD_QUERY_TASK查询命令有效
    static List<Tast_Set> tast_setList;
    static Tast_Set tast_set;
    static Thread receiveThread;

    public CmdQueryTask(Context context, String IPAddress, int port) {
        this.context = context;
        this.IPAddress = IPAddress;
        this.Port = port;
    }

    @Override
    public void run() {
        // 循环发送请求和接收响应
        try {
            buildConnect();   //建立与服务器的连接
            while (true) {
                try {
                    if (isSuccess) {
                        senddata(makeQueryTask(), socket);
                        acceptdata(socket);
                    }else {
                        buildConnect();
                    }
                    Thread.sleep(Const.buCheckInterval * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立与服务器的连接并发送请求
     **/
    public void buildConnect() {
        try {
            LogUtil.i("QueryTaskData", "tcp connect ,ip = " + IPAddress + "Port = " + Port);
            Log.i("QueryTaskData", "连接请求已发送");
            if (socket != null && socket.isConnected() && (!socket.isClosed())) {  //表明当前连接成功
                Log.i("QueryTaskData", "连接成功，准备发送信息");
                isSuccess = true;
            } else {
                try {
                    Log.i("QueryTaskData", "断开连接，重新连接");
                    Thread.sleep(1000);
                    socket = new Socket(IPAddress, Port);
                    //                    socket = new Socket(GlobalBean.getGlobalBean().getCmdServiceIP(), PubUtil.parseInt(GlobalBean.getGlobalBean().getCmdServicePort()));
                    if (socket.isConnected() && (!socket.isClosed())) {
                        Log.i("QueryTaskData", "尝试重新连接成功");
                        isSuccess = true;
                        acceptdata(socket);
                    } else {
                        Log.i("QueryTaskData", "尝试重新连接失败");
                        isSuccess = false;
                        return;
                    }
                    return;
                } catch (InterruptedException e) {
                    isSuccess = false;
                    LogUtil.e("-CmdQueryTask-", e.toString());
                }
            }
        } catch (IOException e) {
            isSuccess = false;
            LogUtil.e("-CmdQueryTask-", e.toString());
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
                    Log.i("QueryTaskData", "向服务器发送数据,发送时间=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    connectsocket1.close();
                } else {
                    connectsocket1.getOutputStream().write(senddata);
                    Log.i("QueryTaskData", "发送消息成功" + senddata.length);
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
                    Log.i("QueryTaskData-1", "接收到的字节数" + datalength);
                    if (datalength > 0) {
                        acceptdata = new byte[datalength];
                        istream.read(acceptdata);
                        if (acceptdata != null) {
                            isSuccess = true;
                            //解析接收到的数据
                            Log.e("QueryTaskData", "the lastresult=" + acceptdata.length);
                            ByteArrayInputStream baInPacketData = new ByteArrayInputStream(acceptdata);
                            DataInputStream stream = new DataInputStream(baInPacketData);

                            byte[] buf_temp = new byte[4];
                            stream.read(buf_temp, 0, 4);
                            szHead = new String(buf_temp, 0, 4, GData.charset).trim();
                            iDataSize = stream.readInt();
                            buf_temp = TypeRevert.intToByte2(iDataSize);
                            int iDataSize = TypeRevert.bytesToInt(buf_temp);
                            buType = stream.readByte();
                            buAck = stream.readByte();
                            buCheckInterval = stream.readByte();
                            buf_temp = TypeRevert.intToByte2(stream.readInt());
                            iNum = TypeRevert.bytesToInt(buf_temp);
                            buf_temp = new byte[33];
                            stream.read(buf_temp, 0, 33);
                            szDeviceID = new String(buf_temp, 0, 33, GData.charset).trim();
                            CmdDate cmdDate = new CmdDate();
                            Tast_Set tast_set = cmdDate.GetTextData(acceptdata);

                            if (iNum > 0) {
                                szTaskID = cmdDate.getTast_set().getSzTaskID();
                                if (!Const.TASKID.equals(szTaskID)) {
                                    Const.TASKID = szTaskID;
                                    if (cmdDate.getTast_set().getiTaskType() == GData.MT_PUBLISH_INFO) {////发布紧急信息命令
                                        Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                                        myIntent.putExtra("cmd", ConstantValue.CMD_TEXT_SETPROIRITY);
                                        myIntent.putExtra(ConstantValue.EXTRA_OBJ, cmdDate);
                                        myIntent.putExtra(ConstantValue.EXTRA_type, "new");
                                        context.sendBroadcast(myIntent);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_STOP_PUBLISH) {//取消紧急信息命令
                                        sendMsg(ConstantValue.CMD_TEXT_STOP);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_RESET_DEVICE) {//重新启动命令
                                        sendMsg(ConstantValue.CMD_MT_RESET_DEVICE);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_SOUND_ON) {//开启声音命令
                                        sendMsg( ConstantValue.CMD_CONTROL_SOUNDON);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_SOUND_OFF) {//关闭声音命令
                                        sendMsg( ConstantValue.CMD_CONTROL_SOUNDOFF);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_SET_VOLUME) {//设置音量
                                        Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
                                        mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_SETVOLUME);
                                        mIntent.putExtra(ConstantValue.EXTRA_type, "new");
                                        Bundle bundle = new Bundle();
                                        bundle.putByte("volume", tast_set.getBuLeftVolume());
                                        mIntent.putExtra(ConstantValue.EXTRA_BUNDLE, bundle);
                                        context.sendBroadcast(mIntent);

                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_LIVE_ON) {//开始直播
                                        sendMsg( ConstantValue.CMD_MT_LIVE_ON);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_LIVE_OFF) {//停止直播
                                        sendMsg( ConstantValue.CMD_MT_LIVE_OFF);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    }  else if (cmdDate.getTast_set().getiTaskType() == GData.MT_SCREEN_ON) {//网管软件发布的设备显示屏开机命令
                                        sendMsg(ConstantValue.CMD_MT_SCREEN_ON);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_SCREEN_OFF) {//网管软件发布的设备显示屏关机命令
                                        sendMsg(ConstantValue.CMD_MT_SCREEN_OFF);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_POWER_ON) {//网管软件发布的设备显示屏开机命令
                                        sendMsg(ConstantValue.MT_POWER_ON);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    } else if (cmdDate.getTast_set().getiTaskType() == GData.MT_POWER_OFF) {//网管软件发布的设备显示屏关机命令
                                        sendMsg(ConstantValue.MT_POWER_OFF);
                                        senddata(makeTaskResult(cmdDate.getTast_set().getSzTaskID()), socket);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    isSuccess = false;
                    Log.i("QueryTaskData", "connectsocket 失败 isInputShutdown=false");
                }
            }
        } catch (IOException e) {
            LogUtil.e("接收消息处理失败", e.toString());
        }
        return acceptdata;
    }

    public void sendMsg(short msg) {
        Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
        myIntent.putExtra("cmd", msg);
        context.sendBroadcast(myIntent);
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
     *
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


    public void closeThread() {
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

}