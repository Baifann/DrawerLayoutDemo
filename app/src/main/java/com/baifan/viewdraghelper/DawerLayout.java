package com.baifan.viewdraghelper;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by baifan on 16/5/31.
 */
public class DawerLayout extends FrameLayout {
    private ViewDragHelper mDragHelper;

    private View mDrawerView;

    private int mOirginWidth;

    private int mCurrentSeeWidh;

    private View mContentView;
    /**
     * 是否可以滑动
     */
    private boolean isCanSwip;

    public DawerLayout(Context context) {
        this(context, null);
    }

    public DawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragCallback());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Log.i("DawerLayout", "tryCaptureView");
            return child == mDrawerView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            int right;
            if (left <= 0) {
                right = mOirginWidth + left;
                mCurrentSeeWidh = right;
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left <= 0) {
                return left;
            }
            return 0;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mOirginWidth;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragHelper.captureChildView(mDrawerView, pointerId);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (releasedChild == mDrawerView) {
                Log.i("DrawerLayout", "mCurrentSeeWidh:" + mCurrentSeeWidh + ", getWidth() / 3:" + getWidth() / 3);
                if (mCurrentSeeWidh < getWidth() / 3) {
                    smooth2Left();
                } else {
                    smooth2Right();
                }
//                mDragHelper.settleCapturedViewAt(100, 100);
//                invalidate();
            }
        }
    }

    /**
     * 一定要这个
     */
    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDrawerView = findViewById(R.id.ly_drawer);
        mContentView = findViewById(R.id.ly_content);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mOirginWidth = mDrawerView.getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mDrawerView.layout(0 - mOirginWidth, 0, 0, getHeight());

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    private void smooth2Right() {
        if (mDragHelper.smoothSlideViewTo(mDrawerView, 0, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void smooth2Left() {
        if (mDragHelper.smoothSlideViewTo(mDrawerView, -mOirginWidth, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
