package com.ghts.player.bean;

import java.util.List;

/**
 * Created by lijingjing on 17-5-2.
 */
public class DayList {


    //    <Day0 Date="9999-99-99" LayoutNum="4">
//     <Layout0 File="测试TEST1.XML" StartTime="00:00:01" StopDate="364" StopTime="00:00:00" />
//    <Layout1 File="peixun.XML" StartTime="12:00:01" StopDate="364" StopTime="00:00:00" />
//    </Day0>
    private String date;
    private int LayoutNum;
    private List<LayoutBean> layoutLists;

    public String getDate() {
        return date;
    }

    public int getLayoutNum() {
        return LayoutNum;
    }

    public List<LayoutBean> getLayoutLists() {
        return layoutLists;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLayoutLists(List<LayoutBean> layoutLists) {
        this.layoutLists = layoutLists;
    }

    public void setLayoutNum(int layoutNum) {
        LayoutNum = layoutNum;
    }


}
