package com.ghts.player.enumType;

import java.io.File;

/**
 * Created by lijingjing on 17-6-26.
 */
public class BGParam {


    // 背景类型 Type ENUM 参见下节 3
    private BackType type;

    // 纯色 PureColor RGBA 仅当背景类型=0时有效 (0,0,0,255)
    private RGBA purecolor;

    /*** 渐变色1 GradientColor1 RGBA 仅当背景类型=1时有效 (0,0,0,255) 缺省 （128,128,128,255）*/
    private RGBA gradientcolor1;

    /*** 渐变色2 GradientColor2 RGBA 仅当背景类型=1时有效 (0,0,0,255) 缺省 （192,192,192,255）*/
    private RGBA gradientcolor2;

    // 背景图片 BKFile FILENAME 仅当背景类型=2时有效
    private File bkfile;

    // 渐变色类型 GradientColorType ENUM 参见下节 0
    private GradientColorType colorType;

    public BackType getType() {
        return type;
    }

    public void setType(BackType type) {
        this.type = type;
    }

    public RGBA getPurecolor() {
        return purecolor;
    }

    public void setPurecolor(RGBA purecolor) {
        this.purecolor = purecolor;
    }

    public RGBA getGradientcolor1() {
        return gradientcolor1;
    }

    public void setGradientcolor1(RGBA gradientcolor1) {
        this.gradientcolor1 = gradientcolor1;
    }

    public RGBA getGradientcolor2() {
        return gradientcolor2;
    }

    public void setGradientcolor2(RGBA gradientcolor2) {
        this.gradientcolor2 = gradientcolor2;
    }

    public File getBkfile() {
        return bkfile;
    }

    public void setBkfile(File bkfile) {
        this.bkfile = bkfile;
    }

    public GradientColorType getColorType() {
        return colorType;
    }

    public void setColorType(GradientColorType colorType) {
        this.colorType = colorType;
    }


}
