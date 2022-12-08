package siren.ocean.launcher.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import siren.ocean.launcher.R;
import siren.ocean.launcher.bubble.BubbleDialog;
import siren.ocean.launcher.model.App;
import siren.ocean.launcher.ui.widgets.itemtouchhelper.CustomItemTouchHelper;
import siren.ocean.launcher.ui.widgets.itemtouchhelper.ItemTouchHelperAdapter;
import siren.ocean.launcher.ui.widgets.itemtouchhelper.ItemTouchHelperViewHolder;
import siren.ocean.launcher.ui.widgets.itemtouchhelper.SimpleItemTouchHelperCallback;
import siren.ocean.launcher.util.PreferencesUtility;
import siren.ocean.launcher.util.Systems;

import static android.content.Context.WALLPAPER_SERVICE;

/**
 * 适配器
 * Created by Siren on 2021/5/13.
 */
public class AppDrawerAdapter extends RecyclerView.Adapter<AppDrawerAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private static final String TAG = "AppDrawerAdapter";
    private ArrayList<App> mData = new ArrayList<>();
    private Context mContext;

    float mFontValue;
    float mIntFontValue;
    private CustomItemTouchHelper mItemTouchHelper;
    private View loading;
    private float downX, downY;
    private long pressTime;
    private boolean isLongClick = false;

    private int[] drawableIds = new int[]{
            R.drawable.wallpaper_02,
            R.drawable.wallpaper_06,
            R.drawable.wallpaper_09,
            R.drawable.wallpaper_10,
            R.drawable.wallpaper_23,
            R.drawable.wallpaper_24
    };

    public void initFontValue() {
        mIntFontValue = mContext.getResources().getInteger(R.integer.font_title_size_integer);
        float one_sp = mContext.getResources().getDimensionPixelSize(R.dimen.one_sp);
        mFontValue = one_sp * mIntFontValue;
    }

    public AppDrawerAdapter(Context mContext, RecyclerView recyclerView, View loading) {
        this.mContext = mContext;
        this.loading = loading;
        initFontValue();
        initTouchHelper(recyclerView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initTouchHelper(RecyclerView recyclerView) {
        CustomItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this);
        mItemTouchHelper = new CustomItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setOnTouchListener(wallPaperTouchListener);
    }

    View.OnTouchListener wallPaperTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = SystemClock.uptimeMillis();
                    downX = event.getX();
                    downY = event.getY();
                    isLongClick = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ((SystemClock.uptimeMillis() - pressTime) > 1000 && isLongClick) {
                        if (Math.abs(downX - event.getX()) <= Systems.dpToPx(5) && Math.abs(downY - event.getY()) <= Systems.dpToPx(5)) {
                            changeWallPaper();
                            isLongClick = false;
                        }
                    }
                    break;
            }
            return false;
        }
    };

    public void setData(List<App> data) {
        if (mData.size() - data.size() == 1) {
            dealDeleteData(data);
        } else if (data.size() - mData.size() == 1) {
            dealAddData(data);
        } else {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    private void dealDeleteData(List<App> data) {
        for (int i = 0; i < mData.size(); i++) {
            boolean isDelete = true;
            for (App app : data) {
                if (app.getApplicationPackageName().equals(mData.get(i).getApplicationPackageName())) {
                    isDelete = false;
                    break;
                }
            }

            if (isDelete) {
                mData.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    private void dealAddData(List<App> data) {
        for (int i = 0; i < data.size(); i++) {
            boolean isAdd = true;
            for (App app : mData) {
                if (app.getApplicationPackageName().equals(data.get(i).getApplicationPackageName())) {
                    isAdd = false;
                    break;
                }
            }

            if (isAdd) {
                mData.add(i, data.get(i));
                notifyItemInserted(i);
                return;
            }
        }
    }

    public void addData(List<App> data) {
        if (data != null) {
            int posBefore = mData.size();
            mData.addAll(data);
            notifyItemRangeInserted(posBefore, data.size());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_drawer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.mIcon.clearAnimation();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        mData.add(toPosition, mData.remove(fromPosition));
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
        ImageView mIcon;
        TextView mTitle;

        public BubbleDialog bubbleDialog;

        ViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mTitle = itemView.findViewById(R.id.text);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            openApp(view, mData.get(pos));
        }

        public void openApp(View v, App app) {
            if (app != null) {
                Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(app.getApplicationPackageName());
                startActivityType2(v, intent);
            }
        }

        public static final String KEY_ANIM_START_X = "android:activity.animStartX";

        public static final String KEY_ANIM_START_Y = "android:activity.animStartY";

        public void startActivityType2(View v, Intent intent) {
            int left = 0, top = 0;
            int width = v.getMeasuredWidth(), height = v.getMeasuredHeight();
            ActivityOptions options = ActivityOptions.makeScaleUpAnimation(v, left, top, width, height);
            Bundle b = options.toBundle();
//            Log.d(TAG, "startActivityType2: startX = " + b.getInt(KEY_ANIM_START_X, -1) + ", startY = " + b.getInt(KEY_ANIM_START_Y, -1));
            mContext.startActivity(intent, b);
        }

        void bind(App app) {
            mTitle.setText(app.getLabel());
            mIcon.setImageBitmap(app.getIcon());
            bindMovableIcon();
            bindAppSizeAndType();
            bindAppTitleTextView();
            itemView.setOnTouchListener(wallPaperTouchListener);
        }

        @SuppressLint("ClickableViewAccessibility")
        void setResponseOnTouch() {
            GestureDetector gd = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_bubble, null);
                    BubbleDialog bubbleDialog = new BubbleDialog(mContext)
                            .setBubbleContentView(rootView)
                            .setClickedView(ViewHolder.this.itemView)
                            .setTransParentBackground()
                            .setRelativeOffset(-20)
                            .setPosition(BubbleDialog.Position.TOP, BubbleDialog.Position.BOTTOM);
                    bubbleDialog.show();

                    //进入app详情
                    rootView.findViewById(R.id.tv_info).setOnClickListener(v -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        String pkn;
                        pkn = mData.get(getAdapterPosition()).getApplicationPackageName();
                        intent.setData(Uri.fromParts("package", pkn, null));
                        mContext.startActivity(intent);
                        bubbleDialog.dismiss();
                    });

                    //卸载app
                    rootView.findViewById(R.id.tv_uninstall).setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        String pkn;
                        pkn = mData.get(getAdapterPosition()).getApplicationPackageName();
                        intent.setData(Uri.parse(String.format("package:%s", pkn)));
                        mContext.startActivity(intent);
                        bubbleDialog.dismiss();
                    });

                    ViewHolder.this.bubbleDialog = bubbleDialog;
                    mItemTouchHelper.startDrag(ViewHolder.this);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
//                    Log.d("setResponseOnTouch", "onSingleTapConfirmed: ");
                    itemView.performClick();
                    onClick(itemView);
                    return true;
                }
            });

            mIcon.setOnTouchListener((v, event) -> {
                gd.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        if (view.getDrawable() != null)
                            view.getDrawable().setColorFilter(0x22000000, PorterDuff.Mode.SRC_ATOP);
                        view.getBackground().setColorFilter(0x22000000, PorterDuff.Mode.SRC_ATOP);
                        view.setAlpha(0.85f);
                        view.invalidate();
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        if (view.getDrawable() != null)
                            view.getDrawable().clearColorFilter();
                        view.getBackground().clearColorFilter();
                        view.setAlpha(1f);
                        view.invalidate();
                        break;
                    }
                }
                return false;
            });
        }

        private void bindMovableIcon() {
            setResponseOnTouch();
            itemView.clearAnimation();
        }

        private void bindAppSizeAndType() {
            Resources resources = mContext.getResources();
            float w = resources.getDimension(R.dimen.app_width);
            float h = resources.getDimension(R.dimen.app_height);

            float scale = 1.0f;

            int nw = (int) (w * scale);
            int nh = (int) (w * scale);

            RelativeLayout.LayoutParams iconParams = (RelativeLayout.LayoutParams) mIcon.getLayoutParams();
            iconParams.width = nw;
            iconParams.height = nh;
            iconParams.addRule(RelativeLayout.CENTER_IN_PARENT);

            ViewGroup.LayoutParams textParams = mTitle.getLayoutParams();
            ViewGroup.LayoutParams rootParams = itemView.getLayoutParams();
            rootParams.width = nw;
            rootParams.height = nh + textParams.height;
        }

        private void bindAppTitleTextView() {
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setTextColor(0xFFEEEEEE);
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mIntFontValue);
        }

        @Override
        public void onItemSelected() {
            itemView.clearAnimation();
            AnimationSet as = new AnimationSet(false);
            ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            as.addAnimation(sa);
            as.setDuration(85);
            as.setFillAfter(true);
            itemView.setAnimation(as);
        }

        @Override
        public void onItemClear() {
            itemView.clearAnimation();
            PreferencesUtility.saveAppInstance(mData);
        }
    }

    public void clear() {
        mData.clear();
    }

    public void changeWallPaper() {
        loading.animate().alphaBy(0f).alpha(1f).setDuration(200).start();
        new Thread(() -> {
            WallpaperManager wpm = (WallpaperManager) mContext.getSystemService(WALLPAPER_SERVICE);
            int[] size = Systems.getScreenSize(mContext);
            int width = size[0];
            int height = size[1];
            wpm.suggestDesiredDimensions(width, height);
            int index = PreferencesUtility.getCurrentWallPaperIndex();
            if (index >= drawableIds.length - 1) {
                index = 0;
            } else {
                index++;
            }
            PreferencesUtility.setCurrentWallPaperIndex(index);
            Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), drawableIds[index]);
            bm = ThumbnailUtils.extractThumbnail(bm, width, height);
            try {
                WallpaperManager.getInstance(mContext).setBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        loading.postDelayed(() -> {
            loading.animate().alphaBy(1).alpha(0).setDuration(200).start();
            loading.postDelayed(this::notifyDataSetChanged, 200);
        }, 3000);
    }
}