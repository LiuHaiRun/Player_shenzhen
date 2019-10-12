package com.ghts.player.enumType;

/**
 * @author ljj
 * @updateDes 2018-10-18
 */
public enum  ShowFontType  {

    CHINESE("中文", 0), ENGLISH("英文", 1),  DIGITAL("轮询", 2);

    private String name;	//枚举类型名称
    private int value;	//枚举类型值

    private ShowFontType(String name, int value) {
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
