package com.czg.headtoast;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * @author ：czg
 * @class ：HeadContentView.class
 * @date ：2017/9/16.
 * @describe ：TODO(input describe)
 */
public class HeadContentView extends FrameLayout {

    private final Scroller mScroller;

    public HeadContentView(@NonNull Context context) {
        super(context);
        mScroller = new Scroller(context);
    }

    private int startX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();//表示相对于当前view的x
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                if (mOnScrollListener != null) {
                    mOnScrollListener.onStateChange(ShowView.TOUCH);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (mOnScrollListener != null) {
                    mOnScrollListener.onStateChange(ShowView.TOUCH);
                }
                int distanceX = x - startX;
                scrollBy(-distanceX, 0);
                startX = x;
                break;
            case MotionEvent.ACTION_UP:
                if (mOnScrollListener != null) {
                    mOnScrollListener.onStateChange(ShowView.TOUCH);
                }
                if (Math.abs(getScrollX()) > getMeasuredWidth() / 5) {//从手动滑动变为自动滑动
                    Log.e("translationX", "滑动隐藏");
                    if (getScrollX() > 0) {
                        if (mOnScrollListener != null) {
                            mOnScrollListener.onHideByScroll(-getScrollX(), -getScrollX() - getMeasuredWidth());
                        }
                    } else {
                        if (mOnScrollListener != null) {
                            mOnScrollListener.onHideByScroll(-getScrollX(), getMeasuredWidth() - getScrollX());
                        }
                    }

                } else {

                    smoothScrollTo(0, 0);//自动回弹回去
                }


                break;
        }
        return true;
    }

    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        smoothScrollBy(dx, 0);
    }
    //调用此方法设置滚动的相对偏移

    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    OnScrollListener mOnScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onStateChange(int state);

        void onHideByScroll(int x, int toX);

        void onDelayedHide();//延时

        void cancelHideDelayed();

    }

    private boolean isScroll;

    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            isScroll = true;
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        } else {
            if (isScroll) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onStateChange(ShowView.DELAYED);
                    mOnScrollListener.onDelayedHide();//这里是回弹后的隐藏 只能调用一次
                    isScroll = false;
                }
            }

        }
        super.computeScroll();
    }
}
