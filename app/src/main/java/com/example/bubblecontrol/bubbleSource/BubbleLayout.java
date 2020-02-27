package com.example.bubblecontrol.bubbleSource;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.example.bubblecontrol.R;

public class BubbleLayout extends BubbleBaseLayout {

    private float initialTouchX;
    private float initialTouchY;
    private int initialX;
    private int initialY;
    private int width;

    private MoveAnimator animator;

    private OnBubbleRemoveListener onBubbleRemoveListener;
    private OnBubbleClickListener onBubbleClickListener;

    private static final int TOUCH_TIME_THRESHOLD = 150;
    private long lastTouchDown;

    private WindowManager windowManager;
    private boolean shouldStickToWall = true;
    private boolean isExpanded = false;

    public BubbleLayout(Context context) {
        super(context);
        animator = new MoveAnimator();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initializeView();
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        animator = new MoveAnimator();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initializeView();
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        animator = new MoveAnimator();
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        initializeView();
    }

    // set------------------------------
    public void setOnBubbleRemoveListener(OnBubbleRemoveListener listener) {
        onBubbleRemoveListener = listener;
    }

    public void setOnBubbleClickListener(OnBubbleClickListener listener) {
        onBubbleClickListener = listener;
    }

    public void setShouldStickToWall(boolean shouldStick) {
        this.shouldStickToWall = shouldStick;
    }

    public void setIsExpanded(Boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
    //end set ------------------------

    // handle touch -----------------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    initialX = getViewParams().x;
                    initialY = getViewParams().y;

                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();

                    playAnimationClickDown();
                    lastTouchDown = System.currentTimeMillis();
                    updateSize();
                    animator.stop();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = initialX + (int) (event.getRawX() - initialTouchX);
                    int y = initialY + (int) (event.getRawY() - initialTouchY);
                    getViewParams().x = x;
                    getViewParams().y = y;
                    getWindowManager().updateViewLayout(this, getViewParams());
                    if (getLayoutCoordinator() != null) {
                        getLayoutCoordinator().notifyBubblePositionChanged(this, x, y);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    goToWall();
                    System.out.println("Debug x= " + getViewParams().x + " y= " + getViewParams().y);
                    if (getLayoutCoordinator() != null) {
                        getLayoutCoordinator().notifyBubbleRelease(this);
                        playAnimationClickUp();
                    }
                    if (System.currentTimeMillis() - lastTouchDown < TOUCH_TIME_THRESHOLD) {
                        if (onBubbleClickListener != null) {
                                onBubbleClickListener.onBubbleClick(this);
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }
    // end handle touch ---------------------

    private void initializeView() {
        setClickable(true);
    }

    void notifyBubbleRemoved() {
        if (onBubbleRemoveListener != null) {
            onBubbleRemoveListener.onBubbleRemoved(this);
        }
    }

    public void goToWall() {
        if (shouldStickToWall) {
            int middle = width / 2;
            float nearestXWall = getViewParams().x >= middle ? width : 0; // if true => nearestXWall = width, false => = 0
            animator.start(nearestXWall, getViewParams().y);
        }
    }

    private void move(float deltaX, float deltaY) {
        getViewParams().x += deltaX;
        getViewParams().y += deltaY;
        windowManager.updateViewLayout(this, getViewParams());
    }

    //handle animation--------------------
    private void playAnimationClickDown() {
        if (!isInEditMode()) {
            AnimatorSet animator = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getContext(), R.animator.bubble_down_click_animator);
            animator.setTarget(this);
            animator.start();
        }
    }

    private void playAnimationClickUp() {
        if (!isInEditMode()) {
            AnimatorSet animator = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getContext(), R.animator.bubble_up_click_animator);
            animator.setTarget(this);
            animator.start();
        }
    }
    // end handle animation--------------------

    // get the width of device
    private void updateSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = (size.x - this.getWidth());

    }

    // 2 interface handle action click and remove
    public interface OnBubbleRemoveListener {
        void onBubbleRemoved(BubbleLayout bubble);
    }

    public interface OnBubbleClickListener {
        void onBubbleClick(BubbleLayout bubble);
    }

    // class MoveAnimator
    private class MoveAnimator implements Runnable {
        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        private void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            if (getRootView() != null && getRootView().getParent() != null) {
                float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
                float deltaX = (destinationX - getViewParams().x) * progress;
                float deltaY = (destinationY - getViewParams().y) * progress;
                move(deltaX, deltaY);
                if (progress < 1) {
                    handler.post(this);
                }
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }
}
