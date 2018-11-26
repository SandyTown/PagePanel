package com.sandy.pagepanellib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class HorizontalFramePagePanel extends AbstractFramePagePanel {

    private static final String TAG = HorizontalFramePagePanel.class.getSimpleName();

    public HorizontalFramePagePanel(Context context) {
        super(context);
    }

    public HorizontalFramePagePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalFramePagePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected boolean isMostLeftColumn(int position) {
        return position % column == 0;
    }

    @Override
    protected boolean isMostRightColumn(int position) {
        return (position + 1) % column == 0;
    }

    @Override
    protected boolean isNextGradHasData(int position) {
        return dataSize > ((currentPage - 1) * pageSize) + position + 1;
    }

//    @Override
//    protected boolean translateLogical() {
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
//                return mainPagePanelAdapter.isChangeSubcategoryData(false);
//            }
//        }
//    }


    @Override
    protected void addItemViewsToPanel() {
        float contentWidth = column * itemWidth + column * itemHorizontalSpace + itemHorizontalSpace;
        float startX;
        float startY = 0;
        for (int i = 0; i < row; i++) {
            startX = (frameWidth - contentWidth) / 2;
            if (i == 0) {
                startY = startY + itemVerticalSpace * 1.5F;
            } else {
                startY = startY + itemVerticalSpace;
            }
            for (int j = 0; j < column; j++) {
                if (j == 0) {
                    startX = startX + itemHorizontalSpace * 1.5F;
                } else {
                    startX = startX + itemHorizontalSpace;
                }
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (itemWidth), (int) itemHeight);
                params.leftMargin = (int) (startX);
                params.topMargin = (int) (startY);

                if (j == column - 1) {
                    params.rightMargin = (int) (itemHorizontalSpace * 1.5F);
                }
                if (i == row - 1) {
                    params.bottomMargin = (int) (itemVerticalSpace * 1.5F);
                }
                this.addView(itemViews[i * column + j], params);
                startX = startX + itemWidth;
            }
            startY = startY + itemHeight;
        }
    }


}
