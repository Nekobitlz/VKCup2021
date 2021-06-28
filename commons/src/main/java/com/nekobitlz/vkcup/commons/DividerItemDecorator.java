package com.nekobitlz.vkcup.commons;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

import com.nekobitlz.vkcup.vkcup.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexey.nikitin on 23.04.15.
 */
public class DividerItemDecorator extends RecyclerView.ItemDecoration {
    private static final int DEFAULT_OFFSET = R.dimen.card_list_item_divider_left_offset;
    private static final int DEFAULT_COLOR = R.color.divider;

    protected final Paint paint = new Paint();
    protected final int dividerHeight;
    protected final int paddingLeft;
    protected boolean top = true;
    protected boolean bottom = false;
    protected int startingPosition = 1;
    protected int endingPosition = -1;
    private Set<Integer> viewTypes;
    private boolean skipFirstForEveryViewType = false;

    public DividerItemDecorator(@NonNull Context context) {
        this(context, context.getResources().getDimensionPixelOffset(DEFAULT_OFFSET), DEFAULT_COLOR);
    }

    public DividerItemDecorator(@NonNull Context context,
                                @Px int paddingLeft) {
        this(context, paddingLeft, DEFAULT_COLOR);
    }

    public DividerItemDecorator(@NonNull Context context,
                                @Px int paddingLeft,
                                @ColorRes int color) {
        this(context, paddingLeft,
                context.getResources().getDimensionPixelSize(R.dimen.card_list_item_divider_height),
                color);
    }

    public DividerItemDecorator(@NonNull Context context,
                                @Px int paddingLeft,
                                @Px int dividerHeight,
                                @ColorRes int color) {
        this.paint.setColor(context.getResources().getColor(color));
        this.dividerHeight = dividerHeight;
        this.paint.setStrokeWidth(dividerHeight);
        this.paddingLeft = paddingLeft;
    }

    public DividerItemDecorator setPosition(boolean top, boolean bottom, int startingPosition) {
        this.top = top;
        this.bottom = bottom;
        this.startingPosition = startingPosition;
        return this;
    }

    public DividerItemDecorator setPosition(int startingPosition, int endingPosition) {
        this.startingPosition = startingPosition;
        this.endingPosition = endingPosition;
        return this;
    }

    public DividerItemDecorator setItemViewTypes(int... viewTypes) {
        this.viewTypes = new HashSet<>();
        for (int viewType : viewTypes) {
            this.viewTypes.add(viewType);
        }
        return this;
    }

    public DividerItemDecorator setItemViewTypesAndSkipFirst(int... viewTypes) {
        skipFirstForEveryViewType = true;
        return setItemViewTypes(viewTypes);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (shouldDecorate(parent, view)) {
            if (top) {
                outRect.top += dividerHeight;
            }
            if (bottom) {
                outRect.bottom += dividerHeight;
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        for (int i = 0; i < parent.getChildCount(); i++) {
            View item = parent.getChildAt(i);
            if (shouldDecorate(parent, item)) {
                decorate(c, i, item);
            }
        }
    }

    protected void decorate(final Canvas c, final int i, final View item) {
        float alpha = item.getAlpha();
        paint.setAlpha((int) (alpha * alpha * 255));
        if (top) {
            float y = item.getTop() + item.getTranslationY() - dividerHeight / 2.0f;
            c.drawLine(item.getLeft() + paddingLeft, y, item.getRight(), y, paint);
        }
        if (bottom) {
            float y = item.getBottom() + item.getTranslationY() + dividerHeight / 2.0f;
            c.drawLine(item.getLeft() + paddingLeft, y, item.getRight(), y, paint);
        }
    }

    protected boolean shouldDecorate(RecyclerView parent, View child) {
        int position = parent.getChildAdapterPosition(child);
        RecyclerView.Adapter<?> adapter = parent.getAdapter();
        return position >= startingPosition
                && (position <= endingPosition || endingPosition < 0)
                && checkViewTypes(position, adapter);
    }

    private boolean checkViewTypes(int position, RecyclerView.Adapter<?> adapter) {
        int viewType;
        return viewTypes == null || viewTypes.isEmpty()
                || (viewTypes.contains(viewType = adapter.getItemViewType(position))
                && (!skipFirstForEveryViewType
                || (position > 0 && viewType == adapter.getItemViewType(position - 1))));
    }
}

