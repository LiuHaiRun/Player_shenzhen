package com.ghts.player.enumType;

/**
 * 表示背景类型的枚举类型
 */
public enum DateShow {
    //  日期显示顺序：0=年月日，1=月日年，2=日月年
    YMD("年月日", 0), MDY("月日年", 1), DMY("日月年", 2);

    private String name;	//枚举类型名称
    private int value;	//枚举类型值

    private DateShow(String name, int value) {
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

