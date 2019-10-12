package com.ghts.player.data;

import android.util.Log;

import com.ghts.player.utils.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by lijingjing on 17-8-28.
 */
public class CmdDate implements Serializable {

    static String szHead;//头部标识，固定为"Cmd"
    static int iDataSize;//本结构以及后续所有数据的字节大小
    //内容类型，由CMD_TYPE描述,1表示命令的答复，0表示原始命令,命令服务器设定的监控查询间隔，单位秒
    static byte buType, buAck, buCheckInterval;
    static int iNum;//后续TASK_LIST或者DEVICE_STATUS结构的数量,操作任务的个数
    static String szTaskID;//针对CMD_QUERY_TASK_RESULT查询命令有效.
    static String szDeviceID;//针对CMD_QUERY_TASK查询命令有效
    static List<Tast_Set> tast_setList;
    static Tast_Set tast_set;


    public static Tast_Set GetTextData(byte[] buf_recv) {
        ByteArrayInputStream baInPacketData = new ByteArrayInputStream(buf_recv, 0, buf_recv.length);
        DataInputStream stream = new DataInputStream(baInPacketData);
        try {
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

            Log.e("--", "CmdDate{" +
                    "szHead='" + szHead + '\'' +
                    ", iDataSize=" + iDataSize +
                    ", buType=" + buType +
                    ", buAck=" + buAck +
                    ", buCheckInterval=" + buCheckInterval +
                    ", iNum=" + iNum +
                    ", szTaskID='" + szTaskID + '\'' +
                    ", szDeviceID='" + szDeviceID + '\'' + '}');
             if (iNum > 0) {
                tast_set = Tast_Set.GetTastSetData(stream, buf_recv);
            } else {
//              Toast.makeText(Const.activityInstance,"没有操作任务",Toast.LENGTH_LONG).show();
//                Log.e("----", "没有操作任务");
            }
            return tast_set;
        } catch (IOException e) {
            LogUtil.i("tag", "TextData.GetTextData() -- catch IOException: " + e.getLocalizedMessage());
            return null;
        }

    }

    public String getSzDeviceID() {
        return szDeviceID;
    }

    public int getiDataSize() {
        return iDataSize;
    }

    public byte getBuType() {
        return buType;
    }

    public byte getBuAck() {
        return buAck;
    }

    public byte getBuCheckInterval() {
        return buCheckInterval;
    }

    public int getiNum() {
        return iNum;
    }

    public String getSzTaskID() {
        return szTaskID;
    }

    public String getSzHead() {
        return szHead;
    }

    public void setSzHead(String szHead) {
        this.szHead = szHead;
    }

    public void setiDataSize(int iDataSize) {
        this.iDataSize = iDataSize;
    }

    public void setBuType(byte buType) {
        this.buType = buType;
    }

    public void setBuAck(byte buAck) {
        this.buAck = buAck;
    }

    public void setBuCheckInterval(byte buCheckInterval) {
        this.buCheckInterval = buCheckInterval;
    }

    public void setiNum(int iNum) {
        this.iNum = iNum;
    }

    public void setSzTaskID(String szTaskID) {
        this.szTaskID = szTaskID;
    }

    public void setSzDeviceID(String szDeviceID) {
        this.szDeviceID = szDeviceID;
    }

    public List<Tast_Set> getTast_setList() {
        return tast_setList;
    }

    public void setTast_setList(List<Tast_Set> tast_setList) {
        this.tast_setList = tast_setList;
    }

    public static Tast_Set getTast_set() {
        return tast_set;
    }

    public static void setTast_set(Tast_Set tast_set) {
        CmdDate.tast_set = tast_set;
    }

    @Override
    public String toString() {
        return "CmdDate{" +
                "szHead='" + szHead + '\'' +
                ", iDataSize=" + iDataSize +
                ", buType=" + buType +
                ", buAck=" + buAck +
                ", buCheckInterval=" + buCheckInterval +
                ", iNum=" + iNum +
                ", szTaskID='" + szTaskID + '\'' +
                ", szDeviceID='" + szDeviceID + '\'' +
                ", tast_setList=" + tast_setList +
                '}';
    }
}
