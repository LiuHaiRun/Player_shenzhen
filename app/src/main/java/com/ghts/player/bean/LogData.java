package com.ghts.player.bean;

import java.io.Serializable;

/**
 * Created by lijingjing on 17-9-13.
 */
public class LogData implements Serializable {

    private static final long serialVersionUID = 1961757797038301256L;
    /**文件标识**/
    private String fileFlag;
    /**版本标识**/
    private String verFlag;
    /**预留**/
    private String reserved;

    /**日志设备的所属线路**/
    private String lineCode;
    /**日志设备的所属车站或列车**/
    private String stationCode;
    /**日志设备的设备ID**/
    private String deviceCode;
    /**日志记录的时间。 ***/
    private String logTime;

    /**换行符***/
    private String newLine="\n";


    /**
     * @return the fileFlag
     */
    public String getFileFlag() {
        return fileFlag;
    }
    /**
     * @param fileFlag the fileFlag to set
     */
    public void setFileFlag(String fileFlag) {
        this.fileFlag = fileFlag;
    }
    /**
     * @return the verFlag
     */
    public String getVerFlag() {
        return verFlag;
    }
    /**
     * @param verFlag the verFlag to set
     */
    public void setVerFlag(String verFlag) {
        this.verFlag = verFlag;
    }
    /**
     * @return the reserved
     */
    public String getReserved() {
        return reserved;
    }
    /**
     * @param reserved the reserved to set
     */
    public void setReserved(String reserved) {
        this.reserved = reserved;
    }
    /**
     * @return the newLine
     */
    public String getNewLine() {
        return newLine;
    }
    /**
     * @param newLine the newLine to set
     */
    public void setNewLine(String newLine) {
        this.newLine = newLine;
    }
    /**
     * @return the lineCode
     */
    public String getLineCode() {
        return lineCode;
    }
    /**
     * @param lineCode the lineCode to set
     */
    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }
    /**
     * @return the stationCode
     */
    public String getStationCode() {
        return stationCode;
    }
    /**
     * @param stationCode the stationCode to set
     */
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
    /**
     * @return the deviceCode
     */
    public String getDeviceCode() {
        return deviceCode;
    }
    /**
     * @param deviceCode the deviceCode to set
     */
    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
    /**
     * @return the logTime
     */
    public String getLogTime() {
        return logTime;
    }
    /**
     * @param logTime the logTime to set
     */
    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }


}
