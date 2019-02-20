package com.software.thincnext.kawasaki.Adapter;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class LinePagerIndicatorDecoration extends RecyclerView.ItemDecoration {


    private int colorActive = Color.parseColor("#929292");
    private int colorInactive = Color.parseColor("#e0e0e0");
    private int colorTransition = Color.parseColor("#11ffff00");
    private int lastActivePosition = 0;

    private static final float DP = Resources.getSystem().getDisplayMetrics().density;

    /**
     * Height of the space the indicator takes up at the bottom of the view.
     */
    private final int mIndicatorHeight = (int) (DP * 15);

    /**
     * Inactive Indicator radiius.
     */
    private final float itemRadius = DP * 4;
    /**
     * Padding between indicators.
     */
    private final float mIndicatorItemPadding = DP * 8;

    /**
     * Some more natural animation interpolation
     */
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private final Paint mPaint = new Paint();

    public LinePagerIndicatorDecoration() {
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int itemCount = parent.getAdapter().getItemCount();
        if (itemCount > 1) {

            float totalLength = itemRadius * 2 * itemCount;
            float paddingBetweenItems = Math.max(0, itemCount - 1) * mIndicatorItemPadding;
            float indicatorTotalWidth = totalLength + paddingBetweenItems;
            float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2F;

            // center vertically in the allotted space
            float indicatorPosY = parent.getHeight() - mIndicatorHeight / 2F;

            drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount);


            // find active page (which should be highlighted)
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            int activePosition = layoutManager.findFirstVisibleItemPosition();
            if (activePosition == RecyclerView.NO_POSITION) {
                return;
            }

            // find offset of active page (if the user is scrolling)
            final View activeChild = layoutManager.findViewByPosition(activePosition);
            int left = activeChild.getLeft();
            int width = activeChild.getWidth();

            // on swipe the active item will be positioned from [-width, 0]
            // interpolate offset for smooth animation
            float progress = mInterpolator.getInterpolation(left * -1 / (float) width);

            drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress);
        }
        // center horizontally, calculate width and subtract half from center
    }

    private void drawInactiveIndicators(Canvas c, float indicatorStartX, float cenY, int itemCount) {
        mPaint.setColor(colorInactive);

        // width of item indicator including padding
        final float itemWidth = itemRadius * 2 + mIndicatorItemPadding;

        float cenX = indicatorStartX + itemRadius;
        for (int i = 0; i < itemCount; i++) {
            // draw the circle for every item
            c.drawCircle(cenX, cenY, itemRadius, mPaint);
            cenX += itemWidth;
        }
    }

    private void drawHighlights(Canvas c, float indicatorStartX, float indicatorPosY,
                                int highlightPosition, float progress) {
        // width of item indicator including padding
        final float itemWidth = itemRadius * 2 + mIndicatorItemPadding;
        if (progress == 0F) {
            // no swipe, draw a normal indicator
            mPaint.setColor(colorActive);
            lastActivePosition = highlightPosition;
        } else {
            if (lastActivePosition > highlightPosition) {
                highlightPosition = lastActivePosition;
            }
            mPaint.setColor(colorTransition);
        }
        float highlightStart = indicatorStartX + itemWidth * highlightPosition;
        c.drawCircle(highlightStart + itemRadius, indicatorPosY, itemRadius, mPaint);

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = -mIndicatorHeight;
    }


}



