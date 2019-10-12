package com.ghts.player.bean;

/**
 * Created by lijingjing on 17-7-27.
 * 直播通道与电源控制器共用一个bean
 */
public class ChannelBean {

    //<Channel1 Name="车载直播通道1" SrcIP="235.1.1.10" SrcPort="5022" 车站  PacketSize="1316" LiveIP="224.3.1.51" LivePort="7661" TrainIP="224.3.1.51"  TrainPort="7662" 单播端口/>
    private String Name,SrcIP,SrcPort,PacketSize,LiveIP,LivePort,TrainIP,TrainPort;

//  <Device1 Addr="1" FirstPort="4" LastPort="4"/>
    private String addr,firstPort,lastPort;

    public String getName() {
        return Name;
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
        Name = name;
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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getFirstPort() {
        return firstPort;
    }

    public void setFirstPort(String firstPort) {
        this.firstPort = firstPort;
    }

    public String getLastPort() {
        return lastPort;
    }

    public void setLastPort(String lastPort) {
        this.lastPort = lastPort;
    }

    @Override
    public String toString() {
        return "ChannelBean{" +
                "Name='" + Name + '\'' +
                ", SrcIP='" + SrcIP + '\'' +
                ", SrcPort='" + SrcPort + '\'' +
                ", PacketSize='" + PacketSize + '\'' +
                ", LiveIP='" + LiveIP + '\'' +
                ", LivePort='" + LivePort + '\'' +
                ", TrainIP='" + TrainIP + '\'' +
                ", TrainPort='" + TrainPort + '\'' +
                ", addr='" + addr + '\'' +
                ", firstPort='" + firstPort + '\'' +
                ", lastPort='" + lastPort + '\'' +
                '}';
    }
}
