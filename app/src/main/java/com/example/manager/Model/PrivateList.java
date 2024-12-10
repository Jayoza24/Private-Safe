package com.example.manager.Model;

public class PrivateList {

    private String videoName;
    private String size;

    public PrivateList(String videoName,String size) {
        this.videoName = videoName;
        this.size = size;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
