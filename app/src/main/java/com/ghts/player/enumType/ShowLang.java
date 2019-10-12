package com.ghts.player.enumType;

/**
 * 显示语言的枚举类型
 */
public enum  ShowLang {

    ENGLISH("英文", 0), CHINESE("中文", 1),  DIGITAL("数字", 2);

    private String name;	//枚举类型名称
    private int value;	//枚举类型值

    private ShowLang(String name, int value) {
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
