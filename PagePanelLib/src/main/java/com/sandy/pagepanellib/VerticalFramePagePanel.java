package com.sandy.pagepanellib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class VerticalFramePagePanel extends AbstractFramePagePanel {

    private static final String TAG = VerticalFramePagePanel.class.getSimpleName();

    public VerticalFramePagePanel(Context context) {
        super(context);
    }

    public VerticalFramePagePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalFramePagePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected boolean isMostLeftColumn(int position) {
        return position < row;
    }

    @Override
    protected boolean isMostRightColumn(int position) {
        return position >= row * (column - 1) && position < pageSize;
    }

    @Override
    protected boolean isNextGradHasData(int position) {
        //position / row * row，可以有效的找到position所在列的第一个位置，加上row后，找到下一列的第一个位置
        return dataSize > ((currentPage - 1) * pageSize) + (position / row * row + row);
    }

//    @Override
//    protected  boolean translateLogical() {
//        if (hasDataInServer) {
//            //服务器端有数据，执行动画，动画执行一半时，请求数据，请求数据完毕后，执行剩下一半的动画，翻页。TODO
//            animLastColumnSpringback();
//            mainPagePanelAdapter.loadMoreData();
//            return true;
//        } else {
//            //服务器端无数据，执行动画，完毕。
//            if (useCase == USE_CASE_IN_ACTIVITY) {
//                animLastColumnSpringback();
//                return true;
//            } else if (useCase == USE_CASE_IN_FRAGMENT) {
//                return false;
//            } else {
//                return false;
//            }
//        }
//    }


    @Override
    protected void addItemViewsToPanel() {
        float contentWidth = column * itemWidth + column * itemHorizontalSpace + itemHorizontalSpace;
        float startX = (frameWidth - contentWidth) / 2;
        float startY;
        for (int i = 0; i < column; i++) {
            if (i == 0) {
                startX = startX + itemHorizontalSpace * 1.5F;
            } else {
                startX = startX + itemHorizontalSpace;
            }
            startY = 0;
            for (int j = 0; j < row; j++) {
                if (j == 0) {
                    startY = startY + itemVerticalSpace * 1.5F;
                } else {
                    startY = startY + itemVerticalSpace;
                }
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (itemWidth), (int) itemHeight);
                params.leftMargin = (int) (startX);
                params.topMargin = (int) (startY);

                if (j == row - 1) {
                    params.bottomMargin = (int) (itemVerticalSpace * 1.5F);
                }
                if (i == column - 1) {
                    params.rightMargin = (int) (itemHorizontalSpace * 1.5F);
                }
                this.addView(itemViews[i * row + j], params);
                startY = startY + itemHeight;
            }
            startX = startX + itemWidth;
        }
    }


}
