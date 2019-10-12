package com.ghts.player.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ghts.player.bean.GlobalBean;
import com.ghts.player.data_ex.CmdQueryTaskHz;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.StorageUtil;
import com.ghts.player.utils.UserInfoSingleton;
import com.ghts.player.utils.VeDate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.ghts.player.data_ex.CmdBase.longToBytes;

public class CmdServiceBase {
    protected static final String TAG = "CmdServiceBase";
    protected Context mContext = null;
    public static boolean isStop = false;
    //状态返回
    int CMD_RESULT_LOCAL_ERROR = -2;        //本地错误，例如参数非法
    int CMD_RESULT_CONN_ERROR = -1;                //网络故障
    int CMD_RESULT_ERROR = 0;            //执行失败，对于状态检测，故障原因由szError描述
    int CMD_RESULT_OK = 1;                        //执行成功
    //错误命令序号
    int CMD_RESULT_NO_CMD = 2;                    //没有指定的命令
    int CMD_RESULT_NO_LOGON = 3;                //没有登录，不使用
    //登录错误序号
    int CMD_RESULT_LOGON_FAIL = 4;                //错误的用户名或密码，不使用
    int CMD_RESULT_NO_RIGHT = 5;            //没有权限，不使用
    //紧急状态或者播表、版式控制错误序号
    int CMD_RESULT_LOW_RIGHT = 6;            //权限低，不能进行紧急状态的控制，或者是当前处于紧急状态，实施其他控制但权限较低
    int CMD_RESULT_LAYOUT_NOT_EXIST = 7;        //版式不存在
    int CMD_RESULT_LIST_NOT_EXIST = 8;            //播表不存在，不使用
    int CMD_RESULT_NOT_EMERGENT = 9;    //取消设置失败，因为没有处于紧急状态
    //TAKE命令错误序号
    int CMD_RESULT_NO_PST = 10;                //当前没有PST节目，不能执行TAKE命令
    //开始运行和停止运行错误序号
    int CMD_RESULT_NO_LIST = 11;                    //当前没有播表节目，不能执行命令
    int CMD_RESULT_NO_LAYOUT = 12;            //当前时间段没有版式，不能执行命令
    int CMD_ERSULT_INVALID_INDEX = 13;            //SKIP命令的版式组或者版式节目序号非法，不使用
    //没有板卡
    int CMD_RESULT_NO_BOARD = 14;                //不使用
    //直播命令的错误序号
    int CMD_RESULT_NO_LIVE_MODULE = 15;            //没有直播模块
    //指令
    int CMD_PUBLISH_TASK = 0;           //网管发布操作任务
    int CMD_QUERY_TASK_RESULT = 1;            //网管查询操作任务执行结果
    int CMD_QUERY_STATUS = 2;                //网管查询设备状态
    int CMD_QUERY_SINGLE_STATUS = 3;       //网管查询单个设备状态
    int CMD_QUERY_TASK = 4;                    //设备查询自己的操作任务
    int CMD_FEEDBACK_TASK_RESULT = 5;        //设备反馈操作任务的执行结果
    int CMD_PUBLISH_STATUS = 6;                //设备发布自己的状态
    //屏幕控制DLL的加载状态
    int DL_STATUS_OK = 0;          //屏幕控制DLL加载正常
    int DL_STATUS_SET_NONE = 1;            //未设置屏幕控制DLL
    int DL_STATUS_LOAD_FAIL = 2;        //屏幕控制DLL加载失败
    int DL_STATUS_INIT_FAIL = 3;     //屏幕控制DLL初始化失败
    int MAX_DL_STATUS = 4;
    //屏幕状态
    byte SCREEN_STATUS_OFF = 0;            //关闭状态
    byte SCREEN_STATUS_ON = 1;                    //开启状态
    byte SCREEN_STATUS_ERROR = 2;                //故障状态
    byte SCREEN_STATUS_UNKNOWN = 3;                //未知状态
    byte MAX_SCREEN_STATUS = 4;
    //NET_ES_TYPE
    int NET_ES_CONTENT = 0;            //采用紧急内容，通过紧急模块实现，szContent维护紧急内容
    int NET_ES_LAYOUT = 1;            //采用紧急版式，szContent维护版式文件名
    int MAX_NET_ES_TYPE = 2;
    // NET_ES_SOUND_TYPE
    int NET_ES_SOUND_NONE = 0;        //不处理
    int NET_ES_SOUND_OFF = 1;        //关闭声音
    int NET_ES_SOUND_SET_VOLUME = 2;    //设置音量
    int MAX_NET_ES_SOUND_TYPE = 3;
    //上传状态信息长度
    protected static final int sendMsgPacketLength = 3856;
    protected static final int szTaskIDLength = 33;
    //ATS显示模式
    byte SM_CHINESE = 0;
    byte SM_ENGLISH = 1;
    byte SM_BOTH = 2;


    //上传本地状态信息
    protected byte[] makelocalPacket(Context context) {
        try {
            ByteArrayOutputStream baOut = new ByteArrayOutputStream(sendMsgPacketLength);
            DataOutputStream dout = new DataOutputStream(baOut);

            if (UserInfoSingleton.getIsPowerOff().equals(ConstantValue.IsPowerOff_NO)) {
                WriteHead(dout);
                String id = "S0203-02";
                id = GlobalBean.getGlobalBean().getDeviceCode();
                WriteFixBytes(dout, id.getBytes(), 9);

                int volume = 0;
                String yin;
                if(UserInfoSingleton.getBoolean(Const.GlobalAudioStatus)){
                    yin="关闭";
                    volume = PubUtil.parseInt(Const.getAudioVolume());
                }else{
                    yin="开启";
                    volume = PubUtil.parseInt(Const.getAudioVolume());
                }

                dout.writeByte(CMD_RESULT_OK);
                dout.writeByte(0);
                dout.writeShort(0);
                long date = System.currentTimeMillis() / 1000;
                byte[] temp = TypeRevert.longToByte(date);
                dout.write(temp);
                int i = 11;
                char v = (char) i;
                String a = "操作系统:" + "\t" + "Linux" + v
                        + "CPU信息:" + "\t" + StorageUtil.readUsage() + "%" + v
                        + "分辨率:" + "\t" + Const.screenW + " X " + Const.screenH + v
                        + "总内存:" + "\t" + "1799MB" + v
                        + "可用内存:" + "\t" + StorageUtil.getAvailMemory(context) + "MB" + v
                        + "主版本号:" + "\t" + "2.4.2" + v
                        + "-------" + "\t" + "-----" + v
                        + "播表名称:" + "\t" + Const.currentPlay + v
                        + "版式名称:" + "\t" + Const.currentLayout + v
                        +"声音控制:"+"\t"+"声音:"+yin+"    "+"音量:"+ volume+v;
                WriteFixBytes(dout, a.getBytes(GData.charset), 924);
                String b = "";
                WriteFixBytes(dout, b.getBytes(GData.charset), 196);
                boolean isEmer = false;
                if (UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES)) {
                    isEmer = true;
                } else {
                    isEmer = false;
                }
                dout.writeBoolean(isEmer);
                dout.writeBoolean(true);
                dout.writeByte(CMD_RESULT_OK);
                String c = "总空间" + "\t" + StorageUtil.getDataTotalSize() + "MB" + v + "硬盘剩余空间" + "\t" + StorageUtil.getDataFreeSize() + "MB" + v;
                WriteFixBytes(dout, c.getBytes(GData.charset), 256);
                //sata总的内存空间
                byte[] uHDSumSpace = longToBytes(StorageUtil.getDataTotalSize());
                dout.write(uHDSumSpace);
                //sata剩余空间
                byte[] uHDFreeSpace = longToBytes(StorageUtil.getDataFreeSize());
                dout.write(uHDFreeSpace);

                dout.writeBoolean(true);
                String szPDPName = "屏幕";
                WriteFixBytes(dout, szPDPName.getBytes(GData.charset), 32);
                dout.writeByte(DL_STATUS_OK);
                dout.writeByte(Const.globalBean.getPctrlCount());
                byte sc[] = new byte[Const.globalBean.getPctrlCount()];
                if (UserInfoSingleton.getIsScreenOff().equals(ConstantValue.IsScreenOff_NO)) {
                    for (int j = 0; j < Const.globalBean.getPctrlCount(); j++) {
                        sc[j] = SCREEN_STATUS_ON;
                    }
                } else if (UserInfoSingleton.getIsScreenOff().equals(ConstantValue.IsScreenOff_YES)) {
                    for (int k = 0; k < Const.globalBean.getPctrlCount(); k++) {
                        sc[k] = SCREEN_STATUS_OFF;
                    }
                } else {
                    for (int m = 0; m < Const.globalBean.getPctrlCount(); m++) {
                        sc[m] = SCREEN_STATUS_UNKNOWN;
                    }
                }
                WriteFixBytes(dout, sc, 32);

                temp = TypeRevert.intToByte(1);
                dout.write(temp);
                temp = TypeRevert.intToByte(NET_ES_CONTENT);
                dout.write(temp);
                String emStr = UserInfoSingleton.getEmergency();
                String bFullScreen = "false";
                String qp = "";
                if (UserInfoSingleton.getIsEmFull()) {
                    bFullScreen = "true";
                    qp = "全屏";
                } else {
                    bFullScreen = "false";
                    qp = "局部";
                }
                String szContent = "";
                if (!TextUtils.isEmpty(emStr)) {
                    szContent = qp + "紧急消息:" + "\t" + emStr + v;
                }
                WriteFixBytes(dout, szContent.getBytes(GData.charset), 1022);
                dout.writeByte(2);
                dout.writeByte(PubUtil.parseInt(Const.AudioVolume));
                dout.writeByte(PubUtil.parseInt(Const.AudioVolume));
                WriteFixBytes(dout, bFullScreen.getBytes(GData.charset), 4);
                short em = 0;//WORD wTimeLength;		//紧急状态持续时间，若为0，表示一直处于紧急状态，直到手动停止
                dout.write(TypeRevert.shortToByte(em));
                em = 1;
                dout.write(TypeRevert.shortToByte(em)); //紧急信息的播出模式，1表示替换之前的，0表示追加到最后
                //播出内容结构  PLAY_CONTENT PlayContent;
                temp = TypeRevert.intToByte(2);
                dout.write(temp); //int  iModuleNum;		//模块个数
                String flag = UserInfoSingleton.getIslive();
                if (flag.equals(ConstantValue.PLAY_LIVE)) {
                    flag = "直播";
                } else {
                    flag = "本地";
                }
                HashMap<String, String> moduleMap = Const.moduleMap;
                StringBuffer buffer = new StringBuffer("");
                if (moduleMap != null && moduleMap.size() > 0 ) {
                    for (String key : moduleMap.keySet()) {
                        buffer.append(key + "\t" + moduleMap.get(key) + v);
                    }
                    a = buffer.toString()+"视频" + "\t" + flag + v + "紧急状态" + "\t" + isEmer + v;
                }else{
                    a = "视频" + "\t" + flag + v  + "紧急状态" + "\t" + isEmer + v;
                }
                WriteFixBytes(dout, a.getBytes(GData.charset), 1280);
            } else if (UserInfoSingleton.getIsPowerOff().equals(ConstantValue.IsPowerOff_YES)) {
                WriteHead(dout);
                String id = "S0203-02";
                id = GlobalBean.getGlobalBean().getDeviceCode();
                WriteFixBytes(dout, id.getBytes(), 9);
                dout.writeByte(CMD_RESULT_OK);
                dout.writeByte(0);
                dout.writeShort(0);
                long date = System.currentTimeMillis() / 1000;
                byte[] temp = TypeRevert.longToByte(date);
                dout.write(temp);
                int i = 11;
                char v = (char) i;
                String a = "此设备已关机" + "\t" + " " + v;
                WriteFixBytes(dout, a.getBytes(GData.charset), 924);
                String b = "";
                WriteFixBytes(dout, b.getBytes(GData.charset), 196);
                dout.writeBoolean(false);
                dout.writeBoolean(false);
                dout.writeByte(CMD_RESULT_OK);
                String c = "";
                WriteFixBytes(dout, c.getBytes(GData.charset), 256);
                //sata总的内存空间
                byte[] uHDSumSpace = TypeRevert.longToByte(StorageUtil.getDataTotalSize());
                dout.write(uHDSumSpace);
                //sata剩余空间
                byte[] uHDFreeSpace = TypeRevert.longToByte(StorageUtil.getDataFreeSize());
                dout.write(0);
                dout.writeBoolean(false);

                String szPDPName = "屏幕";
                WriteFixBytes(dout, szPDPName.getBytes(GData.charset), 32);
                dout.writeByte(DL_STATUS_OK);
                dout.writeByte(1);
                byte sc[] = new byte[1];
                if (UserInfoSingleton.getIsScreenOff().equals(ConstantValue.IsScreenOff_NO)) {
                    for (; i == 0; i++) {
                        sc[i] = SCREEN_STATUS_ON;
                    }
                } else if (UserInfoSingleton.getIsScreenOff().equals(ConstantValue.IsScreenOff_YES)) {
                    for (; i == 0; i++) {
                        sc[i] = SCREEN_STATUS_OFF;
                    }
                } else {
                    for (; i == 0; i++) {
                        sc[i] = SCREEN_STATUS_UNKNOWN;
                    }
                }
                WriteFixBytes(dout, sc, 32);
                temp = TypeRevert.intToByte(1);
                dout.write(temp);
                temp = TypeRevert.intToByte(NET_ES_CONTENT);
                dout.write(temp);
                String emStr = UserInfoSingleton.getEmergency();
                String bFullScreen = "false";
                String szContent = "";
                WriteFixBytes(dout, szContent.getBytes(GData.charset), 1022);
                dout.writeByte(2);
                dout.writeByte(PubUtil.parseInt(Const.AudioVolume));
                dout.writeByte(PubUtil.parseInt(Const.AudioVolume));
                WriteFixBytes(dout, bFullScreen.getBytes(GData.charset), 4);
                short em = 0;//WORD wTimeLength;		//紧急状态持续时间，若为0，表示一直处于紧急状态，直到手动停止
                dout.write(TypeRevert.shortToByte(em));
                em = 1;
                dout.write(TypeRevert.shortToByte(em)); //紧急信息的播出模式，1表示替换之前的，0表示追加到最后
                //播出内容结构  PLAY_CONTENT PlayContent;
                temp = TypeRevert.intToByte(1);
                dout.write(temp); //int  iModuleNum;		//模块个数
                WriteFixBytes(dout, c.getBytes(GData.charset), 1280);
            }
            return baOut.toByteArray();
        } catch (IOException e) {
            LogUtil.i("----", "RecvBase.makeHeartbeatPacket() -- catch IOException: " + e.getLocalizedMessage());
            return null;
        }
    }

    private void WriteHead(DataOutputStream dout) {
        try {
            String a = "Cmd";
            WriteFixBytes(dout, a.getBytes(), 4);
            byte[] temp = TypeRevert.intToByte(3856);
            dout.write(temp);
            dout.writeByte(CMD_PUBLISH_STATUS);
            dout.writeByte(0);
            dout.writeByte(Const.getBuCheckInterval());
            temp = TypeRevert.intToByte(1);
            dout.write(temp);
            String id = "S0203-02";
            id = GlobalBean.getGlobalBean().getDeviceCode();
            WriteFixBytes(dout, id.getBytes(), szTaskIDLength);
        } catch (IOException e) {
            Log.i("--", "write_head...catch IOException: " + e.getLocalizedMessage());
        }
    }

    //解析头部数据
    public void parseHead(byte acceptdata[]) {
        ByteArrayInputStream baInPacketHeader = new ByteArrayInputStream(acceptdata, 0, acceptdata.length);
        DataInputStream stream = new DataInputStream(baInPacketHeader);
        try {
            byte[] buf_temp = new byte[4];
            stream.read(buf_temp, 0, 4);
            String szHead = new String(buf_temp, 0, 4, GData.charset).trim();
            int num = stream.readInt();
            buf_temp = TypeRevert.intToByte2(num);
            int iDataSize = TypeRevert.bytesToInt(buf_temp);
            byte buType = stream.readByte();
            byte buAck = stream.readByte();
            byte buCheckInterval = stream.readByte();
             Const.setBuCheckInterval(buCheckInterval);
            buf_temp = TypeRevert.intToByte2(stream.readInt());
            int iNum = TypeRevert.bytesToInt(buf_temp);
            buf_temp = new byte[33];
            stream.read(buf_temp, 0, 33);
            String szTaskID = new String(buf_temp, 0, 33, GData.charset).trim();
            //            LogUtil.e("--头部数据--", szHead + "-" + iDataSize + "-" + buType + "-" + buAck + "-" + buCheckInterval + "-" + iNum + "-" + szTaskID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查询本机的操作命令
    protected byte[] makeQueryTask() {
        try {
            ByteArrayOutputStream baOut = new ByteArrayOutputStream(48);
            DataOutputStream dout = new DataOutputStream(baOut);
            try {
                String a = "Cmd";
                WriteFixBytes(dout, a.getBytes(), 4);
                byte[] temp = TypeRevert.intToByte(48);
                dout.write(temp);
                dout.writeByte(CMD_QUERY_TASK);
                dout.writeByte(0);
                dout.writeByte(Const.buCheckInterval);
                temp = TypeRevert.intToByte(1);
                dout.write(temp);
                String id = "S0203-02";
                id = GlobalBean.getGlobalBean().getDeviceCode();
                WriteFixBytes(dout, id.getBytes(), szTaskIDLength);
            } catch (IOException e) {
                Log.i("--", "write_head...catch IOException: " + e.getLocalizedMessage());
            }
            return baOut.toByteArray();
        } catch (Exception e) {
            LogUtil.i("----", "RecvBase.makeHeartbeatPacket() -- catch IOException: " + e.getLocalizedMessage());
            return null;
        }
    }

    //向CmdService反馈操作任务的执行结果。
    protected byte[] makeTaskResult(String szTaskID) {
        try {
            ByteArrayOutputStream baOut = new ByteArrayOutputStream(100);
            DataOutputStream dout = new DataOutputStream(baOut);
            try {
                String a = "Cmd";
                WriteFixBytes(dout, a.getBytes(), 4);
                byte[] temp = TypeRevert.intToByte(100);
                dout.write(temp);
                dout.writeByte(CMD_FEEDBACK_TASK_RESULT);
                dout.writeByte(0);
                dout.writeByte(Const.buCheckInterval);
                temp = TypeRevert.intToByte(1);
                dout.write(temp);
                String id = GlobalBean.getGlobalBean().getDeviceCode();
                WriteFixBytes(dout, id.getBytes(), szTaskIDLength);

                WriteFixBytes(dout, id.getBytes(), 9);
                short iExecResult = GData.CMD_RESULT_OK;
                temp = TypeRevert.shortToByte(iExecResult);
                dout.write(temp);

                Date d = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateNowStr = sdf.format(d);
                Double doub = VeDate.changetoDouble(dateNowStr);
                temp = TypeRevert.doubleToBytes(doub);
                dout.write(temp);
                LogUtil.e("--szTaskID--", szTaskID + "&&&&&" + d);
                WriteFixBytes(dout, szTaskID.getBytes(), 33);
            } catch (IOException e) {
                Log.i("--", "write_head...catch IOException: " + e.getLocalizedMessage());
            }
            return baOut.toByteArray();
        } catch (Exception e) {
            LogUtil.i("----", "RecvBase.makeHeartbeatPacket() -- catch IOException: " + e.getLocalizedMessage());
            return null;
        }
    }

    protected void WriteFixBytes(DataOutputStream dout, byte[] bWrite, int length) {
        if (length <= 0) {
            return;
        }
        byte[] bNull = new byte[length];
        try {
            if (bWrite.length <= length) {
                dout.write(bWrite, 0, bWrite.length);
                dout.write(bNull, 0, length - bWrite.length);
            } else {
                dout.write(bWrite, 0, length);
            }
        } catch (IOException e) {
            Log.i("--", "RecvBase.WriteFixBytes() -- catch IOException: " + e.getLocalizedMessage());
        }
    }
}