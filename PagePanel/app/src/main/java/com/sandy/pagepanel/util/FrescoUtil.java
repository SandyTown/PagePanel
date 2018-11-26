package com.sandy.pagepanel.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sandy.pagepanel.R;


public class FrescoUtil {

    private static final String TAG = FrescoUtil.class.getSimpleName();
    /**
     * loading图片为100大小
     */
    public static final int TYPE_ONE = 1;
    /**
     * loading图片为640大小
     */
    public static final int TYPE_TWO = 2;
    /**
     * loading 图片为975大小
     */
    public static final int TYPE_THREE = 3;
    /**
     * loading图片为rectangle
     */
    public static final int TYPE_FOUR = 4;
    /**
     * loading 图片为圆形
     */
    public static final int TYPE_FIVE = 5;
    /**
     * loading图片为rectangle，宽高定为260X160，目前主要用于PlayListMvAdapter
     */
    public static final int TYPE_SIX = 6;

    private static FrescoUtil instance;

    private FrescoUtil() {
    }

    public static FrescoUtil getInstance() {
        if (instance == null) {
            synchronized (FrescoUtil.class) {
                if (instance == null) {
                    instance = new FrescoUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 加载图片100*100
     *
     * @param simpleDraweeView
     * @param url
     */
    public void loadImage(Context context, final SimpleDraweeView simpleDraweeView, final String url, int type) {
        if (TextUtils.isEmpty(url)) {
            simpleDraweeView.setImageResource(R.mipmap.program_loading_100);
            return;
        }
        GenericDraweeHierarchy hierarchy = simpleDraweeView.getHierarchy();
        //设置圆角
        RoundingParams roundingParams = null;
        switch (type) {
            case TYPE_ONE:
                hierarchy.setFailureImage(R.mipmap.program_loading_100);
                hierarchy.setPlaceholderImage(R.mipmap.program_loading_100);
                roundingParams = RoundingParams.fromCornersRadii(
                        context.getResources().getDimensionPixelOffset(R.dimen.w_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.h_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.w_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.h_6));
                break;
            case TYPE_TWO:
                hierarchy.setFailureImage(R.mipmap.program_loading_640);
                hierarchy.setPlaceholderImage(R.mipmap.program_loading_640);
                roundingParams = RoundingParams.fromCornersRadii(
                        context.getResources().getDimensionPixelOffset(R.dimen.w_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.h_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.w_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.h_6));
                break;
            case TYPE_THREE:
                hierarchy.setFailureImage(R.mipmap.program_loading_975);
                hierarchy.setPlaceholderImage(R.mipmap.program_loading_975);
                roundingParams = RoundingParams.fromCornersRadii(
                        context.getResources().getDimensionPixelOffset(R.dimen.w_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.h_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.w_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.h_6));
                break;
            case TYPE_FOUR:
            case TYPE_SIX:
                hierarchy.setFailureImage(R.mipmap.program_loading_rectangle);
                hierarchy.setPlaceholderImage(R.mipmap.program_loading_rectangle);
                roundingParams = RoundingParams.fromCornersRadii(
                        context.getResources().getDimensionPixelOffset(R.dimen.w_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.h_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.w_6),
                        context.getResources().getDimensionPixelOffset(R.dimen.h_6));
                break;
            default:
                break;
        }

        if (roundingParams != null) {
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
            hierarchy.setRoundingParams(roundingParams);
        }

        //渐进式JPEG图仅仅支持网络图Uri.parse(url)
        ImageRequest request = getImageRequest(context, Uri.parse(url), type);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(new BaseControllerListener<ImageInfo>() {

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        ImageLoader.getInstance().reLoad(simpleDraweeView, url, false);
                    }
                })
                .setOldController(simpleDraweeView.getController())
                .setUri(Uri.parse(url))
                .setImageRequest(request)
                .build();
        simpleDraweeView.setHierarchy(hierarchy);
        simpleDraweeView.setController(controller);
    }

    /**
     * 图片压缩
     */
    private ImageRequest getImageRequest(Context context, Uri uri, int type) {

        //根据请求路径生成ImageRequest的构造者
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (type == TYPE_SIX) {
            //宽高暂定为260X160，目前主要用于PlayListMvAdapter
            builder.setResizeOptions(new ResizeOptions(context.getResources().getDimensionPixelOffset(R.dimen.w_260),
                    context.getResources().getDimensionPixelOffset(R.dimen.h_160)));
        }
        return builder.build();
    }

}
