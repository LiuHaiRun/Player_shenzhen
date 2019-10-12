package com.ghts.player.enumType;

/**
 * Created by lijingjing on 17-6-26.
 * 渐变色
 */
public enum  GradientColorType {

    //  渐变色类型枚举定义：0=水平渐变，1=垂直渐变
    HORIZONTALGRADIENT("水平渐变", 0), VERTICALGRADIENT("垂直渐变", 1);

    private String name;	//枚举类型名称
    private int value;	//枚举类型值

    private GradientColorType(String name, int value) {
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
