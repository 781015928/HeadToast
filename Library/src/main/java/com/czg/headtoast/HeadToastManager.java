package com.czg.headtoast;

import android.os.Handler;
import android.os.Looper;

import java.util.LinkedList;
import java.util.Queue;

import static com.czg.headtoast.ShowView.AUTO;
import static com.czg.headtoast.ShowView.SHOW;

/**
 * @author ：czg
 * @class ：HeadToastManager.class
 * @date ：2017/9/16.
 * @describe ：TODO(input describe)
 */
public class HeadToastManager {
    private static HeadToastManager sInstance = new HeadToastManager();
    private final Handler mHandler;
    private final Queue<ShowTask> queue;

    public static HeadToastManager getInstance() {
        return sInstance;
    }

    public HeadToastManager() {
        mHandler = new Handler(Looper.getMainLooper());
        queue = new LinkedList<ShowTask>();
    }

    public void show(HeadToast headToast) {
        ShowView showView = new ShowView(headToast);
        final ShowTask showTask = new ShowTask(showView);

        showTask.setHandler(mHandler);
        showTask.setQueue(queue);

        if (queue.isEmpty()) {
            mHandler.post(showTask);
        }
        queue.add(showTask);
        showView.setDismissListener(new ShowView.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (queue != null) {
                    queue.remove(showTask);
                }
                ShowTask peek = queue.peek();
                if (peek != null) {
                    mHandler.post(peek);
                }
            }
        });
    }

    static class ShowTask implements Runnable {
        private final ShowView mShowView;


        private Handler mHandler;
        private Queue<ShowTask> mQueue;


        public ShowTask(ShowView showView) {
            mShowView = showView;
        }


        @Override
        public void run() {
            switch (mShowView.state) {
                case ShowView.DEFULT:
                    mShowView.show();
                    mShowView.state = SHOW;
                    mHandler.postDelayed(this, mShowView.getDuration());
                    break;
                case ShowView.SHOW:
                    mShowView.state = AUTO;
                    mShowView.autoHide();

                    break;
            }
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        public void setQueue(Queue<ShowTask> queue) {
            mQueue = queue;
        }
    }

}
