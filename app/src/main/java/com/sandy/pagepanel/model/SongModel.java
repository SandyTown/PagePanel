package com.sandy.pagepanel.model;

import com.sandy.pagepanel.http.DataProvider;
import com.sandy.pagepanel.http.bean.SongResponse;
import com.sandy.pagepanel.observe.TaskDataHelper;


public class SongModel extends BaseModel {

    public void requestSongList(final int pageIndex, final int pageSize,
                                final int event, final long timestamp) {
        TaskListener taskListener = new TaskListener() {
            @Override
            public void request() throws Exception {

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SongResponse songResponse = DataProvider.getInstance().requestSongList(pageIndex, pageSize);
                TaskDataHelper.handleResponseMessage(songResponse, event, timestamp);

            }
        };

        runTask(taskListener, event, timestamp, "requestSongList|" + pageIndex + "|" + pageSize);
    }
}
