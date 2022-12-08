package siren.ocean.launcher.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

/**
 * @credit http://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */
public class App {

    private Context mContext;
    private ApplicationInfo mInfo;
    private String mAppLabel;
    private Bitmap mIcon;
    private String mPackageName;

    private static LruCache<String, Bitmap> bitmapCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 8)) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, width, height);
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap bitmapRound(Bitmap mBitmap, float index) {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        RectF rectf = new RectF(rect);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectf, index, index, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmap, rect, rect, paint);
        return bitmap;
    }

    public App(Context context, ApplicationInfo info) {
        mContext = context;
        mInfo = info;
        mAppLabel = mInfo.loadLabel(context.getPackageManager()).toString();
        mPackageName = info.packageName;

        mIcon = bitmapCache.get(mPackageName);
        if (mIcon == null) {
            Bitmap bitmap = drawableToBitmap(mInfo.loadIcon(mContext.getPackageManager()));
            mIcon = bitmapRound(bitmap, 10);
            bitmapCache.put(mPackageName, mIcon);
        }
    }

    public String getApplicationPackageName() {
        return mPackageName;
    }

    public String getLabel() {
        return mAppLabel;
    }

    public Bitmap getIcon() {
        return mIcon;
    }
}