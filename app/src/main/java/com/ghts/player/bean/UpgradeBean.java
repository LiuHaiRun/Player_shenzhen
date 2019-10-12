package com.ghts.player.bean;

import java.io.File;
import java.util.Date;

/**
 * Created by lijingjing on 17-9-20.
 */
public class UpgradeBean {



    private String type;
    private File file;
    private Date time;
    private String install;

    @Override
    public String toString() {
        return "UpgradeBean{" +
                "download='" + type + '\'' +
                ", file=" + file +
                ", time=" + time +
                '}';
    }

    public Date getTime() {
        return time;
    }

    public File getFile() {
        return file;
    }

    public String getType() {
        return type;
    }

    public void setType(String download) {
        this.type = download;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getInstall() {
        return install;
    }

    public void setInstall(String install) {
        this.install = install;
    }
}
