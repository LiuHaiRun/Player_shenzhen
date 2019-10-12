package com.ghts.player.bean;

/**
 * Created by lijingjing on 17-6-20.
 * 图片模型类
 */
public class ImgBean {

    //滤波去燥 logoSofton
    private String w,h,x,y,name,file,logoSofton;

    public void setW(String w) {
        this.w = w;
    }

    public void setH(String h) {
        this.h = h;
    }

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setLogoSofton(String logoSofton) {
        this.logoSofton = logoSofton;
    }

    public String getLogoSofton() {
        return logoSofton;
    }

    public String getW() {
        return w;
    }

    public String getH() {
        return h;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }
}
