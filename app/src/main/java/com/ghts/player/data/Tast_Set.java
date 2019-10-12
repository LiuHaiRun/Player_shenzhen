package com.ghts.player.data;

import android.util.Log;
import android.widget.Toast;

import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by lijingjing on 17-8-28.
 */
public class Tast_Set implements Serializable {
//    char	szTaskID[33];
//    __int8	iTaskType;
//    __int8	iEmType;
//    __int8	iFullScreen;
//    __int8	iReplaceInfo;
//    __int8	iSoundType;
//    BYTE	buLeftVolume;
//    BYTE	buRightVolume;
//    char	szEmergencyInfo[1022];
//    DATE	dtTaskTime;
//    int 	iValidTime;				//有效时长，单位秒
//    __int16 iTimeLength;
//    char    szEditorID[17];
//    BYTE	buRightLevel;

    //    (1) szTaskID[33]：操作任务的ID编号，客户端软件需要自动生成，并确保全局唯一性；
//            (2) iTaskType：操作任务的类型，由枚举结构MACRO_TYPE_INDEX描述，但不支持其中的MT_POWER_ON命令；
//            (3) iEmType：操作任务为发布紧急信息时的方式（紧急文本、紧急版式），由枚举结构NET_ES_TYPE描述；
//            (4) iFullScreen：操作任务为发布紧急信息时的显示方式，1表示全屏显示，0表示区域显示；
//            (5) iReplaceInfo：操作任务为发布紧急信息时的播出方式，1表示覆盖，0表示跟随；
//            (6) iSoundType：操作任务为发布紧急信息时的声音处理方式（不处理、关闭声音、设置音量），由枚举结构NET_ES_SOUND_TYPE描述；
//            (7) buLeftVolume：操作任务为设置音量或者发布紧急信息（同时声音处理方式为调整音量）时的左声道音量，范围0～255；
//            (8) buRightVolume：操作任务为设置音量或者发布紧急信息（同时声音处理方式为调整音量）时的右声道音量，范围0～255；
//            (9) szEmergencyInfo[1022]：操作任务为发布紧急信息时的紧急文本或者紧急版式名称；
//            (10) dtTaskTime：操作任务的发布时间，CmdService接收后会以自身本机时间重新设定该成员；
//            (11) iValidTime：操作任务的有效时间，单位秒，从CmdService接收到后开始计算；该时间应该大于CmdService设定的查询间隔的2倍；例如如果CmdService设定的查询间隔为10秒，则iValidTime可设为22秒；
//            (12) iTimeLength：操作任务为发布紧急信息时，紧急状态的持续时间，单位秒，如果为0，表示紧急状态持续到停止命令；
//            (13) szEditorID[17]：操作任务发布者的ID；
//            (14) buRightLevel：操作任务发布者的权限级别，0～255，数字越大，级别越高；
//            (15) pMapDevice：CmdService内部使用。
    private static String szTaskID = "995774363139280524"; //szTaskID[33]：操作任务的ID编号，客户端软件需要自动生成，并确保全局唯一性；
    private static byte iTaskType, iEmType, iFullScreen, iReplaceInfo, iSoundType;
    private static byte buLeftVolume, buRightVolume, buRightLevel;
    private static String szEmergencyInfo;
    private static long dtTaskTime;
    private static int iValidTime; //有效时长，单位秒
    private static short iTimeLength;
    private static String szEditorID;
    static Tast_Set tast_set;

    public Tast_Set(String szTaskID, byte iTaskType, byte iEmType, byte iFullScreen, byte iReplaceInfo, byte iSoundType,
                    byte buLeftVolume, byte buRightVolume, byte buRightLevel, String szEmergencyInfo, long dtTaskTime, int iValidTime, short iTimeLength, String szEditorID) {
        this.szTaskID = szTaskID;
        this.iTaskType = iTaskType;
        this.iEmType = iEmType;
        this.iFullScreen = iFullScreen;
        this.iReplaceInfo = iReplaceInfo;
        this.iSoundType = iSoundType;
        this.buLeftVolume = buLeftVolume;
        this.buRightVolume = buRightVolume;
        this.buRightLevel = buRightLevel;
        this.szEmergencyInfo = szEmergencyInfo;
        this.dtTaskTime = dtTaskTime;
        this.iValidTime = iValidTime;
        this.iTimeLength = iTimeLength;
        this.szEditorID = szEditorID;

    }

    public static Tast_Set GetTastSetData(DataInputStream stream, byte[] buf_recv) {
        try {
            byte[] buf_temp = new byte[33];
            stream.read(buf_temp, 0, 33);
            szTaskID = new String(buf_temp, 0, 33, GData.charset).trim();
            iTaskType = stream.readByte();
            iEmType = stream.readByte();
            iFullScreen = stream.readByte();
            iReplaceInfo = stream.readByte();
            iSoundType = stream.readByte();
            buLeftVolume = stream.readByte();
            buRightVolume = stream.readByte();
            buf_temp = new byte[1022];
            stream.read(buf_temp, 0, 1022);
            szEmergencyInfo = new String(buf_temp, 0, 1022, GData.charset).trim();

            buf_temp = new byte[8];
            buf_temp = TypeRevert.longToByte(stream.readLong());
            dtTaskTime = TypeRevert.bytesToLong(buf_temp);

            int num = stream.readInt();
            buf_temp = TypeRevert.intToByte2(num);
            iValidTime = TypeRevert.bytesToInt(buf_temp);

            iTimeLength = stream.readShort();
            buf_temp = TypeRevert.shortToByte(iTimeLength);
            iTimeLength = TypeRevert.bytesToShort(buf_temp);

            buf_temp = new byte[17];
            stream.read(buf_temp, 0, 17);
            szEditorID = new String(buf_temp, 0, 17, GData.charset).trim();
            buRightLevel = stream.readByte();
            Log.e("-----", "Tast_Set{" +
                    "szTaskID='" + szTaskID + '\'' +
                    ", iTaskType=" + iTaskType +
                    ", iEmType=" + iEmType +
                    ", iFullScreen=" + iFullScreen +
                    ", iReplaceInfo=" + iReplaceInfo +
                    ", iSoundType=" + iSoundType +
                    ", buLeftVolume=" + buLeftVolume +
                    ", buRightVolume=" + buRightVolume +
                    ", buRightLevel=" + buRightLevel +
                    ", szEmergencyInfo='" + szEmergencyInfo + '\'' +
                    ", dtTaskTime=" + dtTaskTime +
                    ", iValidTime=" + iValidTime +
                    ", iTimeLength=" + iTimeLength +
                    ", szEditorID='" + szEditorID + '\'' +
                    '}');
            tast_set = new Tast_Set(szTaskID, iTaskType, iEmType, iFullScreen, iReplaceInfo, iSoundType,
                    buLeftVolume, buRightVolume, buRightLevel, szEmergencyInfo, dtTaskTime, iValidTime, iTimeLength, szEditorID);
            return tast_set;
        } catch (IOException e) {
            LogUtil.i("tag", "TextData.GetTextData() -- catch IOException: " + e.getLocalizedMessage());
            return null;
        }

    }

    public String getSzEditorID() {
        return szEditorID;
    }

    public String getSzTaskID() {
        return szTaskID;
    }

    public byte getiTaskType() {
        return iTaskType;
    }

    public byte getiEmType() {
        return iEmType;
    }

    public byte getiFullScreen() {
        return iFullScreen;
    }

    public byte getiReplaceInfo() {
        return iReplaceInfo;
    }

    public byte getiSoundType() {
        return iSoundType;
    }

    public byte getBuLeftVolume() {
        return buLeftVolume;
    }

    public byte getBuRightVolume() {
        return buRightVolume;
    }

    public byte getBuRightLevel() {
        return buRightLevel;
    }

    public String getSzEmergencyInfo() {
        return szEmergencyInfo;
    }

    public long getDtTaskTime() {
        return dtTaskTime;
    }

    public int getiValidTime() {
        return iValidTime;
    }

    public short getiTimeLength() {
        return iTimeLength;
    }

    public void setSzTaskID(String szTaskID) {
        this.szTaskID = szTaskID;
    }

    public void setiTaskType(byte iTaskType) {
        this.iTaskType = iTaskType;
    }

    public void setiEmType(byte iEmType) {
        this.iEmType = iEmType;
    }

    public void setiFullScreen(byte iFullScreen) {
        this.iFullScreen = iFullScreen;
    }

    public void setiReplaceInfo(byte iReplaceInfo) {
        this.iReplaceInfo = iReplaceInfo;
    }

    public void setiSoundType(byte iSoundType) {
        this.iSoundType = iSoundType;
    }

    public void setBuLeftVolume(byte buLeftVolume) {
        this.buLeftVolume = buLeftVolume;
    }

    public void setBuRightVolume(byte buRightVolume) {
        this.buRightVolume = buRightVolume;
    }

    public void setBuRightLevel(byte buRightLevel) {
        this.buRightLevel = buRightLevel;
    }

    public void setSzEmergencyInfo(String szEmergencyInfo) {
        this.szEmergencyInfo = szEmergencyInfo;
    }

    public void setDtTaskTime(long dtTaskTime) {
        this.dtTaskTime = dtTaskTime;
    }

    public void setiValidTime(int iValidTime) {
        this.iValidTime = iValidTime;
    }

    public void setiTimeLength(short iTimeLength) {
        this.iTimeLength = iTimeLength;
    }

    public void setSzEditorID(String szEditorID) {
        this.szEditorID = szEditorID;
    }

    public String toString() {
        return "Tast_Set{" +
                "szTaskID='" + szTaskID + '\'' +
                ", iTaskType=" + iTaskType +
                ", iEmType=" + iEmType +
                ", iFullScreen=" + iFullScreen +
                ", iReplaceInfo=" + iReplaceInfo +
                ", iSoundType=" + iSoundType +
                ", buLeftVolume=" + buLeftVolume +
                ", buRightVolume=" + buRightVolume +
                ", buRightLevel=" + buRightLevel +
                ", szEmergencyInfo='" + szEmergencyInfo + '\'' +
                ", dtTaskTime=" + dtTaskTime +
                ", iValidTime=" + iValidTime +
                ", iTimeLength=" + iTimeLength +
                ", szEditorID='" + szEditorID + '\'' +
                '}';
    }

}
