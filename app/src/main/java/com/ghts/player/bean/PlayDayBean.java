package com.ghts.player.bean;

import java.util.Date;

/**
 * Created by lijingjing on 17-6-23.
 * playdate文件下的playlist，medialist模型
 */
public class PlayDayBean {

//    <d20170424 time="2017-04-24 12:34:56" md5val="">playlist1</d20170424>

    private String dTime,PlayFlag,file;


    public void setdTime(String dTime) {
        this.dTime = dTime;
    }


    public String getdTime() {return dTime;}


    public String getFile() {
        return file;
    }

    public String getPlayFlag() {
        return PlayFlag;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setPlayFlag(String playFlag) {
        PlayFlag = playFlag;
    }
}
