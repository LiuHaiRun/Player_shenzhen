package com.ghts.player.data_ex;

import com.ghts.player.data.TypeRevert;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author ljj
 * @version 1.3
 * @updateDes $2018-07-19
 */
public class NET_CMD implements Serializable{
//    UINT uPacketSN;						//包序列号，收到后直接返回
//    BYTE chCmd;							//命令类型，由NET_CMD_TYPE枚举结构描述
//    BYTE chOldCmd;						//回复命令时对应的原来的命令
//    BYTE chResult;						//命令执行结果
//    int  iRightLevel;					//发送命令时，为发送者和发送设备的权限值
//    union
//    {	BOOL bTurnOn;					//开关机命令参数，为0表示关机，非1表示开机
//        NETDATA_SOUND NetSound;			//声音控制参数
//        NET_ES_PARAM esParam;			//紧急状态控制参数结构
//        CHECK_STATUS_PARAM_EX csParam;	//状态检测参数结构    ----- 上传的消息内容
//        PLAY_CONTENT PlayContent;		//播出内容结构
//        DATA_OUTPUT DataOutput;			//请求播出画面的数据结构
//        PLAY_CTRL_PARAM PlayCtrlParam;	//播放控制参数
//    };
//    WORD wCRC;							//CRC-CCITT校验值
    private int uPacketSN,iRightLevel;
    private byte chCmd,chOldCmd,chResult;
    public NET_CMD(){

    }
    public NET_CMD(int uPacketSN, int iRightLevel, byte chCmd, byte chOldCmd, byte chResult) {
        this.uPacketSN = uPacketSN;
        this.iRightLevel = iRightLevel;
        this.chCmd = chCmd;
        this.chOldCmd = chOldCmd;
        this.chResult = chResult;
    }
    public NET_CMD GetNET_Cmd(DataInputStream stream, byte[] buf_recv) {
        try {
            byte[] buf_temp = new byte[4];
            int num = stream.readInt();
            buf_temp = TypeRevert.intToByte2(num);
            uPacketSN = TypeRevert.bytesToInt(buf_temp);

            int iRightLevel = stream.readInt();
            buf_temp = TypeRevert.intToByte2(num);
            iRightLevel = TypeRevert.bytesToInt(buf_temp);

//            addr_info = new ADDR_INFO(szIP,szMac,iRightLevel);
//            return addr_info;
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public int getuPacketSN() {
        return uPacketSN;
    }

    public int getiRightLevel() {
        return iRightLevel;
    }

    public byte getChCmd() {
        return chCmd;
    }

    public byte getChOldCmd() {
        return chOldCmd;
    }

    public byte getChResult() {
        return chResult;
    }

    public void setuPacketSN(int uPacketSN) {
        this.uPacketSN = uPacketSN;
    }

    public void setiRightLevel(int iRightLevel) {
        this.iRightLevel = iRightLevel;
    }

    public void setChCmd(byte chCmd) {
        this.chCmd = chCmd;
    }

    public void setChOldCmd(byte chOldCmd) {
        this.chOldCmd = chOldCmd;
    }

    public void setChResult(byte chResult) {
        this.chResult = chResult;
    }

    @Override
    public String toString() {
        return "NET_CMD{" +
                "uPacketSN=" + uPacketSN +
                ", iRightLevel=" + iRightLevel +
                ", chCmd=" + chCmd +
                ", chOldCmd=" + chOldCmd +
                ", chResult=" + chResult +
                '}';
    }
}
