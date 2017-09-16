package com.czg.headtoast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @author ：czg
 * @class ：HeadToast.class
 * @date ：2017/9/16.
 * @describe ：TODO(input describe)
 */
public class HeadToast {
    public static final int LENGTH_SHORT = 1000;
    public static final int LENGTH_LONG = 3000;
    Context mContext;
    View mNextView;
    int mDuration;

    private HeadToast(Context context) {
        mContext = context.getApplicationContext();
    }


    public static HeadToast makeText(Context context, View view, int duration) {
        HeadToast result = new HeadToast(context);
        result.mNextView = view;
        result.mDuration = duration;
        return result;
    }

    public static HeadToast makeText(Context context, CharSequence text, int duration) {
        HeadToast result = new HeadToast(context);
        LayoutInflater inflate = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mHeaderToastView = inflate.inflate(R.layout.header_toast_sample, null);
        TextView tv_content = (TextView) mHeaderToastView.findViewById(R.id.tv_message);
        tv_content.setText(text);
        result.mNextView = mHeaderToastView;
        result.mDuration = duration;
        return result;
    }

    public void show() {
        HeadToastManager.getInstance().show(this);
    }
}
