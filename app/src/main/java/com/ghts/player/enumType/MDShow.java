package com.ghts.player.enumType;

/**
 *月日显示的枚举类型
 */

public enum MDShow {
    //  月日显示：1=不添加0（如：5月18日），2=添加0（如：05月18日）；仅当显示类型为数字时有效
    NOZERO("不添加0", 1), ADDZERO("添加0", 2);

    private String name;	//枚举类型名称
    private int value;	//枚举类型值

    private MDShow(String name, int value) {
        this.value = value;
        this.name = name;
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