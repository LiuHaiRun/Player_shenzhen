package com.ghts.player.bean;

import com.ghts.player.enumType.AlignMode;
import com.ghts.player.enumType.FontParam;
import com.ghts.player.enumType.POS;

import java.io.Serializable;

/**
 * Created by lijingjing on 17-9-11.
 */
public class StationViewBean implements Serializable {

    //    <Title Show="0" Align="-1" Name="本站">
//    <Font Name="黑体" Size="25" Width="0" Kerning="0" Leading="8">
//    <Style Slant="0" Rotation="0" Bold="0" Italic="0" Underline="0" />
//    <Face Color="16777215" Alpha="255" />
//    <Edge Type="0" Width="0" Color="13158600" Alpha="255" />
//    <Shadow Type="0" Width="0" Color="0" Alpha="255" Angle="315" Softness="4" />
//    </Font>
//    <Rect left="0" right="85" top="0" bottom="50" />
//    </Title>
    //    <Info Show="0" Align="0">
//    <Font Name="黑体" Size="30" Width="0" Kerning="0" Leading="8">
//    <Style Slant="0" Rotation="0" Bold="0" Italic="0" Underline="0" />
//    <Face Color="16777215" Alpha="255" />
//    <Edge Type="0" Width="0" Color="13158600" Alpha="255" />
//    <Shadow Type="0" Width="0" Color="0" Alpha="255" Angle="315" Softness="4" />
//    </Font>
//    <Rect left="86" right="216" top="0" bottom="50" />
//    </Info>
    private String id;
    private boolean show;
    private AlignMode alignMode;
    private String txt;
    private FontParam fontParam;
    private POS rectPos;

    private FontParam FontNum,FontTrans,FontTransEn;

    public String getId() {
        return id;
    }

    public void setIds(String id) {
        this.id = id;
    }

    public boolean isShow() {
        return show;
    }

    public AlignMode getAlignMode() {
        return alignMode;
    }

    public String getTxt() {
        return txt;
    }

    public FontParam getFontParam() {
        return fontParam;
    }

    public POS getRectPos() {
        return rectPos;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public void setAlignMode(AlignMode alignMode) {
        this.alignMode = alignMode;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public void setFontParam(FontParam fontParam) {
        this.fontParam = fontParam;
    }

    public void setRectPos(POS rectPos) {
        this.rectPos = rectPos;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FontParam getFontNum() {
        return FontNum;
    }

    public void setFontNum(FontParam fontNum) {
        FontNum = fontNum;
    }

    public FontParam getFontTrans() {
        return FontTrans;
    }

    public void setFontTrans(FontParam fontTrans) {
        FontTrans = fontTrans;
    }

    public FontParam getFontTransEn() {
        return FontTransEn;
    }

    public void setFontTransEn(FontParam fontTransEn) {
        FontTransEn = fontTransEn;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
