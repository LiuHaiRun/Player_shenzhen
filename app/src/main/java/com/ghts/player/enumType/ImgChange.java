package com.ghts.player.enumType;

/**
 * Created by Administrator on 2017/10/31.
 */

public enum ImgChange {

    Hardcut("硬切",0),louver("百叶窗",1),mosaic("马赛克",2),roll("滚动",3),fade("淡入",4),stroke("划向",5),gridbar("栅条",6);

    String name;
    int value;

    private ImgChange(String name,int value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
