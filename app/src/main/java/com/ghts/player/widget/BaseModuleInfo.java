package com.ghts.player.widget;

import android.content.Context;
import android.view.View;

import com.ghts.player.enumType.POS;


/**
 * Created by lijingjing on 17-6-13.
 *  所有控件的基类
 */
public abstract class BaseModuleInfo {

    //    <Item0 Module="TITLE" Index="0" Left="28" Right="456" Top="24" Bottom="111" ModuleName="TITLE0" />
    protected int module_type; // 模块类型

    protected int zOrder; // 模块的ZOrder

    protected String index;

    protected String module_name;

    protected POS pos;

    /**
     * 得到根据BaseModule子类的信息初始化后的控件
     *
     * @param context 上下文
     * @return 初始化后的控件
     */

    protected abstract View getView(Context context);

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
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

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public POS getModule_Pos() {
        return pos;
    }

    public void setModule_Pos(POS module_Pos) {
        pos = module_Pos;
    }


}
