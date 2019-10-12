package com.ghts.player.enumType;

/**
 *元素对齐方式
 */
public enum AlignMode {

    MIDDLELEFT("左中", 0), MIDDLERIGHT("右中", 1),CENTER("正中", 2);

    private String name;	//枚举类型名称
    private int value;	//枚举类型值

    private AlignMode(String name, int value) {
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
