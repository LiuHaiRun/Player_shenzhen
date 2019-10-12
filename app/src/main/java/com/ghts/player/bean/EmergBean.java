package com.ghts.player.bean;

import java.util.List;

/**
 * @author ljj
 * @updateDes 2018/9/13
 * 读取数据库的紧急消息模块
 */
public class EmergBean {
//[{"Info_id":"10001","Info_name":"2018-09-13","Info_cotent":"换乘乘坐杭州地铁1号线",
//            "Info_type":"","Sub_type":"","Start_date":"2018-09-13 00:00:00.0",
//            "Stop_date":"2018-09-13 00:00:00.0","Start_time":"00:00:00",
//            "Stop_time":"00:00:00","Editor_id":"000001","Edit_time":"2018-09-13 11:25:36.0",
//            "Audit":"1","Device_pos":"列车","Line_code":"L001","Station_code":"T0101"},

    /**
     * md5val : d36f781bc5f01003ca139a79ac929282
     * contents : ["最新信息发布"]
     */

    private String md5val;
    private List<String> contents;

    public String getMd5val() {
        return md5val;
    }

    public void setMd5val(String md5val) {
        this.md5val = md5val;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }



    private String Info_id;
    private String Info_name;
    private String Info_cotent;
    private String Info_type;
    private String Sub_type;
    private String Start_date;
    private String Stop_date;
    private String Start_time;
    private String Stop_time;
    private String Editor_id;
    private String Edit_time;
    private String Audit;
    private String Device_pos;
    private String Line_code;
    private String Station_code;

    public String getInfo_id() {
        return Info_id;
    }

    public String getInfo_name() {
        return Info_name;
    }

    public String getInfo_cotent() {
        return Info_cotent;
    }

    public String getInfo_type() {
        return Info_type;
    }

    public String getSub_type() {
        return Sub_type;
    }

    public String getStart_date() {
        return Start_date;
    }

    public String getStop_date() {
        return Stop_date;
    }

    public String getStart_time() {
        return Start_time;
    }

    public String getStop_time() {
        return Stop_time;
    }

    public String getEditor_id() {
        return Editor_id;
    }

    public String getEdit_time() {
        return Edit_time;
    }

    public String getAudit() {
        return Audit;
    }

    public String getDevice_pos() {
        return Device_pos;
    }

    public String getLine_code() {
        return Line_code;
    }

    public String getStation_code() {
        return Station_code;
    }

    public void setInfo_id(String info_id) {
        Info_id = info_id;
    }

    public void setInfo_name(String info_name) {
        Info_name = info_name;
    }

    public void setInfo_cotent(String info_cotent) {
        Info_cotent = info_cotent;
    }

    public void setInfo_type(String info_type) {
        Info_type = info_type;
    }

    public void setSub_type(String sub_type) {
        Sub_type = sub_type;
    }

    public void setStart_date(String start_date) {
        Start_date = start_date;
    }

    public void setStop_date(String stop_date) {
        Stop_date = stop_date;
    }

    public void setStart_time(String start_time) {
        Start_time = start_time;
    }

    public void setStop_time(String stop_time) {
        Stop_time = stop_time;
    }

    public void setEditor_id(String editor_id) {
        Editor_id = editor_id;
    }

    public void setEdit_time(String edit_time) {
        Edit_time = edit_time;
    }

    public void setAudit(String audit) {
        Audit = audit;
    }

    public void setDevice_pos(String device_pos) {
        Device_pos = device_pos;
    }

    public void setLine_code(String line_code) {
        Line_code = line_code;
    }

    public void setStation_code(String station_code) {
        Station_code = station_code;
    }

    public EmergBean() {
     }

    public EmergBean(String info_id, String info_name, String info_cotent, String info_type, String sub_type, String start_date, String stop_date, String start_time, String stop_time, String editor_id, String edit_time, String audit, String device_pos, String line_code, String station_code) {
        Info_id = info_id;
        Info_name = info_name;
        Info_cotent = info_cotent;
        Info_type = info_type;
        Sub_type = sub_type;
        Start_date = start_date;
        Stop_date = stop_date;
        Start_time = start_time;
        Stop_time = stop_time;
        Editor_id = editor_id;
        Edit_time = edit_time;
        Audit = audit;
        Device_pos = device_pos;
        Line_code = line_code;
        Station_code = station_code;
    }

    @Override
    public String toString() {
        return "EmergBean{" +
                "Info_id='" + Info_id + '\'' +
                ", Info_name='" + Info_name + '\'' +
                ", Info_cotent='" + Info_cotent + '\'' +
                ", Info_type='" + Info_type + '\'' +
                ", Sub_type='" + Sub_type + '\'' +
                ", Start_date='" + Start_date + '\'' +
                ", Stop_date='" + Stop_date + '\'' +
                ", Start_time='" + Start_time + '\'' +
                ", Stop_time='" + Stop_time + '\'' +
                ", Editor_id='" + Editor_id + '\'' +
                ", Edit_time='" + Edit_time + '\'' +
                ", Audit='" + Audit + '\'' +
                ", Device_pos='" + Device_pos + '\'' +
                ", Line_code='" + Line_code + '\'' +
                ", Station_code='" + Station_code + '\'' +
                '}';
    }
}
