package com.nekobitlz.vkcup.commons;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Px;

public class DimenUtils {
    private final float density;

    public DimenUtils(@NonNull Context ctx) {
        this(ctx.getResources());
    }

    public DimenUtils(@NonNull Resources res) {
        density = res.getDisplayMetrics()
                .density;
    }

    /**
     * Конвертит dp в пиксели
     * @param dp размер в dp
     * @return размер в пикселях
     */
    @Px
    public int dp(float dp) {
        return (int) (dp * density);
    }

    /**
     * Конвертит px в dp
     * @param px размер в пикселях
     * @return размер в dp
     */
    public int px(@Px int px) {
        return (int) (px / density);
    }

    public static final int getRealDisplayPixels(final int dip, final Context context) {
        return getRealDisplayPixels(dip, context.getResources());
    }

    public static final int getRealDisplayPixels(final int dip, final Resources resources) {
        if (dip > 0) {
            final float density = resources.getDisplayMetrics().density;
            return Math.max(1, (int) (dip * density));
        }
        return 0;
    }

    public static int getStatusBarHeight(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return 0;
        }
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavBarWidth(Context context) {
        return getNavBarDimen(context, "navigation_bar_width");
    }

    public static int getNavBarHeight(Context context) {
        return getNavBarDimen(context, "navigation_bar_height");
    }

    private static int getNavBarDimen(Context context, String resourceString) {
        Resources r = context.getResources();
        int id = r.getIdentifier(resourceString, "dimen", "android");
        if (id > 0) {
            return r.getDimensionPixelSize(id);
        } else {
            return 0;
        }
    }


    public static View findNavigationBarView(View attachedView) {
        final int navBarId = attachedView.getContext().getResources().getIdentifier(
                "navigationBarBackground", "id", "android");
        if (navBarId != 0) {
            final View root = attachedView.getRootView();
            if (root instanceof ViewGroup) {
                final ViewGroup rootViewGroup = (ViewGroup) root;
                for (int i = rootViewGroup.getChildCount() - 1; i >= 0; i--) {
                    final View view = rootViewGroup.getChildAt(i);
                    if (view.getId() == navBarId) {
                        return view;
                    }
                }
            }
        }
        return null;
    }

    @Px
    public static float dpToPixels(Context context, float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }/*
    @Px
    public static int dpToPixels(float value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, ApplicationProvider.getApplication().getResources().getDisplayMetrics());
    }*/

    @Px
    public static int dimenResourceToPixels(Context context, @DimenRes int dimenResourceId) {
        return (int) context.getResources().getDimension(dimenResourceId);
    }
/*

    @Px
    public static int spToPixels(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, ApplicationProvider.getApplication().getResources().getDisplayMetrics());
    }
*/

    /**
     * @return True if {@link android.view.Window} of specified activity has full screen height.
     */
    public static boolean isWindowFullScreenHeight(Activity activity) {
        int windowHeight = activity.getWindow().getAttributes().height;
        int displayHeight = activity.getResources().getDisplayMetrics().heightPixels;
        return windowHeight == ViewGroup.LayoutParams.MATCH_PARENT || windowHeight == displayHeight;
    }

    public static int getActionBarHeight(Activity activity, @DimenRes int actionBarHeightResId){
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        int actionBarHeight = (int)activity.getResources().getDimension(actionBarHeightResId);
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,activity.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }
}
