package com.czg.headtoast;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

/**
 * @author ：czg
 * @class ：ShowVIew.class
 * @date ：2017/9/16.
 * @describe ：TODO(input describe)
 */
public class ShowView {
    private View contentView;
    private WindowManager wm;
    private final int mDuration;
    private final static int ANIM_DURATION = 600;
    private final Context mContext;
    private HeadContentView currentView;
    volatile int state = DEFULT;
    public static final int DISMISS = 0;
    public static final int SHOW = 1;
    public static final int DEFULT = -1;
    public static final int TOUCH = -2;
    public static final int AUTO = -3;
    public static final int DELAYED = -4;


    public ShowView(HeadToast headToast) {
        wm = (WindowManager) headToast.mContext.getSystemService(Context.WINDOW_SERVICE);
        mDuration = headToast.mDuration;
        contentView = headToast.mNextView;
        mContext = headToast.mContext;

    }


    public int getDuration() {
        return mDuration;
    }

    void show() {
        //为mHeaderToastView添加parent使其能够展示动画效果
        currentView = new HeadContentView(mContext);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        currentView.setLayoutParams(llParams);
        //  currentView.setOnTouchListener(this);
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        wmParams.gravity = Gravity.CENTER | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        currentView.addView(contentView, currentView.getLayoutParams());
        wm.addView(currentView, wmParams);
        ObjectAnimator a = ObjectAnimator.ofFloat(contentView, "translationY", -700, 0);
        a.setDuration(ANIM_DURATION);
        a.start();

        currentView.setOnScrollListener(new HeadContentView.OnScrollListener() {
            @Override
            public void onStateChange(int state) {
                ShowView.this.state = state;
            }

            @Override
            public void onHideByScroll(int x, int toX) {

                touchHide(x, toX);


            }

            @Override
            public void onDelayedHide() {
                postHideDelayed();
            }

            @Override
            public void cancelHideDelayed() {
                if (currentView != null) {
                    currentView.removeCallbacks(delayedRunnable);
                }
            }
        });
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    OnDismissListener mDismissListener;

    public void setDismissListener(OnDismissListener dismissListener) {
        mDismissListener = dismissListener;
    }

    private void dismiss() {
        state = DISMISS;
        if (null != currentView && null != currentView.getParent()) {
            wm.removeView(currentView);
        }
        if (mDismissListener != null) {
            mDismissListener.onDismiss();
        }
    }



    private void postHideDelayed() {//会谈后的隐藏
        if (this.state == DELAYED) {
            this.state = DISMISS;
            Log.e("translationX", "延时隐藏" + state);
            if (currentView != null) {
                currentView.postDelayed(delayedRunnable, 500);
            }
        }
    }

    void autoHide() {
        if (state == AUTO) {
            if (currentView != null) {
                currentView.post(delayedRunnable);
            }
        }
    }

    void touchHide(int x, int toX) {
        if (ShowView.this.state == TOUCH) {
            hide(x, toX);
        }
    }


    private void hide() {
        hide(0, -currentView.getMeasuredWidth());
    }

    void hide(float X, float toX) {
        state = DISMISS;
        if (null == currentView || null == currentView.getParent()) {
            //如果linearLayout已经被从wm中移除，直接return
            dismiss();
            return;
        }
        Log.e("translationX", "......x:" + X + "......toX:" + toX);
        ObjectAnimator a = ObjectAnimator.ofFloat(currentView, "translationX", X, toX);
        a.setDuration((long) Math.abs(ANIM_DURATION * toX / currentView.getMeasuredWidth()));
        a.setInterpolator(new LinearInterpolator());
        a.start();
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }
        });
    }


    private Runnable delayedRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentView != null) {
                currentView.removeCallbacks(this);
                hide();
            }

        }
    };

}