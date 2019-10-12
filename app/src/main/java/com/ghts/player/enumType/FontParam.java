package com.ghts.player.enumType;



/**
 * Created by lijingjing on 17-6-26.
 * 字体的类型
 */
public class FontParam {

    // 字体名称 Name CHAR 文字字体，如“宋体”、“黑体”等 黑体
    private String name;
    // 大小 Size INT 文字大小 20
    private int size;

    // 宽度 Width INT 如宽度值为0，则实际宽度值为文字大小的1/2 0
    private int width;

    // 倾斜度 Escapement INT 0 预留
    private int escapement;

    // 旋转 Orientation INT，0~360o 文字的旋转角度（与X轴） 0 预留
    private int orientation;

    // 字距 Kerning INT 0 预留
    private int kerning;

    // 行距 Spacing INT 0 预留
    private int spacing;

    // 粗体 Bold BOOL FALSE
    private boolean bold;

    // 斜体 Italic BOOL FALSE
    private boolean italic;

    // 下划线 Underline BOOL FALSE
    private boolean underline;

    // 字体颜色 FaceColor RGBA 文字颜色 (0,0,0,255)
    private RGBA faceColor;

    // 边宽度 EdgeWidth INT 文字边缘的宽度 0 预留
    private int edgewidth;

    // 边颜色 EdgeColor RGBA 文字边缘的颜色 (0,0,0,255) 预留
    private RGBA edgeColor;

    // 影宽度 ShadowWidth INT 文字阴影的宽度 0 预留
    private int shadowWidth;

    // 影颜色 ShadowColor RGBA 文字阴影的颜色 (0,0,0,255) 预留
    private RGBA shadowColor;

    // 影角度 ShadowAngle INT，0~360o 文字阴影的（与X轴）角度 0 预留
    private int shadowAngle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getEscapement() {
        return escapement;
    }

    public void setEscapement(int escapement) {
        this.escapement = escapement;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getKerning() {
        return kerning;
    }

    public void setKerning(int kerning) {
        this.kerning = kerning;
    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public RGBA getFaceColor() {
        return faceColor;
    }

    public void setFaceColor(RGBA faceColor) {
        this.faceColor = faceColor;
    }

    public int getEdgewidth() {
        return edgewidth;
    }

    public void setEdgewidth(int edgewidth) {
        this.edgewidth = edgewidth;
    }

    public RGBA getEdgeColor() {
        return edgeColor;
    }

    public void setEdgeColor(RGBA edgeColor) {
        this.edgeColor = edgeColor;
    }

    public int getShadowWidth() {
        return shadowWidth;
    }

    public void setShadowWidth(int shadowWidth) {
        this.shadowWidth = shadowWidth;
    }

    public RGBA getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(RGBA shadowColor) {
        this.shadowColor = shadowColor;
    }

    public int getShadowAngle() {
        return shadowAngle;
    }

    public void setShadowAngle(int shadowAngle) {
        this.shadowAngle = shadowAngle;
    }


}
