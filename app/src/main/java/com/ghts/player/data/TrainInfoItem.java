package com.ghts.player.data;

import com.ghts.player.bean.StationViewBean;

public class TrainInfoItem {
    private String ids;
    private StationViewBean TitleView,DestView,TitleEnView,DestEnView,TimeView;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public StationViewBean getTitleView() {
        return TitleView;
    }

    public void setTitleView(StationViewBean titleView) {
        TitleView = titleView;
    }

    public StationViewBean getDestView() {
        return DestView;
    }

    public void setDestView(StationViewBean destView) {
        DestView = destView;
    }

    public StationViewBean getTitleEnView() {
        return TitleEnView;
    }

    public void setTitleEnView(StationViewBean titleEnView) {
        TitleEnView = titleEnView;
    }

    public StationViewBean getDestEnView() {
        return DestEnView;
    }

    public void setDestEnView(StationViewBean destEnView) {
        DestEnView = destEnView;
    }

    public StationViewBean getTimeView() {
        return TimeView;
    }

    public void setTimeView(StationViewBean timeView) {
        TimeView = timeView;
    }

    @Override
    public String toString() {
        return "TrainInfoItem{" +
                "ids='" + ids + '\'' +
                ", TitleView=" + TitleView +
                ", DestView=" + DestView +
                ", TitleEnView=" + TitleEnView +
                ", DestEnView=" + DestEnView +
                ", TimeView=" + TimeView +
                '}';
    }
}
