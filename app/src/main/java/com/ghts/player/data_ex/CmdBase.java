package com.ghts.player.data_ex;

import android.content.Context;
import android.text.TextUtils;

import com.ghts.player.data.GData;
import com.ghts.player.data.TypeRevert;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.ConstantValue;
import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.PubUtil;
import com.ghts.player.utils.StorageUtil;
import com.ghts.player.utils.UserInfoSingleton;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static com.ghts.player.data.GData.CMD_RESULT_OK;

/**
 * @author ljj
 * @version 1.3
 * @updateDes $2018-07-19
 */
public class CmdBase {

    /**
     * 网络命令类型枚举NET_CMD_TYPE
     */
    public static final int NET_CMD_ACK = 0;                 //回复命令时才使用
    public static final int NET_CMD_SWITCH_DEVICE = 1;                //设备开关机命令
    public static final int NET_CMD_SWITCH_DEVICE_SCREEN = 2;        //设备显示屏开关机命令
    public static final int NET_CMD_GET_CONTENT = 3;              //得到当前设置的内容
    public static final int NET_CMD_SET_CONTENT = 4;              //显示内容设置命令
    public static final int NET_CMD_END_CONTENT = 5;                //结束内容设置命令
    public static final int NET_CMD_CHECK_STATUS = 6;               //状态检测命令
    public static final int NET_CMD_START_RUNNING = 7;               //开始运行命令
    public static final int NET_CMD_STOP_RUNNING = 8;           //停止运行命令
    public static final int NET_CMD_TAKE = 9;            //TAKE命令
    public static final int NET_CMD_RESET_DEVICE = 10;                //重新启动命令
    public static final int NET_CMD_GET_PLAY_CONTENT = 11;           //获取模块的播出内容
    public static final int NET_CMD_SWITCH_SOUND = 12;                //声音开关命令
    public static final int NET_CMD_SET_VOLUME = 13;             //设置声音大小
    public static final int NET_CMD_GET_OUTPUT = 14;                   //获取播出画面
    public static final int NET_CMD_START_LIVE = 15;               //开始视频直播，通常从录播转为直播
    public static final int NET_CMD_STOP_LIVE = 16;              //结束视频直播，通常从直播转回录播
    public static final int NET_CMD_WAKEUP_DEVICE = 100;        //唤醒其他设备，要唤醒的设备IP及MAC通过CHECK_STATUS_PARAM的szStatus合szError传递进来
    /**
     * DLL_LOAD_STATUS
     */
    public static int DL_STATUS_OK = 0;
    public static int DL_STATUS_SET_NONE = 1;
    public static int DL_STATUS_LOAD_FAIL = 2;
    public static int DL_STATUS_INIT_FAIL = 3;

    //屏幕状态
    byte SCREEN_STATUS_OFF = 0;            //关闭状态
    byte SCREEN_STATUS_ON = 1;                    //开启状态
    byte SCREEN_STATUS_ERROR = 2;                //故障状态
    byte SCREEN_STATUS_UNKNOWN = 3;                //未知状态
    byte MAX_SCREEN_STATUS = 4;
    int i = 11;
    char v = (char) i;

    /**
     * CHECK_STATUS_PARAM_EX csParam;	//状态检测参数结构    ----- 上传的消息内容
     **/
    protected byte[] makelocalPacket(Context context, NET_CMD net_cmd) {
        try {
            ByteArrayOutputStream baOut = new ByteArrayOutputStream(1471);
            DataOutputStream dout = new DataOutputStream(baOut);
            WriteHead(dout, net_cmd);
            int volume = 0;
            String yin;
            if (UserInfoSingleton.getBoolean(Const.GlobalAudioStatus)) {
                volume = 0;
                yin = "关闭";
            } else {
                yin = "开启";
                volume = PubUtil.parseInt(Const.getAudioVolume());
            }
            //String szStatus="{\n" +"\"updated_time\": \"20160513 103834\",\n" +"\"linecode\": \"L001\",\n" + "\"deviceList\": [\n" + "{\n" + "\"code\": \"S0101-01\",\n" + "\"status\": {\n" + "\"HD\": {\n" + "\"property\": true,\n" + "\"status\": \"0\",\n" + "\"sumSpace\": \"1799MB\",\n" + "\"freeSpace\": \""+StorageUtil.getAvailMemory(context)+"MB\"\n" + "},\n" + "\"screen\": {\n" + "\"property\": false,\n" + "\"status\": [\n" + "1,\n" + "0\n" + "],\n" + "\"num\": \"1\"\n" + "},\n" + "\"emer\": {\n" + "\"emergent\": "+!TextUtils.isEmpty(UserInfoSingleton.getEmergency())+",\n" + "\"rightLevel\": \"0\",\n" + "\"esType\": \""+UserInfoSingleton.getIsEmFull()+"\",\n" + "\"content\": \"test content\",\n" + "\"fullScreen\": \"1\",\n" + "\"timeLength\": \"1\"\n" + "},\n" + "\"status\": \"设备运行正常\"\n" + "}\n" + "}\n"+"\n" + "  ]\n" + "}";
            String szStatus = "操作系统:" + "\t" + "Linux" + v
                    + "CPU信息:" + "\t" + StorageUtil.readUsage() + "%" + v
                    + "分辨率:" + "\t" + Const.screenW + " X " + Const.screenH + v
                    + "总内存:" + "\t" + "1799MB" + v
                    + "可用内存:" + "\t" + StorageUtil.getAvailMemory(context) + "MB" + v
                    + "主版本号:" + "\t" + "2.4.2" + v
                    + "------:" + "\t" + "-----" + v
                    + "播表名称:" + "\t" + Const.currentPlay + v
                    + "版式名称:" + "\t" + Const.currentLayout + v
                    + "声音控制:" + "\t" + "声音:" + yin + "    " + "音量:" + volume + v;
            WriteFixBytes(dout, szStatus.getBytes(GData.charset), 924);
            String szError = "";
            WriteFixBytes(dout, szError.getBytes(), 192);
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
//            LogUtil.e("空间数据", StorageUtil.getDataTotalSize() + "****" + StorageUtil.getDataFreeSize());

            //sata总的内存空间
            byte[] temp = TypeRevert.longToByte2(StorageUtil.getDataTotalSize());
            dout.write(temp);

            //sata剩余空间
            byte[] uHDFreeSpace = longToBytes(StorageUtil.getDataFreeSize());
            dout.write(uHDFreeSpace);

            dout.writeBoolean(false);
            String szPDPName = "屏幕";
            WriteFixBytes(dout, szPDPName.getBytes(GData.charset), 32);
            dout.writeByte(DL_STATUS_OK);
            /////////////////////////////////
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
            //            byte aa[] = CrcUtil.setParamCRC(baOut.toByteArray());
            //            short em = 0;//WORD 校验值
            //              for (int j = 0; j < aa.length; j++)
            //            {
            //                String hex = Integer.toHexString(aa[j] & 0xFF);
            //                if (hex.length() == 1)
            //                {
            //                    hex = '0' + hex;
            //                }
            //                LogUtil.e("---shuju----",hex.toUpperCase() + " ");
            //            }
            //
            //            if(CrcUtil.isPassCRC(baOut.toByteArray(), 2)){
            //                System.out.println("验证通过");
            //            }else{
            //                System.out.println("验证失败");
            //            }
            dout.writeByte(0);
            dout.writeByte(0);
            return baOut.toByteArray();
        } catch (IOException e) {
            LogUtil.i("----", "RecvBase.makeHeartbeatPacket() -- catch IOException: " + e.getLocalizedMessage());
            return null;
        }
    }

    private static ByteBuffer buffer = ByteBuffer.allocate(8);

    //byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    protected byte[] getContent(Context context, NET_CMD net_cmd) {
        try {
            ByteArrayOutputStream baOut = new ByteArrayOutputStream(1471);
            DataOutputStream dout = new DataOutputStream(baOut);

            WriteHead(dout, net_cmd);

            byte[] temp = TypeRevert.intToByte(net_cmd.getiRightLevel());
            dout.write(temp);

            temp = TypeRevert.intToByte(0);
            dout.write(temp);

            String emStr = UserInfoSingleton.getEmergency();
            String qp = "";
            if (UserInfoSingleton.getIsEmFull()) {
                qp = "全屏";
            } else {
                qp = "局部";
            }
            String szContent = "";
            if (!TextUtils.isEmpty(emStr)) {
                szContent = qp + "紧急消息:" + "\t" + emStr + v;
            }
            WriteFixBytes(dout, szContent.getBytes(GData.charset), 1022);

            dout.writeBoolean(true);
            dout.writeByte(PubUtil.parseInt(Const.AudioVolume));
            dout.writeByte(PubUtil.parseInt(Const.AudioVolume));

            temp = TypeRevert.intToByte(0);
            dout.write(temp);
            temp = TypeRevert.intToByte(0);
            dout.write(temp);

            byte[] aa = {0, 0, 0, 0};
            WriteFixBytes(dout, aa, 417);
            dout.writeByte(0);
            dout.writeByte(0);
            LogUtil.e("---长度Content----" + baOut.toByteArray().length, baOut.toByteArray().length + "");
            return baOut.toByteArray();
        } catch (IOException e) {
            LogUtil.i("----", "RecvBase.makeHeartbeatPacket() -- catch IOException: " + e.getLocalizedMessage());
            return null;
        }
    }

    protected byte[] getPlayContent(Context context, NET_CMD net_cmd) {
        ByteArrayOutputStream baOut = null;
        try {
            baOut = new ByteArrayOutputStream(1471);
            DataOutputStream dout = new DataOutputStream(baOut);
            WriteHead(dout, net_cmd);
            byte temp[] = TypeRevert.intToByte(5);
            dout.write(temp); //int  iModuleNum;		//模块个数
            String flag = UserInfoSingleton.getIslive();
            if (flag.equals(ConstantValue.PLAY_LIVE)) {
                flag = "直播";
            } else {
                flag = "本地";
            }
            boolean isEmer = false;
            if (UserInfoSingleton.getIsExigent().equals(ConstantValue.EMERGSTATUS_YES)) {
                isEmer = true;
            } else {
                isEmer = false;
            }
            String a = "";
            HashMap<String, String> moduleMap = Const.moduleMap;
            StringBuffer buffer = new StringBuffer("");
            if (moduleMap != null && moduleMap.size() > 0) {
                for (String key : moduleMap.keySet()) {
                    buffer.append(key + "\t" + moduleMap.get(key) + v);
                }
                a = buffer.toString() + "视频" + "\t" + flag + v + "紧急状态" + "\t" + isEmer + v;
            } else {
                a = "视频" + "\t" + flag + v + "紧急状态" + "\t" + isEmer + v;
            }
            WriteFixBytes(dout, a.getBytes(GData.charset), 1280);
            byte[] aa = {0, 0, 0, 0};
            WriteFixBytes(dout, aa, 176);
            return baOut.toByteArray();
        } catch (Exception e) {
            //            return null;
        }
        return baOut.toByteArray();
    }

    protected byte[] getOutput(Context context, NET_CMD net_cmd) {
        try {
            ByteArrayOutputStream baOut = new ByteArrayOutputStream(36);
            DataOutputStream dout = new DataOutputStream(baOut);
            WriteHead(dout, net_cmd);
            String flag = UserInfoSingleton.getIslive();
            if (flag.equals(ConstantValue.PLAY_LIVE)) {
                flag = "直播";
            } else {
                flag = "本地";
            }
            String a = "当前播表:" + "\t" + Const.currentPlay + v + "视频" + "\t" + flag + v;
            WriteFixBytes(dout, a.getBytes(GData.charset), 36);

            byte temp[] = TypeRevert.intToByte(1);
            dout.write(temp); //int  iModuleNum;
            byte[] aa = {0, 0, 0, 0};
            WriteFixBytes(dout, aa, 1420);
            LogUtil.e("---getOutput----" + baOut.toByteArray().length, baOut.toByteArray().length + "");
            return baOut.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 发送头部命令数据
     *
     * @param dout
     * @param net_cmd
     */
    private void WriteHead(DataOutputStream dout, NET_CMD net_cmd) {
        try {
            byte[] temp = TypeRevert.intToByte(net_cmd.getuPacketSN());
            dout.write(temp);
             dout.writeByte(NET_CMD_ACK);
            dout.writeByte(net_cmd.getChCmd());
            dout.writeByte(CMD_RESULT_OK);
            temp = TypeRevert.intToByte(net_cmd.getiRightLevel());
            dout.write(temp);
        } catch (IOException e) {
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
            LogUtil.i("--", "RecvBase.WriteFixBytes() -- catch IOException: " + e.getLocalizedMessage());
        }
    }
}
