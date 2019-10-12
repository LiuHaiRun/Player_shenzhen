package com.ghts.player.data;

import java.io.Serializable;

/**
 * @author ljj
 * @version 1.1.3
 * @updateDes $2018-07-23 17:57:00
 */
public class ATSStrainModel implements Serializable {

//<ATS>
//    <StartStation NameCh="湘湖"NameEn="Xianghu"TipCh=" "TipEn=" "/>
//    <CurStation NameCh="下沙西"NameEn="West Xiasha"TipCh=" "TipEn=" "/>
//    <NextStation NameCh="近江"NameEn="JinJiang"TipCh=" "TipEn=" "/>
//    <DestStation NameCh=" "NameEn=" "TipCh=" "TipEn=" "/>
//    <Status NameCh="Train Arried"NameEn=" "TipCh=" "TipEn=" "/>
//</ATS>

    private String NameCh,NameEn,TipCh,TipEn;

    public ATSStrainModel() {

    }

    public String getNameCh() {
        return NameCh;
    }

    public String getNameEn() {
        return NameEn;
    }

    public String getTipCh() {
        return TipCh;
    }

    public String getTipEn() {
        return TipEn;
    }

    public void setNameCh(String nameCh) {
        NameCh = nameCh;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public void setTipCh(String tipCh) {
        TipCh = tipCh;
    }

    public void setTipEn(String tipEn) {
        TipEn = tipEn;
    }

    @Override
    public String toString() {
        return "ATSStrainModel{" +
                "NameCh='" + NameCh + '\'' +
                ", NameEn='" + NameEn + '\'' +
                ", TipCh='" + TipCh + '\'' +
                ", TipEn='" + TipEn + '\'' +
                '}';
    }
}
