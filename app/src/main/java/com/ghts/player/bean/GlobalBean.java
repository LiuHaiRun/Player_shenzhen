package com.ghts.player.bean;

import android.text.format.Time;

import com.ghts.player.parse.ParseXml;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by lijingjing on 17-7-26.
 * 读取Global配置文件
 */
public class GlobalBean {
    private static GlobalBean globalBean = null;
    private static final String TAG = "GlobalBean";
    private static ParseXml parseXml;

//  <Station Code="T0101" Name="测试车" EnName="ceshi" Map="12" IsTrain="1" StartTime="00:00:00" StopTime="00:00:00" />
    //判断是车站还是车载 0：车站 1:车载

    private String IsTrain,stationCode;
    //设备唯一标识
//      <Device Code="S0001-01" Name="视频扩展单元2" Type="5" Pos="下行" IP="10.1.91.31" Mac="C8-32-55-10-02-83" HDThreshold="5"/>
    private String DeviceCode;
    private String DevicePos,DeviceType ;
    //    <ATS MCIP="10.3.1.2" MCPort="7655" AtsID="8" />  //接收 ats
    private String MCIP, MCPort, AtsID,Platform;
    //车载用LiveClient
    //<LiveClient LogEx="1" Standalone="0" TalkPort="7705" ForwardHead="0" ForwardMode="2" 0:单播 1:组播  2:第三方转发  FollowList="0" AccelScale="66" ThirdIP="192.168.80.1" />
    private String ForwardMode; //0:单播 1:组播  2:第三方转发
    //    <LiveList Count="1" >
//    <Channel1 Name="车载直播通道1" SrcIP="235.1.1.10" SrcPort="5022" 车站
// PacketSize="1316" LiveIP="224.3.1.51" LivePort="7661" TrainIP="224.3.1.51"  TrainPort="7662" 单播端口/>
//    </LiveList>
    private ArrayList<ChannelBean> liveList;
    private String name, SrcIP, SrcPort, PacketSize, LiveIP, LivePort, TrainIP, TrainPort;
    //    <Server CenterIP="10.69.11.1" CmdServiceIP="10.69.11.1" CmdServicePort="7650" UpgradeIP="10.1.0.10" />
    private String centerIP, cmdServiceIP, cmdServicePort, upgradeIP;
    private ArrayList<String> initC;//开机启动项
//   <Station Code="S0101" Name="公主坟" EnName="Gongzhufen" Map="001;" IsTrain="0" StartTime="00:05:00" StopTime="23:45:00" />/
     private Time openScreenTime,closeScreenTime;
    /**
     * 电源控制器
     <PCtrl type="2" Count="2" Enable="1" Com="com1/ip" Port="4567" Out="1" on="1" off="1">
       <Device1 Addr="1" FirstPort="4" LastPort="4"/>
       <Device2 Addr="2" FirstPort="4" LastPort="4"/>
     </PCtrl>
     */
    private int pctrlType,pctrlCount,pctrlEnable,pctrlPort,pctrlOn,pctrOff;
    private String pctrlCom,pctrlOut,lineCode,stationName,stationEnName;
    private ArrayList<ChannelBean> pctrlList;

    public static GlobalBean getInstance() {
        if (globalBean == null) {
            globalBean = readGlobaParam();
        }
        return globalBean;
    }

    public static GlobalBean readGlobaParam() {
        GlobalBean globalBean = null;
        try {
            globalBean = Const.parseXml.ParseGlobal();
        } catch (Exception e) {
            LogUtil.e(TAG, "readGlobaParam", e);
        }
        return globalBean;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationEnName() {
        return stationEnName;
    }

    public void setStationEnName(String stationEnName) {
        this.stationEnName = stationEnName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public static GlobalBean getGlobalBean() {
        return globalBean;
    }

    public static String getTAG() {
        return TAG;
    }

    public static ParseXml getParseXml() {
        return parseXml;
    }

    public String getIsTrain() {
        return IsTrain;
    }

    public String getDeviceCode() {
        return DeviceCode;
    }

    public String getMCIP() {
        return MCIP;
    }

    public String getMCPort() {
        return MCPort;
    }

    public String getAtsID() {
        return AtsID;
    }

    public String getForwardMode() {
        return ForwardMode;
    }

    public ArrayList<ChannelBean> getLiveList() {
        return liveList;
    }

    public static void setGlobalBean(GlobalBean globalBean) {
        GlobalBean.globalBean = globalBean;
    }

    public static void setParseXml(ParseXml parseXml) {
        GlobalBean.parseXml = parseXml;
    }

    public void setIsTrain(String isTrain) {
        IsTrain = isTrain;
    }

    public void setDeviceCode(String deviceCode) {
        DeviceCode = deviceCode;
    }

    public void setMCIP(String MCIP) {
        this.MCIP = MCIP;
    }

    public void setMCPort(String MCPort) {
        this.MCPort = MCPort;
    }

    public void setAtsID(String atsID) {
        AtsID = atsID;
    }

    public void setForwardMode(String forwardMode) {
        ForwardMode = forwardMode;
    }

    public void setLiveList(ArrayList<ChannelBean> liveList) {
        this.liveList = liveList;
    }

    public String getName() {
        return name;
    }

    public String getSrcIP() {
        return SrcIP;
    }

    public String getSrcPort() {
        return SrcPort;
    }

    public String getPacketSize() {
        return PacketSize;
    }

    public String getLiveIP() {
        return LiveIP;
    }

    public String getLivePort() {
        return LivePort;
    }

    public String getTrainIP() {
        return TrainIP;
    }

    public String getTrainPort() {
        return TrainPort;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSrcIP(String srcIP) {
        SrcIP = srcIP;
    }

    public void setSrcPort(String srcPort) {
        SrcPort = srcPort;
    }

    public void setPacketSize(String packetSize) {
        PacketSize = packetSize;
    }

    public void setLiveIP(String liveIP) {
        LiveIP = liveIP;
    }

    public void setLivePort(String livePort) {
        LivePort = livePort;
    }

    public void setTrainIP(String trainIP) {
        TrainIP = trainIP;
    }

    public void setTrainPort(String trainPort) {
        TrainPort = trainPort;
    }

    public String getCenterIP() {
        return centerIP;
    }

    public String getCmdServiceIP() {
        return cmdServiceIP;
    }

    public String getCmdServicePort() {
        return cmdServicePort;
    }

    public String getUpgradeIP() {
        return upgradeIP;
    }

    public void setUpgradeIP(String upgradeIP) {
        this.upgradeIP = upgradeIP;
    }

    public void setCmdServicePort(String cmdServicePort) {
        this.cmdServicePort = cmdServicePort;
    }

    public void setCmdServiceIP(String cmdServiceIP) {
        this.cmdServiceIP = cmdServiceIP;
    }

    public void setCenterIP(String centerIP) {
        this.centerIP = centerIP;
    }

    public ArrayList<String> getInitC() {
        return initC;
    }

    public void setInitC(ArrayList<String> initC) {
        this.initC = initC;
    }

    public Time getOpenScreenTime() {
        return openScreenTime;
    }

    public void setOpenScreenTime(Time openScreenTime) {
        this.openScreenTime = openScreenTime;
    }

    public Time getCloseScreenTime() {
        return closeScreenTime;
    }

    public void setCloseScreenTime(Time closeScreenTime) {
        this.closeScreenTime = closeScreenTime;
    }

    public String getDevicePos() {
        return DevicePos;
    }

    public void setDevicePos(String devicePos) {
        DevicePos = devicePos;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public int getPctrlType() {
        return pctrlType;
    }

    public void setPctrlType(int pctrlType) {
        this.pctrlType = pctrlType;
    }

    public int getPctrlEnable() {
        return pctrlEnable;
    }

    public void setPctrlEnable(int pctrlEnable) {
        this.pctrlEnable = pctrlEnable;
    }

    public String getPctrlCom() {
        return pctrlCom;
    }

    public void setPctrlCom(String pctrlCom) {
        this.pctrlCom = pctrlCom;
    }

    public int getPctrlPort() {
        return pctrlPort;
    }

    public void setPctrlPort(int pctrlPort) {
        this.pctrlPort = pctrlPort;
    }

    public String getPctrlOut() {
        return pctrlOut;
    }

    public void setPctrlOut(String pctrlOut) {
        this.pctrlOut = pctrlOut;
    }

    public int getPctrlOn() {
        return pctrlOn;
    }

    public void setPctrlOn(int pctrlOn) {
        this.pctrlOn = pctrlOn;
    }

    public int getPctrOff() {
        return pctrOff;
    }

    public void setPctrOff(int pctrOff) {
        this.pctrOff = pctrOff;
    }

    public ArrayList<ChannelBean> getPctrlList() {
        return pctrlList;
    }

    public void setPctrlList(ArrayList<ChannelBean> pctrlList) {
        this.pctrlList = pctrlList;
    }

    public int getPctrlCount() {
        return pctrlCount;
    }

    public void setPctrlCount(int pctrlCount) {
        this.pctrlCount = pctrlCount;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
}
