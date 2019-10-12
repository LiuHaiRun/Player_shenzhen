package com.ghts.player.widget;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by lijingjing on 17-8-29.
 */
public class MExigent_Full extends TextView {
    private int module_type; // 模块类型

    private int zOrder; // 模块的ZOrder

    private String file_version;	//文件版本

    private String module_name;	//控件名


    public MExigent_Full(Context context) {
        super(context);
    }


    public int getModule_type() {
        return module_type;
    }

    public void setModule_type(int module_type) {
        this.module_type = module_type;
    }

    public int getzOrder() {
        return zOrder;
    }

    public void setzOrder(int zOrder) {
        this.zOrder = zOrder;
    }

    public String getFile_version() {
        return file_version;
    }

    public void setFile_version(String file_version) {
        this.file_version = file_version;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }



}
