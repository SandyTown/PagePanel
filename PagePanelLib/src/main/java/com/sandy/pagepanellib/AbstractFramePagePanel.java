package com.sandy.pagepanellib;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;


public abstract class AbstractFramePagePanel extends FrameLayout {

    private static final String TAG = AbstractFramePagePanel.class.getSimpleName();
    public static final int USE_CASE_IN_ACTIVITY = 1;
    public static final int USE_CASE_IN_FRAGMENT = 2;
    public static final int USE_CASE_IN_SUBCATEGORY_IN_FRAGMENT = 3;

    protected Context context;
    protected PagePanelAdapter mainPagePanelAdapter;

    protected int row = 2;
    protected int column = 6;
    protected int pageSize = row * column;
    protected View[] itemViews = new View[pageSize];
    protected int currentPage = 1;
    protected int dataSize = 0;

    private boolean isSlidingEffect = false;
    protected boolean hasDataInServer = false;
    protected View selectedItem;
    protected int useCase = USE_CASE_IN_FRAGMENT;

    //item布局参数
    protected float itemWidth;
    protected float itemHeight;
    protected float itemHorizontalSpace;
    protected float itemVerticalSpace;
    protected float frameWidth;

    protected float bottomHeight;

    public AbstractFramePagePanel(Context context) {
        this(context, null);
    }

    public AbstractFramePagePanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractFramePagePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.FramePageItem);
        frameWidth = ta.getDimension(R.styleable.FramePageItem_frame_width, getResources().getDimension(R.dimen.w_1920));
        itemWidth = ta.getDimension(R.styleable.FramePageItem_item_width, getResources().getDimension(R.dimen.w_200));
        itemHeight = ta.getDimension(R.styleable.FramePageItem_item_height, getResources().getDimension(R.dimen.h_200));
        itemHorizontalSpace = ta.getDimension(R.styleable.FramePageItem_horizontal_space, getResources().getDimension(R.dimen.w_20));
        itemVerticalSpace = ta.getDimension(R.styleable.FramePageItem_vertical_space, getResources().getDimension(R.dimen.h_20));
        bottomHeight = ta.getDimension(R.styleable.FramePageItem_bottom_height, 0);
        ta.recycle();

    }

    public void initParameter() {
        currentPage = 1;
        dataSize = 0;
        hasDataInServer = false;
    }


    public void setPanelItemSize(int row, int column) {
        this.row = row;
        this.column = column;
        pageSize = row * column;
        itemViews = new View[pageSize];
    }

    protected abstract boolean isMostLeftColumn(int position);

    protected abstract boolean isMostRightColumn(int position);

    protected abstract boolean isNextGradHasData(int position);

    protected abstract void addItemViewsToPanel();

    //    protected abstract boolean translateLogical() ;
    protected boolean translateLogical() {
        if (hasDataInServer) {
            //服务器端有数据，执行动画，动画执行一半时，请求数据，请求数据完毕后，执行剩下一半的动画，翻页。TODO
            animLastColumnSpringback();
            mainPagePanelAdapter.loadMoreData();
            return true;
        } else {
            //服务器端无数据，执行动画，完毕。
            if (useCase == USE_CASE_IN_ACTIVITY) {
                animLastColumnSpringback();
                return true;
            } else if (useCase == USE_CASE_IN_FRAGMENT) {
                return false;
            } else {
                return mainPagePanelAdapter.isChangeSubcategoryData(false);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (isMostRightColumn(getFocusPosition())) {
                    //最右边
                    if (dataSize > (currentPage * pageSize)) {
                        //下一页有数据，执行翻页动画，动画完毕后，翻页。TODO
                        if (isSlidingEffect) {
                            animToLeftOutThenIn();
                        } else {
                            toRightPage();
                        }
                        return true;
                    } else {
                        //下一页无数据
                        return translateLogical();
                    }
                } else {
                    //不是最右边
                    if (isNextGradHasData(getFocusPosition())) {
                        //下一列有数据，不做任何操作，完毕
                        return false;
                    } else {
                        //下一列无数据（最后一页会出现，有时候接口返回的数据参差不齐，可能导致这种情况），
                        return translateLogical();
                    }
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (isMostLeftColumn(getFocusPosition())) {
                    //最左边列
                    if (currentPage == 1) {
                        //无前一页
                        if (useCase == USE_CASE_IN_ACTIVITY) {
                            animFirstColumnSpringback();
                            return true;
                        } else if (useCase == USE_CASE_IN_FRAGMENT) {
//                                        animFirstColumnSpringback();
                            return false;
                        } else {
                            return mainPagePanelAdapter.isChangeSubcategoryData(true);
                        }

                    } else {
                        //有前一页，执行动画，翻页。
                        if (isSlidingEffect) {
                            animToRightOutThenIn();
                        } else {
                            toLeftPage();
                        }
                        return true;
                    }
                } else {
                    //不是最左边列
                    return false;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private int getFocusPosition() {
        for (int i = 0; i < pageSize; i++) {
            if (itemViews[i] != null && itemViews[i].getVisibility() == View.VISIBLE && itemViews[i].hasFocus()) {
                return i;
            }
        }
        return 0;
    }

    private void initItemView() {
        for (int i = 0; i < pageSize; i++) {
            final int position = i;
            itemViews[i] = mainPagePanelAdapter.onCreateView();
//            itemViews[i].setTag(R.id.tag_key_focus_scale, true);
            itemViews[i].setFocusable(true);
            itemViews[i].setClickable(true);
            itemViews[i].setVisibility(View.INVISIBLE);
//            itemViews[i].setOnKeyListener(new OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    Logger.d(TAG, "onKey=>event.getAction()=" + event.getAction() + ",keyCode=" + keyCode + " position=" + position);
//                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                            if (isMostRightColumn(position)) {
//                                //最右边
//                                if (dataSize > (currentPage * pageSize)) {
//                                    //下一页有数据，执行翻页动画，动画完毕后，翻页。TODO
//                                    if (isSlidingEffect) {
//                                        animToLeftOutThenIn();
//                                    } else {
//                                        toRightPage();
//                                    }
//                                    return true;
//                                } else {
//                                    //下一页无数据
//                                    return translateLogical();
//                                }
//                            } else {
//                                //不是最右边
//                                if (isNextGradHasData(position)) {
//                                    //下一列有数据，不做任何操作，完毕
//                                    return false;
//                                } else {
//                                    //下一列无数据（最后一页会出现，有时候接口返回的数据参差不齐，可能导致这种情况），
//                                    return translateLogical();
//                                }
//                            }
//                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//                            if (isMostLeftColumn(position)) {
//                                //最左边列
//                                if (currentPage == 1) {
//                                    //无前一页
//                                    if (useCase == USE_CASE_IN_ACTIVITY) {
//                                        animFirstColumnSpringback();
//                                        return true;
//                                    } else if (useCase == USE_CASE_IN_FRAGMENT) {
////                                        animFirstColumnSpringback();
//                                        return false;
//                                    } else {
//                                        return mainPagePanelAdapter.isChangeSubcategoryData(true);
//                                    }
//
//                                } else {
//                                    //有前一页，执行动画，翻页。
//                                    if (isSlidingEffect) {
//                                        animToRightOutThenIn();
//                                    } else {
//                                        toLeftPage();
//                                    }
//                                    return true;
//                                }
//                            } else {
//                                //不是最左边列
//                                return false;
//                            }
//                        }
//                    }
//                    return false;
//                }
//            });

            itemViews[i].setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        selectedItem = v;
                    } else {
                        selectedItem = null;
                    }
                    int index = (currentPage - 1) * pageSize + position;
                    if (index < 0 || index >= dataSize) {
                        index = 0;
                    }
                    scale(v, hasFocus);
                    mainPagePanelAdapter.onFocusChange(v, index, hasFocus);
                }
            });

            itemViews[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (currentPage - 1) * pageSize + position;
                    if (index < 0 || index >= dataSize) {
                        index = 0;
                    }
                    mainPagePanelAdapter.onItemClicked(index);
                }
            });
        }
        addItemViewsToPanel();
    }


    public void notifyDataSetChanged() {
        if (dataSize >= pageSize * currentPage) {
            for (int i = 0; i < pageSize; i++) {
                itemViews[i].setVisibility(View.VISIBLE);
                int position = i + ((currentPage - 1) * pageSize);
                mainPagePanelAdapter.onBindDataToView(itemViews[i], position);
            }
        } else {
            int end = dataSize - (currentPage - 1) * pageSize;
            for (int i = 0; i < end; i++) {
                int position = i + ((currentPage - 1) * pageSize);
                itemViews[i].setVisibility(View.VISIBLE);
                mainPagePanelAdapter.onBindDataToView(itemViews[i], position);
            }

            for (int i = end; i < pageSize; i++) {
                itemViews[i].clearAnimation();
                itemViews[i].setVisibility(View.INVISIBLE);
            }
        }
    }


    public void notifyDataSetChangedWhenLoadMore() {
        if (dataSize > currentPage * pageSize) {
            currentPage++;
            itemViews[0].requestFocus();
            notifyDataSetChanged();
        }
    }


    public void addCurrentPage() {
        currentPage++;
    }

    public void setItemViewsRequestFocus(int index) {
        itemViews[index].requestFocus();
    }

    public void setMainPagePanelAdapter(PagePanelAdapter mainPagePanelAdapter) {
        this.mainPagePanelAdapter = mainPagePanelAdapter;
        initItemView();
    }

    public void firstItemViewRequestFocus() {
        if (itemViews.length > 0) {
            if (itemViews[0].getVisibility() == View.VISIBLE) {
                itemViews[0].requestFocus();
            }
        }
    }

    private void toLeftPage() {
        currentPage--;
        itemViews[0].requestFocus();
        notifyDataSetChanged();
        mainPagePanelAdapter.refreshPageInfo(currentPage);
    }

    private void toRightPage() {
        currentPage++;
        itemViews[0].requestFocus();
        notifyDataSetChanged();
        mainPagePanelAdapter.refreshPageInfo(currentPage);
    }

    private AnimationSet animToRightOut;
    private AnimationSet animToRightIn;
    private volatile boolean animToRightRunning = false;
    private AnimationSet animToLeftOut;
    private AnimationSet animToLeftIn;
    private volatile boolean animToLeftRunning = false;

    private void animToRightOutThenIn() {
        if (animToRightOut == null) {
            animToRightOut = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.translate_to_right_out);
        }

        if (animToRightIn == null) {
            animToRightIn = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.translate_to_right_in);
        }

        if (animToRightRunning) {
            return;
        }
        animToRightRunning = true;
        animToRightOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toLeftPage();
                animToRightIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        initAnimationParameters();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                startAnimation(animToRightIn);
//                if (focusLayout != null) {
//                    focusLayout.startAnimation(animToRightIn);
//                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animToRightOut);
//        if (focusLayout != null) {
//            focusLayout.startAnimation(animToRightOut);
//        }

    }


    private void animToLeftOutThenIn() {
        if (animToLeftOut == null) {
            animToLeftOut = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.translate_to_left_out);
        }

        if (animToLeftIn == null) {
            animToLeftIn = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.translate_to_left_in);
        }

        if (animToLeftRunning) {
            return;
        }
        animToLeftRunning = true;
        animToLeftOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                toRightPage();

                animToLeftIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        initAnimationParameters();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                startAnimation(animToLeftIn);
//                if (focusLayout != null) {
//                    focusLayout.startAnimation(animToLeftIn);
//                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(animToLeftOut);
//        if (focusLayout != null) {
//            focusLayout.startAnimation(animToLeftOut);
//        }

    }


    private AnimationSet firstColumnSpringbackGo;
    private AnimationSet firstColumnSpringbackBack;
    private volatile boolean firstColumnSpringbackRunning = false;
    private AnimationSet lastColumnSpringbackGo;
    private AnimationSet lastColumnSpringbackBack;
    private volatile boolean lastColumnSpringbackRunning = false;

    private void animFirstColumnSpringback() {
        if (firstColumnSpringbackGo == null) {
            firstColumnSpringbackGo = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.translate_first_springback_go);
        }

        if (firstColumnSpringbackBack == null) {
            firstColumnSpringbackBack = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.translate_first_springback_back);
        }

        if (firstColumnSpringbackRunning) {
            return;
        }
        firstColumnSpringbackRunning = true;
        firstColumnSpringbackGo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                firstColumnSpringbackBack.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        initAnimationParameters();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                startAnimation(firstColumnSpringbackBack);
//                if (focusLayout != null) {
//                    focusLayout.startAnimation(firstColumnSpringbackBack);
//                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(firstColumnSpringbackGo);
//        if (focusLayout != null) {
//            focusLayout.startAnimation(firstColumnSpringbackGo);
//        }
    }


    protected void animLastColumnSpringback() {

        if (lastColumnSpringbackGo == null) {
            lastColumnSpringbackGo = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.translate_last_springback_go);
        }

        if (lastColumnSpringbackBack == null) {
            lastColumnSpringbackBack = (AnimationSet) AnimationUtils.loadAnimation(context, R.anim.translate_last_springback_back);
        }

        if (lastColumnSpringbackRunning) {
            return;
        }
        lastColumnSpringbackRunning = true;
        lastColumnSpringbackGo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                lastColumnSpringbackBack.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        initAnimationParameters();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                startAnimation(lastColumnSpringbackBack);
//                if (focusLayout != null) {
//                    focusLayout.startAnimation(lastColumnSpringbackBack);
//                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        startAnimation(lastColumnSpringbackGo);
//        if (focusLayout != null) {
//            focusLayout.startAnimation(lastColumnSpringbackGo);
//        }
    }

    private void initAnimationParameters() {
        animToLeftRunning = false;
        animToRightRunning = false;
        firstColumnSpringbackRunning = false;
        lastColumnSpringbackRunning = false;
    }

    private void scale(final View view, boolean hasFocus) {
        if (hasFocus) {
            view.bringToFront();
//            if (focusLayout != null) {
//                focusLayout.onFocus(view, bottomHeight);
//            }
//            ScaleAnimation animation = (ScaleAnimation) AnimationUtils.loadAnimation(context, R.anim.scale_in);
////            animation.setZAdjustment(Animation.ZORDER_TOP);
//            view.clearAnimation();
//            view.startAnimation(animation);
        } else {
//            ScaleAnimation animation = (ScaleAnimation) AnimationUtils.loadAnimation(context, R.anim.scale_out);
//            animation.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    if (view.getVisibility() != View.VISIBLE) {
//                        view.setAnimation(null);
//                    }
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
//            view.startAnimation(animation);
        }
    }

    public boolean adjustRequestFocus() {
        if (itemViews != null && itemViews.length > 0 && itemViews[0].getVisibility() == View.VISIBLE) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (itemViews != null && itemViews.length > 0 && itemViews[0].getVisibility() == View.VISIBLE) {
                        itemViews[0].requestFocus();
//                        if (focusLayout != null) {
//                            focusLayout.onFocus(itemViews[0]);
//                        }
                    }

                }
            }, 300);
            return true;
        } else {
            return false;
        }
    }

    public void adjustRequestFocus2() {
        if (itemViews != null && itemViews.length > 0 && itemViews[0].getVisibility() == View.VISIBLE) {
            itemViews[0].requestFocus();
        }
    }

    public boolean isSlidingEffect() {
        return isSlidingEffect;
    }

    public void setSlidingEffect(boolean slidingEffect) {
        isSlidingEffect = slidingEffect;
    }

    public boolean isHasDataInServer() {
        return hasDataInServer;
    }

    public void setHasDataInServer(boolean hasDataInServer) {
        this.hasDataInServer = hasDataInServer;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }


    public int getCurrentPage() {
        return currentPage;
    }

    public View getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(View selectedItem) {
        this.selectedItem = selectedItem;
    }

    public int getUseCase() {
        return useCase;
    }

    public void setUseCase(int useCase) {
        this.useCase = useCase;
    }
}
