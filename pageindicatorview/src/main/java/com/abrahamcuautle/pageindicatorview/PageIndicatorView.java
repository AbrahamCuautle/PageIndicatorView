package com.abrahamcuautle.pageindicatorview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class PageIndicatorView extends View {

    private final int MIN_HEIGHT = 40;

    private final long mDuration = 200L;

    private final Paint mPaintSelected = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint mPaintUnselected = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mPageIndicatorsCount;

    private int mRadius;

    private int mSpacing;

    private int mMaxWidthPageIndicatorsDistribution;

    private PageIndicator[] mPageIndicators;

    private float mLeftSelectedPageIndicator;

    private float mRightSelectedPageIndicator;

    private int mSelectedPageIndicatorPosition;

    private final ForwardAnimation mForwardAnimation = new ForwardAnimation();

    private final BackwardAnimation mBackwardAnimation = new BackwardAnimation();

    private boolean mPendingComputePageIndicatorSize;

    public PageIndicatorView(Context context) {
        super(context);
        setViewAttributes(null);
    }

    public PageIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setViewAttributes(attrs);
    }

    public PageIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setViewAttributes(attrs);
    }

    private void setViewAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PageIndicatorView);

        if (typedArray.hasValue(R.styleable.PageIndicatorView_indicator_selected_color)) {
            int color = typedArray.getColor(R.styleable.PageIndicatorView_indicator_selected_color, 0);
            mPaintSelected.setColor(color);
        }

        if (typedArray.hasValue(R.styleable.PageIndicatorView_indicator_unselected_color)) {
            int color = typedArray.getColor(R.styleable.PageIndicatorView_indicator_unselected_color, 0);
            mPaintUnselected.setColor(color);
        }

        if (typedArray.hasValue(R.styleable.PageIndicatorView_indicator_radius)) {
            mRadius = typedArray.getDimensionPixelOffset(R.styleable.PageIndicatorView_indicator_radius, 0);
        }

        if (typedArray.hasValue(R.styleable.PageIndicatorView_indicator_spacing)) {
            mSpacing = typedArray.getDimensionPixelOffset(R.styleable.PageIndicatorView_indicator_spacing, 0);
        }

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d("TAG_APP", " onMeasure laid out: " + ViewCompat.isLaidOut(this));
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int height = 0;

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(MIN_HEIGHT, heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                height = MIN_HEIGHT;
                break;
        }

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, heightMode));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("TAG_APP", " onLayout 1 laid out: " + ViewCompat.isLaidOut(this));
        if (mPendingComputePageIndicatorSize) {
            mPendingComputePageIndicatorSize = false;
            Log.d("TAG_APP", " onLayout 2 laid out: " + ViewCompat.isLaidOut(this));
            computeIndicatorSizeAndPositions();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("TAG_APP", " onDraw laid out: " + ViewCompat.isLaidOut(this));
        //Draw unselected page indicators
        for (int i = 0; i < mPageIndicatorsCount; i++) {
            float cx = mPageIndicators[i].getCx();
            float cy = mPageIndicators[i].getCy();
            canvas.drawCircle(cx, cy, mRadius, mPaintUnselected);
        }

        //Draw selected page indicator
        float top = (float) (getHeight() - mRadius * 2) / 2;
        float bottom = (float) (getHeight() + mRadius * 2) / 2;

        canvas.drawRoundRect(
                mLeftSelectedPageIndicator - 0.5f,
                top - 0.5f,
                mRightSelectedPageIndicator + 0.5f,
                bottom + 0.5f,
                mRadius,
                mRadius,
                mPaintSelected);

    }

    private boolean isPositionInRange(int position) {
        return position >= 0 && position < mPageIndicatorsCount;
    }

    private void computeMaxWidthPageIndicator() {
        mMaxWidthPageIndicatorsDistribution = 2 * mRadius * mPageIndicatorsCount + mSpacing * (mPageIndicatorsCount - 1);
    }

    private void computeCenterUnselectedPageIndicator() {
        if (mPageIndicatorsCount == 0) {
            return;
        }

        float centerXView = (float) getWidth() / 2;
        float cx = (centerXView - (float) (mMaxWidthPageIndicatorsDistribution / 2)) - mRadius - mSpacing;
        float cy = (float) getHeight() / 2;

        for (int i = 0; i < mPageIndicatorsCount; i++) {
            cx += (mRadius * 2) + mSpacing;
            mPageIndicators[i].setCx(cx);
            mPageIndicators[i].setCy(cy);
        }
    }

    private void computeInitialWidthSelectedPageIndicator() {
        if (mPageIndicatorsCount > 0 ){
            mLeftSelectedPageIndicator = mPageIndicators[0].getCx() - mRadius;
            mRightSelectedPageIndicator = mPageIndicators[0].getCx() + mRadius;
        }
    }

    private void computeIndicatorSizeAndPositions(){
        computeMaxWidthPageIndicator();
        computeCenterUnselectedPageIndicator();
        computeInitialWidthSelectedPageIndicator();
    }

    private void invalidateIndicatorSizeAndPositions() {
        if (ViewCompat.isLaidOut(this) && !isLayoutRequested()) {
            computeIndicatorSizeAndPositions();
            invalidate();
        } else  {
            mPendingComputePageIndicatorSize = true;
            requestLayout();
        }
    }

    public void setPageIndicatorsCount(int pageIndicatorsCount) {
        if (mPageIndicatorsCount == pageIndicatorsCount) {
            return;
        }

        this.mPageIndicatorsCount = pageIndicatorsCount;

        mPageIndicators = new PageIndicator[mPageIndicatorsCount];
        for (int i = 0; i < pageIndicatorsCount; i++) {
            mPageIndicators[i] = new PageIndicator();
        }

        invalidateIndicatorSizeAndPositions();

    }

    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
        invalidateIndicatorSizeAndPositions();
    }

    public void setSpacing(int mSpacing) {
        this.mSpacing = mSpacing;
        invalidateIndicatorSizeAndPositions();
    }

    public void selectPosition(int position, boolean animated) {
        if (position == mSelectedPageIndicatorPosition || !(position >= 0 && position < mPageIndicatorsCount)){
            return;
        }

        if (position > mSelectedPageIndicatorPosition) {
            mBackwardAnimation.cancel();
            mForwardAnimation.start(position, animated);
        } else {
            mForwardAnimation.cancel();
            mBackwardAnimation.start(position, animated);
        }

        mSelectedPageIndicatorPosition = position;

    }

    private final class ForwardAnimation {

        private ValueAnimator expandAnimator;

        private ValueAnimator collapseAnimator;

        private int position;

        public void start(int position, boolean animated) {
            this.position = position;
            if (animated) {
                startSelectedPageIndicatorExpandAnimation();
            } else {
                drawDotNextPosition();
            }
        }

        private void drawDotNextPosition() {

            if (mPageIndicators == null && !isPositionInRange(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            mRightSelectedPageIndicator = nextPageIndicator.getCx() + mRadius;
            mLeftSelectedPageIndicator = nextPageIndicator.getCx() - mRadius;
            invalidate();
        }

        private void startSelectedPageIndicatorExpandAnimation() {

            if (mPageIndicators == null && !isPositionInRange(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            float nextPageIndicatorRight = nextPageIndicator.getCx() + mRadius;

            expandAnimator = ValueAnimator.ofFloat(mRightSelectedPageIndicator, nextPageIndicatorRight);
            expandAnimator.addUpdateListener( animation -> {
                mRightSelectedPageIndicator = (float) animation.getAnimatedValue();
                invalidate();
            });

            expandAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    startSelectedPageIndicatorCollapseAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            expandAnimator.setDuration(mDuration);
            expandAnimator.start();
        }

        private void startSelectedPageIndicatorCollapseAnimation() {

            if (mPageIndicators == null && !isPositionInRange(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            float nextPageIndicatorLeft = nextPageIndicator.getCx() - mRadius;

            collapseAnimator = ValueAnimator.ofFloat(mLeftSelectedPageIndicator, nextPageIndicatorLeft);
            collapseAnimator.addUpdateListener(animation -> {
                mLeftSelectedPageIndicator = (float) animation.getAnimatedValue();
                invalidate();
            });
            collapseAnimator.setDuration(mDuration);
            collapseAnimator.start();
        }

        public void cancel() {
            if (expandAnimator != null) {
                expandAnimator.cancel();
            }

            if (collapseAnimator != null) {
                collapseAnimator.cancel();
            }
        }

    }

    private class BackwardAnimation {

        private ValueAnimator expandAnimator;

        private ValueAnimator collapseAnimator;

        private int position;

        public void start(int position, boolean animated) {
            this.position = position;
            if (animated) {
                startSelectedPageIndicatorExpandAnimation();
            } else {
                drawDotNextPosition();
            }
        }

        private void drawDotNextPosition() {

            if (mPageIndicators == null && !isPositionInRange(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            mRightSelectedPageIndicator = nextPageIndicator.getCx() + mRadius;
            mLeftSelectedPageIndicator = nextPageIndicator.getCx() - mRadius;
            invalidate();
        }

        private void startSelectedPageIndicatorExpandAnimation() {

            if (mPageIndicators == null && !isPositionInRange(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            float nextPageIndicatorLeft = nextPageIndicator.getCx() - mRadius;

            expandAnimator = ValueAnimator.ofFloat(mLeftSelectedPageIndicator, nextPageIndicatorLeft);

            expandAnimator.addUpdateListener( animation -> {
                mLeftSelectedPageIndicator = (float) animation.getAnimatedValue();
                invalidate();
            });

            expandAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    startSelectedPageIndicatorCollapseAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            expandAnimator.setDuration(mDuration);
            expandAnimator.start();
        }

        private void startSelectedPageIndicatorCollapseAnimation() {

            if (mPageIndicators == null && !isPositionInRange(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            float nextPageIndicatorRight = nextPageIndicator.getCx() + mRadius;

            collapseAnimator = ValueAnimator.ofFloat(mRightSelectedPageIndicator, nextPageIndicatorRight);
            collapseAnimator.addUpdateListener(animation -> {
                mRightSelectedPageIndicator = (float) animation.getAnimatedValue();
                invalidate();
            });
            collapseAnimator.setDuration(mDuration);
            collapseAnimator.start();
        }

        public void cancel() {
            if (expandAnimator != null) {
                expandAnimator.cancel();
            }

            if (collapseAnimator != null) {
                collapseAnimator.cancel();
            }
        }

    }

}
