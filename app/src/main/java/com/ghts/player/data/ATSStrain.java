package com.ghts.player.data;

import java.io.Serializable;

/**
 * @author ljj
 * @version 1.1.3
 * @updateDes $2018-07-23 17:57:00
 */
public class ATSStrain implements Serializable {
    //<ATS>
    //    <StartStation NameCh="湘湖"NameEn="Xianghu"TipCh=" "TipEn=" "/>
    //    <CurStation NameCh="下沙西"NameEn="West Xiasha"TipCh=" "TipEn=" "/>
    //    <NextStation NameCh="近江"NameEn="JinJiang"TipCh=" "TipEn=" "/>
    //    <DestStation NameCh=" "NameEn=" "TipCh=" "TipEn=" "/>
    //    <Status NameCh="Train Arried"NameEn=" "TipCh=" "TipEn=" "/>
    //</ATS>
    private ATSStrainModel startStation,curStation,nextStation,destStation,status;

    public ATSStrain() {
    }

    public ATSStrainModel getStartStation() {
        return startStation;
    }

    public ATSStrainModel getCurStation() {
        return curStation;
    }

    public ATSStrainModel getNextStation() {
        return nextStation;
    }

    public ATSStrainModel getDestStation() {
        return destStation;
    }

    public ATSStrainModel getStatus() {
        return status;
    }

    public void setStartStation(ATSStrainModel startStation) {
        this.startStation = startStation;
    }

    public void setCurStation(ATSStrainModel curStation) {
        this.curStation = curStation;
    }

    public void setNextStation(ATSStrainModel nextStation) {
        this.nextStation = nextStation;
    }

    public void setDestStation(ATSStrainModel destStation) {
        this.destStation = destStation;
    }

    public void setStatus(ATSStrainModel status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ATSStrain{" +
                "startStation=" + startStation.toString() +
                ", curStation=" + curStation +
                ", nextStation=" + nextStation.toString() +
                ", destStation=" + destStation +
                ", status=" + status +
                '}';
    }
}
