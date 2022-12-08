package siren.ocean.launcher.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import siren.ocean.launcher.AppContext;

/**
 * 系统工具
 * Created by Tony on 2017/8/4.
 */
public class Systems {

    public static int getStatusHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        if (result <= 0) {
            result = dpToPx(25);
        }
        return result;
    }

    public static int getNavigationHeight(Activity activity) {
        int navigationBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        if (!hasSoftKeys(activity.getWindowManager())) return 0;
        return navigationBarHeight;
    }

    public static boolean hasSoftKeys(WindowManager windowManager) {
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * dp to px
     *
     * @param dp
     * @return
     */
    public static int dpToPx(float dp) {
        return (int) applyDimension(AppContext.sInstance, TypedValue.COMPLEX_UNIT_DIP, dp);
    }

    /**
     * 单位转换
     *
     * @param context
     * @param unit    TypedValue.COMPLEX_UNIT_DIP
     * @param value   px
     * @return
     */
    public static float applyDimension(Context context, int unit, float value) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(unit, value, displayMetrics);
    }

    public static int[] getScreenSize(Context context) {
        Point p = new Point();
        Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        d.getRealSize(p);
        int width = p.x;
        int height = p.y;
        int[] screenSize = new int[]{width, height};
        return screenSize;
    }
}
