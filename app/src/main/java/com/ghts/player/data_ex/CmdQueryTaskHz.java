package com.ghts.player.data_ex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ghts.player.data.GData;
import com.ghts.player.data.TypeRevert;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


public class CmdQueryTaskHz extends CmdBase implements Runnable {

    private String IPAddress;
    private int Port;
    public Context context;
    private DatagramSocket dataSocket; //车载单播
    private DatagramPacket pkt_in;
    private SocketAddress mSocketAddress;
    private Thread sendThread,playThread,msgThread,contentThread;

    public CmdQueryTaskHz(Context context, String IPAddress, int port) {
        this.context = context;
        this.IPAddress = IPAddress;
        this.Port = port;
    }

    @Override
    public void run() {
        try {
            byte[] buf_in = new byte[1471];
            if (dataSocket == null) {
                dataSocket = new DatagramSocket(null);
                dataSocket.setReuseAddress(true);
                dataSocket.bind(new InetSocketAddress(7610));
            }
            while (true) {
                pkt_in = new DatagramPacket(buf_in, buf_in.length);
                dataSocket.receive(pkt_in);
                LogUtil.e("接收到DLL的数据长度", pkt_in.getLength() + "--");
                mSocketAddress = pkt_in.getSocketAddress();

                int recvLength = pkt_in.getLength();
                byte[] buf_recv = pkt_in.getData();
                assert (buf_recv.length == recvLength);
                ByteArrayInputStream baInPacketHeader = new ByteArrayInputStream(buf_recv, 0, recvLength);
                DataInputStream dInHeader = new DataInputStream(baInPacketHeader);
                acceptdata(dInHeader);
            }
        } catch (Exception e) {
            LogUtil.e("--接收车站数据错误--", e.toString());
        }
    }

    /**
     * 接收数据
     **/
    public void acceptdata(DataInputStream istream) {
        DataInputStream stream = istream;

        try {
            //            UINT uPacketSN;						//包序列号，收到后直接返回
            //            BYTE chCmd;							//命令类型，由NET_CMD_TYPE枚举结构描述
            //            BYTE chOldCmd;						//回复命令时对应的原来的命令
            //            BYTE chResult;						//命令执行结果
            //            int  iRightLevel;					//发送命令时，为发送者和发送设备的权限值
            //            union
            //            {	BOOL bTurnOn;					//开关机命令参数，为0表示关机，非1表示开机
            //                NETDATA_SOUND NetSound;			//声音控制参数
            //                NET_ES_PARAM esParam;			//紧急状态控制参数结构
            //                CHECK_STATUS_PARAM_EX csParam;	//状态检测参数结构
            //                PLAY_CONTENT PlayContent;		//播出内容结构
            //                DATA_OUTPUT DataOutput;			//请求播出画面的数据结构
            //                PLAY_CTRL_PARAM PlayCtrlParam;	//播放控制参数
            //            };
            //            WORD wCRC;							//CRC-CCITT校验值

            final NET_CMD net_cmd = new NET_CMD();
            byte[] buf_temp = new byte[4];

            //            int uPacketSN = stream.readInt();
            //            buf_temp = TypeRevert.intToByte2(uPacketSN);
            //            uPacketSN = TypeRevert.bytesToInt(buf_temp); //包序列号，收到后直接返回
            //            LogUtil.e("包序列号",uPacketSN+"");
            stream.read(buf_temp);
            int uPacketSN = byte2int(buf_temp);
            LogUtil.e("接收到DLL的包序列号", uPacketSN + "");
            byte chCmd = stream.readByte();//命令类型，由NET_CMD_TYPE枚举结构描述
            byte chOldCmd = stream.readByte();//回复命令时对应的原来的命令
            byte chResult = stream.readByte();//命令执行结果
            buf_temp = TypeRevert.intToByte2(stream.readInt());
            int iRightLevel = TypeRevert.bytesToInt(buf_temp);//发送命令时，为发送者和发送设备的权限值
            net_cmd.setChCmd(chCmd);
            net_cmd.setChOldCmd(chOldCmd);
            net_cmd.setChResult(chResult);
            net_cmd.setiRightLevel(iRightLevel);
            net_cmd.setuPacketSN(uPacketSN);
            LogUtil.e("处理接收到DLL的数据", net_cmd.toString());
            String aa = chCmd + "";
//            NET_CMD{uPacketSN=16276, iRightLevel=50, chCmd=6, chOldCmd=0, chResult=0}
            switch (Integer.parseInt(aa)) {
                case NET_CMD_RESET_DEVICE: //设备重启
                    sendMsg(ConstantValue.CMD_MT_RESET_DEVICE);
                    if (sendThread != null) {
                        sendThread.interrupt();
                        sendThread = null;
                    }
                    SendMsg sendMsgreset = new SendMsg(makelocalPacket(context, net_cmd), pkt_in.getSocketAddress());
                    sendThread = new Thread(sendMsgreset);
                    sendThread.start();
                    break;
                case NET_CMD_SWITCH_DEVICE_SCREEN: // 开关屏0表示关屏，非1表示开屏
                    buf_temp = TypeRevert.intToByte2(stream.readInt());
                    int screen = TypeRevert.bytesToInt(buf_temp);
                    //开关机命令参数，为0表示关机，非1表示开机
                    if (screen == 0) {
                        sendMsg(ConstantValue.CMD_MT_SCREEN_OFF);
                        if (sendThread != null) {
                            sendThread.interrupt();
                            sendThread = null;
                        }
                        SendMsg sendMsgreset1 = new SendMsg(makelocalPacket(context, net_cmd), pkt_in.getSocketAddress());
                        sendThread = new Thread(sendMsgreset1);
                        sendThread.start();
                    } else if (screen == 1) {
                        sendMsg(ConstantValue.CMD_MT_SCREEN_ON);
                        if (sendThread != null) {
                            sendThread.interrupt();
                            sendThread = null;
                        }
                        SendMsg sendMsgreset2 = new SendMsg(makelocalPacket(context, net_cmd), pkt_in.getSocketAddress());
                        sendThread = new Thread(sendMsgreset2);
                        sendThread.start();
                    }
                    break;
                case NET_CMD_SWITCH_DEVICE: //设备开关机命令
                    buf_temp = TypeRevert.intToByte2(stream.readInt());
                    int bTurnOn = TypeRevert.bytesToInt(buf_temp);
                    //开关机命令参数，为0表示关机，非1表示开机
                    if (bTurnOn == 0) {
                        sendMsg(ConstantValue.MT_POWER_OFF);
                        if (contentThread != null) {
                            contentThread.interrupt();
                            contentThread = null;
                        }
                        SendMsg sendContentend = new SendMsg(getContent(context, net_cmd), pkt_in.getSocketAddress());
                        contentThread = new Thread(sendContentend);
                        contentThread.start();
                    } else if (bTurnOn == 1) {
                        sendMsg(ConstantValue.MT_POWER_ON);
                        if (contentThread != null) {
                            contentThread.interrupt();
                            contentThread = null;
                        }
                        SendMsg sendContentopen = new SendMsg(getContent(context, net_cmd), pkt_in.getSocketAddress());
                        contentThread = new Thread(sendContentopen);
                        contentThread.start();
                    }
                    break;
                case NET_CMD_SET_CONTENT: // 发送紧急消息
                    parseEmergency(stream, net_cmd);
                    break;
                case NET_CMD_END_CONTENT:  //结束紧急消息
                    sendMsg(ConstantValue.CMD_TEXT_STOP);
                    if (msgThread != null) {
                        msgThread.interrupt();
                        msgThread = null;
                    }
                    SendMsg sendContent2 = new SendMsg(getContent(context, net_cmd), pkt_in.getSocketAddress());
                    msgThread = new Thread(sendContent2);
                    msgThread.start();
                    break;
                case NET_CMD_START_LIVE: //直播控制命令，iParam为0表示结束直播，为1表示开始直播
                    sendMsg(ConstantValue.CMD_MT_LIVE_ON);
                    if (contentThread != null) {
                        contentThread.interrupt();
                        contentThread = null;
                    }
                    SendMsg sendContent5 = new SendMsg(getContent(context, net_cmd), pkt_in.getSocketAddress());
                    contentThread = new Thread(sendContent5);
                    contentThread.start();
                    break;
                case NET_CMD_STOP_LIVE:
                    sendMsg(ConstantValue.CMD_MT_LIVE_OFF);
                    if (contentThread != null) {
                        contentThread.interrupt();
                        contentThread = null;
                    }
                    SendMsg sendContent4 = new SendMsg(getContent(context, net_cmd), pkt_in.getSocketAddress());
                    contentThread = new Thread(sendContent4);
                    contentThread.start();
                    break;
                case NET_CMD_GET_CONTENT:
                    LogUtil.e("--接收到获取状态--", "----");
                    if (contentThread != null) {
                        contentThread.interrupt();
                        contentThread = null;
                    }
                    SendMsg sendContent3 = new SendMsg(getContent(context, net_cmd), pkt_in.getSocketAddress());
                    contentThread = new Thread(sendContent3);
                    contentThread.start();
                    break;
                case NET_CMD_CHECK_STATUS:  //上传状态检测命令
                    if (sendThread != null) {
                        sendThread.interrupt();
                        sendThread = null;
                    }
                    SendMsg sendMsg = new SendMsg(makelocalPacket(context, net_cmd), pkt_in.getSocketAddress());
                    sendThread = new Thread(sendMsg);
                    sendThread.start();
                    break;
                case NET_CMD_SWITCH_SOUND: // 声音开关
                    buf_temp = TypeRevert.intToByte2(stream.readInt());
                    int isburn = TypeRevert.bytesToInt(buf_temp);
                    if (isburn == 0) { //静音
                        LogUtil.e("声音设置", "静音");
                        sendMsg(ConstantValue.CMD_CONTROL_SOUNDOFF);
                    } else if (isburn == 1) {
                        LogUtil.e("声音设置", "声音恢复");
                        sendMsg(ConstantValue.CMD_CONTROL_SOUNDON);
                    }
                    if (sendThread != null) {
                        sendThread.interrupt();
                        sendThread = null;
                    }
                    SendMsg sendMsg1 = new SendMsg(makelocalPacket(context, net_cmd), pkt_in.getSocketAddress());
                    sendThread = new Thread(sendMsg1);
                    sendThread.start();
                    break;
                case NET_CMD_SET_VOLUME:   //声音大小设置
                    setVolume(stream);
                    if (sendThread != null) {
                        sendThread.interrupt();
                        sendThread = null;
                    }
                    SendMsg sendMsg2 = new SendMsg(makelocalPacket(context, net_cmd), pkt_in.getSocketAddress());
                    sendThread = new Thread(sendMsg2);
                    sendThread.start();
                    break;
                case NET_CMD_GET_PLAY_CONTENT: //获取播出内容
                    if (playThread != null) {
                        playThread.interrupt();
                        playThread = null;
                    }
                    SendMsg sendPlayContent = new SendMsg(getPlayContent(context, net_cmd), pkt_in.getSocketAddress());
                    playThread = new Thread(sendPlayContent);
                    playThread.start();
                    break;
                case NET_CMD_GET_OUTPUT:   //获取播出画面
                    if (sendThread != null) {
                        sendThread.interrupt();
                        sendThread = null;
                    }
                    SendMsg sendOutput = new SendMsg(getOutput(context, net_cmd), pkt_in.getSocketAddress());
                    sendThread = new Thread(sendOutput);
                    sendThread.start();
                    break;
            }
        } catch (IOException e) {
            LogUtil.e("--解析接收到的车站数据错误--", e.toString());
        }
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    public static int byte2int(byte[] res) {
        // res = InversionByte(res);
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00); // | 表示安位或
        return targets;
    }

    /**
     * 显示紧急消息内容
     *
     * @param stream
     */
    public void parseEmergency(DataInputStream stream, NET_CMD net_cmd) {
        try {
            int iRightLevel = TypeRevert.bytesToInt(TypeRevert.intToByte2(stream.readInt()));  //权限级别码，数字越大级别越高
            int iEsType = TypeRevert.bytesToInt(TypeRevert.intToByte2(stream.readInt()));
            byte[] buf_temp = new byte[1022];
            stream.read(buf_temp, 0, 1022);
            String szContent = new String(buf_temp, 0, 1022, GData.charset).trim();
            byte iSoundType = stream.readByte();
            byte iLeftVolume = stream.readByte();
            byte iRightVolume = stream.readByte();
            int bFullScreen = TypeRevert.bytesToInt(TypeRevert.intToByte2(stream.readInt()));
            int uTimeLength = TypeRevert.bytesToInt(TypeRevert.intToByte2(stream.readInt()));  //权限级别码，数字越大级别越高

            NET_ES_PARAM net_es_param = new NET_ES_PARAM();
            net_es_param.setiRightLevel(iRightLevel);
            net_es_param.setiEsType(iEsType);
            net_es_param.setSzContent(szContent);
            net_es_param.setiSoundType(iSoundType);
            net_es_param.setiLeftVolume(iLeftVolume);
            net_es_param.setiRightVolume(iRightVolume);
            if (bFullScreen == 1) {
                net_es_param.setbFullScreen(true);
            } else {
                net_es_param.setbFullScreen(false);
            }
            net_es_param.setuTimeLength(uTimeLength);

            //如果是全屏则显示全屏紧急消息，否则显示局部
            Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
            myIntent.putExtra("cmd", ConstantValue.CMD_TEXT_SETPROIRITY);
            myIntent.putExtra(ConstantValue.EXTRA_type, "old");
            myIntent.putExtra(ConstantValue.EXTRA_OBJ, net_es_param);
            context.sendBroadcast(myIntent);

            if (msgThread != null) {
                msgThread.interrupt();
                msgThread = null;
            }
            SendMsg sendContent = new SendMsg(getContent(context, net_cmd), pkt_in.getSocketAddress());
            msgThread = new Thread(sendContent);
            msgThread.start();
        } catch (Exception e) {

        }
    }

    public void setVolume(DataInputStream stream) {
        try {
            byte[] buf_temp = new byte[4];
            stream.read(buf_temp);
            int bTurnOn = byte2int(buf_temp);
            stream.read(buf_temp);
            int iLeftVolume = byte2int(buf_temp);
             stream.read(buf_temp);
            int iRightVolume = byte2int(buf_temp);
            stream.read(buf_temp);
            short iChannel = TypeRevert.bytesToShort(TypeRevert.shortToByte(stream.readShort()));

            Intent mIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
            mIntent.putExtra("cmd", ConstantValue.CMD_CONTROL_SETVOLUME);
            mIntent.putExtra(ConstantValue.EXTRA_type, "old");
            Bundle bundle = new Bundle();
            bundle.putInt("volume", iLeftVolume);
            mIntent.putExtra(ConstantValue.EXTRA_BUNDLE, bundle);
            context.sendBroadcast(mIntent);
        } catch (Exception e) {

        }
    }

    public void sendMsg(short msg) {
        Intent myIntent = new Intent(ConstantValue.ACTION_CMD_CONTROL);
        myIntent.putExtra("cmd", msg);
        myIntent.putExtra(ConstantValue.EXTRA_type, "old");
        context.sendBroadcast(myIntent);
    }

}