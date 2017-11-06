package me.cl.lingxi.common.widget;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/09/15
 * desc   :
 * version: 1.0
 */

public class ItemDecoration extends RecyclerView.ItemDecoration {

    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;

    private int mOrientation;
    private int mDecoration;
    private ColorDrawable mDivider;

    private final Rect mBounds = new Rect();

    public ItemDecoration(@LinearLayoutCompat.OrientationMode int orientation, int decoration, @ColorInt int color) {
        setOrientation(orientation);
        setDecoration(decoration);
        setDrawable(color);
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void setDecoration(int decoration) {
        mDecoration = decoration;
    }

    public void setDrawable(@ColorInt int color) {
        mDivider = new ColorDrawable(color);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() == null) {
            return;
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    @SuppressLint("NewApi")
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }
        final int lastPosition = ((LinearLayoutManager)parent.getLayoutManager()).findLastCompletelyVisibleItemPosition();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int adapterPosition = parent.getChildAdapterPosition(child);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child));
            final int top = bottom - mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    @SuppressLint("NewApi")
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
            final int right = mBounds.right + Math.round(ViewCompat.getTranslationX(child));
            final int left = right - mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, mDecoration);
        } else {
            outRect.set(0, 0, mDecoration, 0);
        }
    }
}
