package com.nekobitlz.vkcup.commons;

import android.graphics.Rect;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    @Nullable
    private Set<Integer> viewTypes;
    private boolean includeEdge;
    protected final int spacing;

    public GridSpacingItemDecoration(int spacing, boolean includeEdge) {
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (!shouldDecorate(position, adapter)) {
            return;
        }
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        final GridLayoutManager.SpanSizeLookup spanSizeLookup = layoutManager.getSpanSizeLookup();
        int spanCount = layoutManager.getSpanCount();
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();

        int firstSpanColumn = layoutParams.getSpanIndex();
        int lastSpanColumn = firstSpanColumn + layoutParams.getSpanSize() - 1;
        boolean isFirstRow = isFirstRowOrAfterAnotherViewType(adapter, position, firstSpanColumn, spanSizeLookup);

        if (includeEdge) {
            outRect.left = spacing - firstSpanColumn * spacing / spanCount;
            outRect.right = (lastSpanColumn + 1) * spacing / spanCount;

            if (isFirstRow) { // top edge
                outRect.top = getTopEdgeSpacing();
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = firstSpanColumn * spacing / spanCount;
            outRect.right = spacing - (lastSpanColumn + 1) * spacing / spanCount;
            if (!isFirstRow) {
                outRect.top = spacing; // item top
            }
        }
    }

    protected int getTopEdgeSpacing() {
        return spacing;
    }

    protected boolean isFirstRowOrAfterAnotherViewType(RecyclerView.Adapter adapter,
                                                       int position,
                                                       int column,
                                                       GridLayoutManager.SpanSizeLookup spanSizeLookup) {
        if (position == -1) {
            return false;
        }

        while (position > 0) {
            position--;
            column -= spanSizeLookup.getSpanSize(position);

            if (column < 0) {
                if (viewTypes == null) {
                    return false;
                } else {
                    return !viewTypes.contains(adapter.getItemViewType(position));
                }
            }
        }

        return true;
    }

    protected boolean isLastRowOrBeforeAnotherViewType(RecyclerView recyclerView,
                                                       int position,
                                                       int column) {
        if (position == -1) {
            return false;
        }

        GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
        GridLayoutManager.SpanSizeLookup spanSizeLookup = manager.getSpanSizeLookup();
        while (position < recyclerView.getAdapter().getItemCount()) {
            column += spanSizeLookup.getSpanSize(position);
            if (column > manager.getSpanCount()) {
                if (viewTypes == null) {
                    return false;
                } else {
                    return !viewTypes.contains(recyclerView.getAdapter().getItemViewType(position));
                }
            }
            position++;
        }

        return true;
    }

    protected boolean shouldDecorate(int position, RecyclerView.Adapter<?> adapter) {
        return position != RecyclerView.NO_POSITION && (viewTypes == null ||
                viewTypes.contains(adapter.getItemViewType(position)));
    }

    public GridSpacingItemDecoration setItemViewTypes(int... viewTypes) {
        this.viewTypes = new HashSet<>();
        for (int viewType : viewTypes) {
            this.viewTypes.add(viewType);
        }
        return this;
    }
}
