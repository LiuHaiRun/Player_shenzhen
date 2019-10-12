package com.ghts.player.enumType;

/**
 * 年份显示方式的枚举类型
 */
public enum YearShow {
     SHOWFOUR("显示4位", 0), SHOWTWO("显示后2位", 1), NOTSHOW("不显示", 2) ;
    private String name;	//枚举类型名称
    private int value;	//枚举类型值

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

    private YearShow(String name, int value) {
        this.value = value;
        this.name = name;
    }

}

