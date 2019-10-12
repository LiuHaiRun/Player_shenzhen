package com.ghts.player.bean;


import java.io.Serializable;

/**
 * @author ljj
 * @des 2019-1-15
 */
public class WeatherBean implements Serializable {
    private String station;
    private String upTime;
    private String temperature;
    private String humidity;
    private String wind;

    private String district;
    private String icon;

    public WeatherBean( ) {

    }
    public WeatherBean(String district, String icon) {
        this.district = district;
        this.icon = icon;
    }
    public WeatherBean(String station, String upTime, String temperature, String humidity, String wind, String path) {
        this.station = station;
        this.upTime = upTime;
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.icon = path;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }


    @Override
    public String toString() {
        return "WeatherBean{" +
                "station='" + station + '\'' +
                ", upTime=" + upTime +
                ", temperature='" + temperature + '\'' +
                ", humidity='" + humidity + '\'' +
                ", wind='" + wind + '\'' +
                 ", district='" + district + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
