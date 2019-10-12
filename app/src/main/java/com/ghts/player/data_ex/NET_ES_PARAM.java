package com.ghts.player.data_ex;

import java.io.Serializable;

/**
 * @author ljj
 * @version 1.3
 * @updateDes $2018-07-19
 */
public class NET_ES_PARAM implements Serializable {
    private int iRightLevel;                    //权限级别码，数字越大级别越高
    private int iEsType;                        //紧急状态类型，由enum NET_ES_TYPE结构描述
    private String szContent; //1022             //表示需要设置的紧急状态内容或者紧急版式名称，以NULL结束。
    private byte iSoundType;                    //发布紧急信息时声音处理类型，0表示不处理，1表示关闭声音，2表示设置音量
    private byte iLeftVolume;                    //发布紧急信息时调整的左声道音量
    private byte iRightVolume;                    //发布紧急信息时调整的右声道音量
    private boolean bFullScreen;                    //如果是设置紧急内容，表示是否使用全屏显示。
    //若为true，则使用全屏实现紧急内容显示，否则只使用视频区域
    private int uTimeLength;                    //紧急状态持续时间，若为0，表示一直处于紧急状态，直到手动停止

    public NET_ES_PARAM(){

    }

    public int getiRightLevel() {
        return iRightLevel;
    }

    public int getiEsType() {
        return iEsType;
    }

    public String getSzContent() {
        return szContent;
    }

    public byte getiSoundType() {
        return iSoundType;
    }

    public byte getiLeftVolume() {
        return iLeftVolume;
    }

    public byte getiRightVolume() {
        return iRightVolume;
    }

    public boolean getbFullScreen() {
        return bFullScreen;
    }

    public int getuTimeLength() {
        return uTimeLength;
    }

    public void setiRightLevel(int iRightLevel) {
        this.iRightLevel = iRightLevel;
    }

    public void setiEsType(int iEsType) {
        this.iEsType = iEsType;
    }

    public void setSzContent(String szContent) {
        this.szContent = szContent;
    }

    public void setiSoundType(byte iSoundType) {
        this.iSoundType = iSoundType;
    }

    public void setiLeftVolume(byte iLeftVolume) {
        this.iLeftVolume = iLeftVolume;
    }

    public void setiRightVolume(byte iRightVolume) {
        this.iRightVolume = iRightVolume;
    }

    public void setbFullScreen(boolean bFullScreen) {
        this.bFullScreen = bFullScreen;
    }

    public void setuTimeLength(int uTimeLength) {
        this.uTimeLength = uTimeLength;
    }

    public NET_ES_PARAM(int iRightLevel, int iEsType, String szContent, byte iSoundType, byte iLeftVolume, byte iRightVolume, boolean bFullScreen, int uTimeLength) {

        this.iRightLevel = iRightLevel;
        this.iEsType = iEsType;
        this.szContent = szContent;
        this.iSoundType = iSoundType;
        this.iLeftVolume = iLeftVolume;
        this.iRightVolume = iRightVolume;
        this.bFullScreen = bFullScreen;
        this.uTimeLength = uTimeLength;
    }

    @Override
    public String toString() {
        return "NET_ES_PARAM{" +
                "iRightLevel=" + iRightLevel +
                ", iEsType=" + iEsType +
                ", szContent='" + szContent + '\'' +
                ", iSoundType=" + iSoundType +
                ", iLeftVolume=" + iLeftVolume +
                ", iRightVolume=" + iRightVolume +
                ", bFullScreen=" + bFullScreen +
                ", uTimeLength=" + uTimeLength +
                '}';
    }
}
