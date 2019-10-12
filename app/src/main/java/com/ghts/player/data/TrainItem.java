package com.ghts.player.data;

import com.ghts.player.bean.StationViewBean;

/**
 * Created by lijingjing on 17-10-19.
 */
public class TrainItem {

    private String ids;
    private StationViewBean titleView, infoView, titleEnView, infoEnView;

    public StationViewBean getTitleView() {
        return titleView;
    }

    public void setTitleView(StationViewBean titleView) {
        this.titleView = titleView;
    }

    public StationViewBean getInfoView() {
        return infoView;
    }

    public void setInfoView(StationViewBean infoView) {
        this.infoView = infoView;
    }

    public StationViewBean getTitleEnView() {
        return titleEnView;
    }

    public void setTitleEnView(StationViewBean titleEnView) {
        this.titleEnView = titleEnView;
    }

    public StationViewBean getInfoEnView() {
        return infoEnView;
    }

    public void setInfoEnView(StationViewBean infoEnView) {
        this.infoEnView = infoEnView;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "TrainItem{" +
                "titleView=" + titleView +
                ", infoView=" + infoView +
                ", titleEnView=" + titleEnView +
                ", infoEnView=" + infoEnView +
                '}';
    }
}
