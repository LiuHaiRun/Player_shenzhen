package com.ghts.player.enumType;

/**
 * 背景
 */
public enum  BackType {

    //  背景类型枚举定义：0=纯色，1=渐变色，2=图片，3=不显示
    PURECOLOR("纯色", 0), GRADIENTCOLOR("渐变色", 1), PICTURE("图片", 2), NOTSHOW("不显示", 3);

    private String name;	//枚举类型名称
    private int value;	//枚举类型值

    private BackType(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
