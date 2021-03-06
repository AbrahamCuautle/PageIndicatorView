package com.abrahamcuautle.pageindicatorview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.google.android.material.color.MaterialColors;

public class PageIndicatorView extends View {

    private final int NO_POSITION = -1;

    private int MIN_HEIGHT;

    private int DEFAULT_PADDING;

    private final long DURATION = 200L;

    private final Paint mPaintSelected = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint mPaintDeselected = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mPageIndicatorsCount;

    private int mRadius;

    private int mSpacing;

    private int mMaxWidthPageIndicatorsDistribution;

    private PageIndicator[] mPageIndicators;

    private float mLeftSelectedPageIndicator;

    private float mRightSelectedPageIndicator;

    private int mSelectedPageIndicatorPosition = NO_POSITION;

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
        } else {
            int defaultColor = ContextCompat.getColor(getContext(), R.color.piv_default_selected_color);
            mPaintSelected.setColor(MaterialColors.getColor(getContext(), R.attr.colorPrimary, defaultColor));
        }

        if (typedArray.hasValue(R.styleable.PageIndicatorView_indicator_unselected_color)) {
            int color = typedArray.getColor(R.styleable.PageIndicatorView_indicator_unselected_color, 0);
            mPaintDeselected.setColor(color);
        } else {
            int defaultColor = ContextCompat.getColor(getContext(), R.color.piv_default_deselected_color);
            mPaintDeselected.setColor(MaterialColors.getColor(getContext(), R.attr.colorPrimaryVariant, defaultColor));
        }

        if (typedArray.hasValue(R.styleable.PageIndicatorView_indicator_radius)) {
            mRadius = typedArray.getDimensionPixelOffset(R.styleable.PageIndicatorView_indicator_radius, 0);
        } else {
            mRadius = (int) DxPxUtils.dpToPx(getContext(), 8);
        }

        if (typedArray.hasValue(R.styleable.PageIndicatorView_indicator_spacing)) {
            mSpacing = typedArray.getDimensionPixelOffset(R.styleable.PageIndicatorView_indicator_spacing, 0);
        } else {
            mSpacing = (int) DxPxUtils.dpToPx(getContext(), 8);
        }

        typedArray.recycle();

        DEFAULT_PADDING = (int) DxPxUtils.dpToPx(getContext(), 8);
        MIN_HEIGHT = (int) DxPxUtils.dpToPx(getContext(), 15);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int width = 0;

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                computeMaxWidthPageIndicator();

                int desiredPaddingLeft = getPaddingLeft() == 0 ? DEFAULT_PADDING : getPaddingLeft();
                int desiredPaddingRight = getPaddingRight() == 0 ? DEFAULT_PADDING : getPaddingRight();
                int desiredWidth = mMaxWidthPageIndicatorsDistribution + desiredPaddingLeft + desiredPaddingRight;

                width = Math.min(desiredWidth, widthSize);
                break;
        }

        int height = 0;

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                int desiredPaddingTop = getPaddingTop() == 0 ? DEFAULT_PADDING : getPaddingTop();
                int desiredPaddingBottom = getPaddingBottom() == 0 ? DEFAULT_PADDING : getPaddingBottom();
                int desiredHeight = mRadius * 2 + desiredPaddingTop + desiredPaddingBottom;
                height = Math.min(desiredHeight, heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                height = MIN_HEIGHT;
                break;
        }

        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, heightMode));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mPendingComputePageIndicatorSize) {
            mPendingComputePageIndicatorSize = false;
            computeIndicatorSizeAndPositions();
        }

        //Draw unselected page indicators
        for (int i = 0; i < mPageIndicatorsCount; i++) {
            float cx = mPageIndicators[i].getCx();
            float cy = mPageIndicators[i].getCy();
            canvas.drawCircle(cx, cy, mRadius, mPaintDeselected);
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

    private boolean isPositionValid(int position) {
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
        if (mPageIndicatorsCount > 0 && mSelectedPageIndicatorPosition != NO_POSITION){
            mLeftSelectedPageIndicator = mPageIndicators[mSelectedPageIndicatorPosition].getCx() - mRadius;
            mRightSelectedPageIndicator = mPageIndicators[mSelectedPageIndicatorPosition].getCx() + mRadius;
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

    public void setSelectedColor(int color) {
        mPaintSelected.setColor(color);
        invalidate();
    }

    public void setDeselectedColor(int color) {
        mPaintDeselected.setColor(color);
        invalidate();
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

        if (ViewCompat.isLaidOut(this) && !isLayoutRequested()) {
            if (position > mSelectedPageIndicatorPosition) {
                mBackwardAnimation.cancel();
                mForwardAnimation.start(position, animated);
            } else {
                mForwardAnimation.cancel();
                mBackwardAnimation.start(position, animated);
            }
        } else {
            mPendingComputePageIndicatorSize = true;
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

            if (mPageIndicators == null && !isPositionValid(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            mRightSelectedPageIndicator = nextPageIndicator.getCx() + mRadius;
            mLeftSelectedPageIndicator = nextPageIndicator.getCx() - mRadius;
            invalidate();
        }

        private void startSelectedPageIndicatorExpandAnimation() {

            if (mPageIndicators == null && !isPositionValid(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            float nextPageIndicatorRight = nextPageIndicator.getCx() + mRadius;

            expandAnimator = ValueAnimator.ofFloat(mRightSelectedPageIndicator, nextPageIndicatorRight);
            expandAnimator.addUpdateListener( animation -> {
                mRightSelectedPageIndicator = (float) animation.getAnimatedValue();
                invalidate();
            });

            expandAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startSelectedPageIndicatorCollapseAnimation();
                }
            });
            expandAnimator.setDuration(DURATION);
            expandAnimator.start();
        }

        private void startSelectedPageIndicatorCollapseAnimation() {

            if (mPageIndicators == null && !isPositionValid(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            float nextPageIndicatorLeft = nextPageIndicator.getCx() - mRadius;

            collapseAnimator = ValueAnimator.ofFloat(mLeftSelectedPageIndicator, nextPageIndicatorLeft);
            collapseAnimator.addUpdateListener(animation -> {
                mLeftSelectedPageIndicator = (float) animation.getAnimatedValue();
                invalidate();
            });
            collapseAnimator.setDuration(DURATION);
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

            if (mPageIndicators == null && !isPositionValid(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            mRightSelectedPageIndicator = nextPageIndicator.getCx() + mRadius;
            mLeftSelectedPageIndicator = nextPageIndicator.getCx() - mRadius;
            invalidate();
        }

        private void startSelectedPageIndicatorExpandAnimation() {

            if (mPageIndicators == null && !isPositionValid(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            float nextPageIndicatorLeft = nextPageIndicator.getCx() - mRadius;

            expandAnimator = ValueAnimator.ofFloat(mLeftSelectedPageIndicator, nextPageIndicatorLeft);

            expandAnimator.addUpdateListener( animation -> {
                mLeftSelectedPageIndicator = (float) animation.getAnimatedValue();
                invalidate();
            });

            expandAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startSelectedPageIndicatorCollapseAnimation();
                }
            });
            expandAnimator.setDuration(DURATION);
            expandAnimator.start();
        }

        private void startSelectedPageIndicatorCollapseAnimation() {

            if (mPageIndicators == null && !isPositionValid(position)){
                return;
            }

            PageIndicator nextPageIndicator = mPageIndicators[position];

            float nextPageIndicatorRight = nextPageIndicator.getCx() + mRadius;

            collapseAnimator = ValueAnimator.ofFloat(mRightSelectedPageIndicator, nextPageIndicatorRight);
            collapseAnimator.addUpdateListener(animation -> {
                mRightSelectedPageIndicator = (float) animation.getAnimatedValue();
                invalidate();
            });
            collapseAnimator.setDuration(DURATION);
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
