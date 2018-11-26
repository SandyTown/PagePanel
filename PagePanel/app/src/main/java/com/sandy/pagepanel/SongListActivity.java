package com.sandy.pagepanel;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sandy.pagepanel.http.bean.Song;
import com.sandy.pagepanel.http.bean.SongResponse;
import com.sandy.pagepanel.model.SongModel;
import com.sandy.pagepanel.observe.TaskData;
import com.sandy.pagepanel.observe.TaskEvent;
import com.sandy.pagepanel.util.FrescoUtil;
import com.sandy.pagepanel.util.ImagePipelineConfigUtils;
import com.sandy.pagepanel.util.ViewUtils;
import com.sandy.pagepanellib.AbstractFramePagePanel;
import com.sandy.pagepanellib.HorizontalFramePagePanel;
import com.sandy.pagepanellib.PagePanelAdapter;

import java.util.ArrayList;
import java.util.List;


public class SongListActivity extends BaseActivity {
    private static final String TAG = SongListActivity.class.getSimpleName();

    private LinearLayout llPagePanel;
    private HorizontalFramePagePanel pagePanel;
    private TextView tvPageCurrent;
    private TextView tvPageTotal;

    private LinearLayout llSongDataEmpty;

    private final SongModel model = new SongModel();
    private boolean isRequesting = false;
    private List<Song> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list_activity);
        Fresco.initialize(this, ImagePipelineConfigUtils.getDefaultImagePipelineConfig(this));
        llPagePanel = findViewById(R.id.ll_page_panel);
        pagePanel = findViewById(R.id.page_panel);
        tvPageCurrent = findViewById(R.id.tv_page_current);
        tvPageTotal = findViewById(R.id.tv_page_total);
        llSongDataEmpty = findViewById(R.id.ll_song_data_empty);

        pagePanel.initParameter();
        pagePanel.setPanelItemSize(2, 5);
        pagePanel.setUseCase(AbstractFramePagePanel.USE_CASE_IN_ACTIVITY);
        pagePanel.setHasDataInServer(true);
        pagePanel.setMainPagePanelAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void requestData() {
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        showProgressDialog();
        if (dataList.isEmpty()) {
            model.requestSongList(0, 10, TaskEvent.GET_SONG_LIST, genTimestamp());
        } else {
            int pageIndex = (int) Math.ceil(dataList.size() * 1.0F / 10);
            model.requestSongList(pageIndex, 10, TaskEvent.GET_SONG_LIST_LOAD_MORE, genTimestamp());
        }
    }

    @Override
    protected void onRequestSuccess(TaskData taskData) {
        if (!containTimestamp(taskData.getTimestamp())) {
            return;
        }
        if (taskData.getEvent() == TaskEvent.GET_SONG_LIST) {
            dismissProgressDialog();
            isRequesting = false;
            SongResponse songResponse = (SongResponse) taskData.getData();
            List<Song> songs = songResponse.getData();

            if (songs == null || songs.isEmpty()) {
                llPagePanel.setVisibility(View.GONE);
                llSongDataEmpty.setVisibility(View.VISIBLE);

                pagePanel.initParameter();
                pagePanel.setHasDataInServer(false);
            } else {
                llPagePanel.setVisibility(View.VISIBLE);
                llSongDataEmpty.setVisibility(View.GONE);
                dataList.clear();
                dataList.addAll(songs);

                pagePanel.initParameter();
                pagePanel.setHasDataInServer(true);
                pagePanel.setDataSize(dataList.size());
                pagePanel.notifyDataSetChanged();
                pagePanel.firstItemViewRequestFocus();
                refreshPageInfo(pagePanel.getCurrentPage());
            }
        } else if (taskData.getEvent() == TaskEvent.GET_SONG_LIST_LOAD_MORE) {
            dismissProgressDialog();
            isRequesting = false;
            SongResponse songResponse = (SongResponse) taskData.getData();
            List<Song> songs = songResponse.getData();

            if (songs == null || songs.isEmpty()) {
                Toast.makeText(this, "亲，没有更多数据了", Toast.LENGTH_SHORT).show();
                pagePanel.setHasDataInServer(false);
            } else {
                dataList.addAll(songs);

                pagePanel.setHasDataInServer(true);
                pagePanel.setDataSize(dataList.size());
                pagePanel.notifyDataSetChanged();
                pagePanel.firstItemViewRequestFocus();
                refreshPageInfo(pagePanel.getCurrentPage());
            }
        }
    }

    @Override
    protected void onRequestSuccessButBusinessFail(TaskData taskData) {
        if (!containTimestamp(taskData.getTimestamp())) {
            return;
        }
        if (taskData.getEvent() == TaskEvent.GET_SONG_LIST) {
            dismissProgressDialog();
            isRequesting = false;
            super.onRequestSuccessButBusinessFail(taskData);
        } else if (taskData.getEvent() == TaskEvent.GET_SONG_LIST_LOAD_MORE) {
            dismissProgressDialog();
            isRequesting = false;
            super.onRequestSuccessButBusinessFail(taskData);
        }
    }

    @Override
    protected void onRequestError(TaskData taskData) {
        if (!containTimestamp(taskData.getTimestamp())) {
            return;
        }
        if (taskData.getEvent() == TaskEvent.GET_SONG_LIST) {
            dismissProgressDialog();
            isRequesting = false;
            super.onRequestError(taskData);
        } else if (taskData.getEvent() == TaskEvent.GET_SONG_LIST_LOAD_MORE) {
            dismissProgressDialog();
            isRequesting = false;
            super.onRequestError(taskData);
        }
    }

    private void refreshPageInfo(int currentPage) {
        int total = dataList.size();
        tvPageCurrent.setText(String.valueOf(currentPage));
        tvPageTotal.setText("/" + (int) Math.ceil(total * 1.0F / 10));
    }

    private final PagePanelAdapter adapter = new PagePanelAdapter() {
        @Override
        public View onCreateView() {
            View view = View.inflate(SongListActivity.this, R.layout.song_item, null);
            return view;
        }

        @Override
        public void onBindDataToView(View view, int position) {
            if (dataList != null && position < dataList.size()) {
                SimpleDraweeView image = view.findViewById(R.id.iv_song_poster);
                ViewUtils.onFocus(image);
                Song song = dataList.get(position);
                FrescoUtil.getInstance().loadImage(SongListActivity.this, image, song.getPicUrl(), FrescoUtil.TYPE_ONE);
                ((TextView) view.findViewById(R.id.tv_song_name)).setText(song.getSongName());
            }
        }

        @Override
        public void onFocusChange(View v, int position, boolean hasFocus) {
            TextView tv = v.findViewById(R.id.tv_song_name);
            if (hasFocus) {
                tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                tv.setSelected(true);
            } else {
                tv.setSelected(false);
            }
            ViewUtils.scaleView(v, hasFocus);
        }

        @Override
        public void onItemClicked(int position) {
            if (dataList != null && position < dataList.size()) {
                StringBuffer sb = new StringBuffer();
                sb.append("ID=").append(dataList.get(position).getSongId());
                sb.append(" NAME=").append(dataList.get(position).getSongName());
                Toast.makeText(SongListActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void loadMoreData() {
            requestData();
        }

        @Override
        public void refreshPageInfo(int currentPage) {
            SongListActivity.this.refreshPageInfo(currentPage);
        }
    };


}
