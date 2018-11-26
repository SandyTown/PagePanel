package com.sandy.pagepanellib;

import android.view.View;

public abstract class PagePanelAdapter {

    public abstract View onCreateView();

    public abstract void onBindDataToView(View v, int index);

    public void onFocusChange(View v, int position, boolean hasFocus) {
    }

    public void onItemClicked(int position) {
    }

    public void loadMoreData() {
    }

    public void refreshPageInfo(int currentPage) {
    }

    public boolean isChangeSubcategoryData(boolean toLeft) {
        return true;
    }

}