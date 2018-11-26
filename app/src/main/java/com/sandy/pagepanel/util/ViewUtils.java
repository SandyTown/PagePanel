package com.sandy.pagepanel.util;

import android.graphics.PorterDuff;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;



public class ViewUtils {

    /**
     * 动画 放大/缩小
     *
     * @param view
     * @param hasFocus
     */
    public static void scaleView(View view, boolean hasFocus) {
        float scale = hasFocus ? 1.2f : 1.0f;
        view.animate().scaleX(scale).scaleY(scale).setInterpolator(new AccelerateInterpolator()).setDuration(200);
    }

    public static void onFocus(View view) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = -view.getPaddingLeft();
        layoutParams.rightMargin = -view.getPaddingRight();
        layoutParams.bottomMargin = -view.getPaddingBottom();
        layoutParams.topMargin = -view.getPaddingTop();
        view.setLayoutParams(layoutParams);
    }

    public static void scaleView(View view, float scale, boolean hasFocus) {
        if (!hasFocus) {
            scale = 1.0f;
        }
        view.animate().scaleX(scale).scaleY(scale).setInterpolator(new AccelerateInterpolator()).setDuration(200);
    }

    /**
     * 改变图片的颜色
     * 参考：https://www.jianshu.com/p/9cae2250d0ed
     *
     * @param view  ImageView
     * @param color 颜色格式：0xA6FFFFFF
     */
    public static void setViewColorFilter(ImageView view, int color) {
        view.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

}