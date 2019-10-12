package com.ghts.player.enumType;

/**
 * 文本模块的文字显示枚举类型
 *
 */
public enum ShowType {
     // 显示方式枚举定义：0=左滚，1=上滚，2=静态
    LEFTSCROLL("左滚", 0), UPSCROLL("上滚", 1), NOSCROLL("静态", 2);


    private String name;	//枚举类型名称
    private int value;	//枚举类型值s

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

    private ShowType(String name, int value) {
        this.name = name;
        this.value = value;
    }

}