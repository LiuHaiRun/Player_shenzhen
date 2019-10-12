package com.ghts.player.data;

/**
 * @author ljj
  * @updateDes 2018-11-14
 * 解析staion.xml目的码表
 */
public class Stationxml  {

//  <Params ver="1.0" md5val="" Count=3>
//	<Station0 Code="stationcode" Name="" EnName="" Map="01,02,03"/>
//	<Station1 Code="" Name="" EnName="" Map=""/>
//	<Station2 Code="" Name="" EnName="" Map=""/>

    private int count;
    private String code,map,name,enName;

    public Stationxml(String code, String map, String name, String enName) {
        this.code = code;
        this.map = map;
        this.name = name;
        this.enName = enName;
    }

    public Stationxml(int count, String code, String map, String name, String enName) {
        this.count = count;
        this.code = code;
        this.map = map;
        this.name = name;
        this.enName = enName;
    }

    @Override
    public String toString() {
        return "station{" +
                "Code='" + code + '\'' +
                ", Map='" + map + '\'' +
                ", Name='" + name + '\'' +
                ", EnName='" + enName + '\'' +
                '}';
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }
}
