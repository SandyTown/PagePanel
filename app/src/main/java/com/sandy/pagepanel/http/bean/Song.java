package com.sandy.pagepanel.http.bean;


public class Song {
    private int songId;
    private String songName;
    private String singerName;
    private String picUrl;

    public Song() {
    }

    public Song(int songId, String songName, String singerName, String picUrl) {
        this.songId = songId;
        this.songName = songName;
        this.singerName = singerName;
        this.picUrl = picUrl;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName == null ? "" : songName;
    }

    public void setSongName(String songName) {
        this.songName = songName == null ? "" : songName;
    }

    public String getSingerName() {
        return singerName == null ? "" : singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName == null ? "" : singerName;
    }

    public String getPicUrl() {
        return picUrl == null ? "" : picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl == null ? "" : picUrl;
    }
}
