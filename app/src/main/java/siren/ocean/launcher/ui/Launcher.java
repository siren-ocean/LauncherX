package siren.ocean.launcher.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import siren.ocean.launcher.R;
import siren.ocean.launcher.model.App;
import siren.ocean.launcher.model.AppsLoader;
import siren.ocean.launcher.ui.widgets.BoundItemDecoration;
import siren.ocean.launcher.util.Systems;

/**
 * 主页
 * Created by Siren on 2021/5/13.
 */
public class Launcher extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<App>> {
    private static final String TAG = "Launcher";

    private FrameLayout mRecyclerViewParent;
    private RecyclerView mRecyclerView;
    private AppDrawerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        LoaderManager.getInstance(this).initLoader(0, null, this).forceLoad();
        initWindow();
        initView();
    }

    private void initWindow() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerViewParent = findViewById(R.id.recycler_view_parent);
        View loading = findViewById(R.id.loading);

        float statusBarHeight = Systems.getStatusHeight(this);
        float navigationHeight = Systems.getNavigationHeight(this);

        int[] size = Systems.getScreenSize(this);
        FrameLayout.LayoutParams rPParams = (FrameLayout.LayoutParams) mRecyclerViewParent.getLayoutParams();
        rPParams.topMargin += (int) statusBarHeight;
        rPParams.height = (int) (size[1] - rPParams.bottomMargin - navigationHeight - rPParams.topMargin);
        rPParams.bottomMargin = 0;
        mRecyclerViewParent.requestLayout();

        mAdapter = new AppDrawerAdapter(this, mRecyclerView, loading);
        mRecyclerView.setAdapter(mAdapter);
        setLayoutManager();
    }

    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void setLayoutManager() {
        Resources resources = getResources();
        float margin = resources.getDimension(R.dimen.app_drawer_margin);
        float padding = resources.getDimension(R.dimen.recycler_view_padding);
        int[] ss = Systems.getScreenSize(this);
        float mRVContentWidth = ss[0] - margin - padding;
        float mRVContentHeight = mRecyclerViewParent.getLayoutParams().height - resources.getDimension(R.dimen.recycler_view_margin_top) - resources.getDimension(R.dimen.recycler_view_padding);
        float scale = 1.0f;
        float appWidth = resources.getDimension(R.dimen.app_width) * scale;
        float appHeight = resources.getDimension(R.dimen.app_height) * scale;
        float minWidthZone = appWidth * 1.35f;
        float minHeightZone = appHeight * 1.35f;

        int numberColumn = (int) (mRVContentWidth / minWidthZone);
        int numberRow = (int) (mRVContentHeight / minHeightZone);
        float horizontalMargin = (mRVContentWidth - numberColumn * appWidth) / (numberColumn + 1);
        float verticalMargin = (mRVContentHeight - numberRow * appHeight) / (numberRow + 1);
        if (numberColumn < 1) numberColumn = 1;
        if (numberRow < 1) numberRow = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberColumn, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        BoundItemDecoration itemDecoration = new BoundItemDecoration(mRVContentWidth, mRVContentHeight, numberColumn, numberRow, (int) (verticalMargin * 0.9f), (int) (horizontalMargin * 0.9f));
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public Loader<List<App>> onCreateLoader(int i, @Nullable Bundle bundle) {
        Log.d(TAG, "onCreateLoader: ");
        return new AppsLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<App>> loader, List<App> appDetails) {
        Log.d(TAG, "onLoadFinished: ");
        mAdapter.setData(appDetails);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<App>> loader) {
    }
}