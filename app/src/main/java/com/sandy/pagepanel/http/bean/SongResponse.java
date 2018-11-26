package com.sandy.pagepanel.http.bean;

import com.sandy.pagepanel.base.BaseResponse;

import java.util.List;


public class SongResponse extends BaseResponse {
    private int total;
    private List<Song> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Song> getData() {
        return data;
    }

    public void setData(List<Song> data) {
        this.data = data;
    }
}
