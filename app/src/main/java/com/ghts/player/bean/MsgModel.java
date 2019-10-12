package com.ghts.player.bean;

import java.io.Serializable;

/**
 * Created by lijingjing on 17-4-19.
 */
public class MsgModel implements Serializable{

    private String module;
    private int index,left,right,top,bottom,count;
    private String moduleName;

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

//    Module="TIME" Index="0" Left="858" Right="1920" Top="1" Bottom="195" ModuleName="TIME0" />


    @Override
    public String toString() {
        return "MsgModel{" +
                "module='" + module + '\'' +
                ", index=" + index +
                ", left=" + left +
                ", right=" + right +
                ", top=" + top +
                ", bottom=" + bottom +
                ", moduleName='" + moduleName + '\'' +
                '}';
    }

}
