package com.ghts.player.bean;

import java.util.ArrayList;

/**
 * Created by lijingjing on 17-6-12.
 * PLAYDATE模型
 */
public class PlayDateBean {

    private String version;
    private ArrayList<PlayDayBean> playlists;
    private ArrayList<PlayDayBean> mediaList;
    private ArrayList<PlayDayBean> defaultlist;

    public String getVersion() {
        return version;
    }

    public ArrayList<PlayDayBean> getDayList() {
        return playlists;
    }

    public ArrayList<PlayDayBean> getMediaList() {
        return mediaList;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDayList(ArrayList<PlayDayBean> Playlist) {
        this.playlists = Playlist;
    }

    public void setMediaList(ArrayList<PlayDayBean> mediaList) {
        this.mediaList = mediaList;
    }

    public ArrayList<PlayDayBean> getPlaylists() {
        return playlists;
    }

    public void setDefaultlist(ArrayList<PlayDayBean> defaultlist) {
        this.defaultlist = defaultlist;
    }

    public ArrayList<PlayDayBean> getDefaultlist() {
        return defaultlist;
    }
}
