package com.ghts.player.bean;

import java.util.Date;
import java.util.List;

/**
 * Created by lijingjing on 17-5-2.
 */
public class PlayList {

//     <List UpdateTime="2017-04-19 11:40:52" DayNum="1">
//    <Day0 Date="9999-99-99" LayoutNum="4">
//    <Layout0 File="测试TEST1.XML" StartTime="00:00:01" StopDate="364" StopTime="00:00:00" />
//    <Layout1 File="peixun.XML" StartTime="12:00:01" StopDate="364" StopTime="00:00:00" />
//    </Day0>
//    </List>

    private Date UpdateTime;
    private String DayNum;
    private List<DayList> dayListList;
    private List<LayoutBean> layoutBeanList;

    public List<LayoutBean> getLayoutBeanList() {
        return layoutBeanList;
    }

    public void setLayoutBeanList(List<LayoutBean> layoutBeanList) {
        this.layoutBeanList = layoutBeanList;
    }

    public void setUpdateTime(Date updateTime) {
        UpdateTime = updateTime;
    }

    public void setDayNum(String dayNum) {
        DayNum = dayNum;
    }

    public void setDayListList(List<DayList> dayListList) {
        this.dayListList = dayListList;
    }

    public Date getUpdateTime() {

        return UpdateTime;
    }

    public String getDayNum() {
        return DayNum;
    }

    public List<DayList> getDayListList() {
        return dayListList;
    }
}
