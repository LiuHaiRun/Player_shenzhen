package com.ghts.player.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ghts.player.anim.SlideShowView;
import com.ghts.player.enumType.ImgChange;
import com.ghts.player.enumType.POS;
import com.ghts.player.utils.Const;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/31.
 */

public class AdvertisementView extends BaseModuleInfo {

    private ImgChange stype;
    private ArrayList<String>  imgPaths;
    private int duration;

    @Override
    public View getView(Context context) {
        SlideShowView slideShowView = new SlideShowView(context);
        slideShowView.setFileList(imgPaths);
        slideShowView.setImg(duration);
        initPosition(slideShowView, pos);

        return slideShowView;
    }

    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(Const.screenH - pos.getTop() - pos.getHeight());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }
    public ImgChange getStype() {
        return stype;
    }

    public void setStype(ImgChange stype) {
        this.stype = stype;
    }

    public ArrayList<String> getImgPaths() {
        return imgPaths;
    }

    public void setImgPaths(ArrayList<String> imgPaths) {
        this.imgPaths = imgPaths;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
