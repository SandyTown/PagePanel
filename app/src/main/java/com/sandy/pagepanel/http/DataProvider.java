package com.sandy.pagepanel.http;

import com.sandy.pagepanel.base.ResponseCode;
import com.sandy.pagepanel.http.bean.Song;
import com.sandy.pagepanel.http.bean.SongResponse;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DataProvider {
    private static DataProvider instance;

    private List<String> picUrls;

    private DataProvider() {
        picUrls = new ArrayList<>();
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000004QnEHc3zjC7J.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000003V9RQC25xddw.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M0000001XmD20k5a9G.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M0000017PV7Q0xng2C.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000004ZrpRK2Lpf9z.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000002Km75T2Ea1Yj.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M0000010UC5f4Bhd2n.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000003NThF42HaO9r.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000000oxiyL2bywsE.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M0000010dTfq1sWSOh.jpg?max_age=2592000");

        picUrls.add("https://p.qpic.cn/music_cover/xCubmCUxl5icKI16GSM3EX0GzraJpzEAQf4TvrpazxFhqZ8YA4IOibqg/300?n=1");
        picUrls.add("https://p.qpic.cn/music_cover/qB1icvKIehIKmroV6TNic9DySsXNm6AGRurwtEyficnOVcLfFib08RPS3g/300?n=1");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000000Zo4971EYeQl.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000001ZQwIZ1upguG.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000000qqSLj17RUBc.jpg?max_age=2592000");
        picUrls.add("https://y.gtimg.cn/music/photo_new/T002R300x300M000004dbhlO3x0LwE.jpg?max_age=2592000");

    }

    public static DataProvider getInstance() {
        if (instance == null) {
            synchronized (DataProvider.class) {
                if (instance == null) {
                    instance = new DataProvider();
                }
            }
        }
        return instance;
    }

    public SongResponse requestSongList(int pageIndex, int pageSize) throws IOException, JSONException {
        int total = 300;
        SongResponse songResponse = new SongResponse();
        songResponse.setCode(ResponseCode.CODE_SUCCESS);
        songResponse.setMsg("请求数据成功");
        songResponse.setTotal(total);

        List<Song> songs = new ArrayList<>();
        int startIndex = pageIndex * pageSize;
        int endIndex = startIndex + pageSize;
        endIndex = endIndex < total ? endIndex : total;

        Random random = new Random();
        for (int i = startIndex; i < endIndex; i++) {
            Song song = new Song();
            song.setSongId(i);
            song.setSongName("歌曲名称" + i);
            song.setSingerName("歌手名称" + i);
            song.setPicUrl(picUrls.get(random.nextInt(16)));
            songs.add(song);
        }

        if (random.nextInt(6) == 3) {
            songResponse.setData(null);//表示结束
        } else {
            songResponse.setData(songs);
        }
        return songResponse;


    }

}
