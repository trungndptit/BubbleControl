package com.example.bubblecontrol.bubbleSource;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ExpandedLayout extends FrameLayout {

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    private OnExpandedClickListener mOnExpandedClickListener;

    public void setOnExpandedClickListener(OnExpandedClickListener onExpandedClickListener) {
        mOnExpandedClickListener = onExpandedClickListener;
    }

    void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    WindowManager getWindowManager() {
        return this.windowManager;
    }

    void setViewParams(WindowManager.LayoutParams params) {
        this.params = params;
    }

    WindowManager.LayoutParams getViewParams() {
        return this.params;
    }

    public ExpandedLayout(Context context) {
        super(context);
    }

    public ExpandedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface OnExpandedClickListener{

        void onExpandedClick(ExpandedLayout mExpandedLayout);

    }
}
