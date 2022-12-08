package siren.ocean.launcher.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.loader.content.AsyncTaskLoader;
import siren.ocean.launcher.util.PreferencesUtility;

/**
 * 加载器
 * Created by Siren on 2021/5/13.
 */
public class AppsLoader extends AsyncTaskLoader<List<App>> {
    private static final String TAG = "AppsLoader";

    private final PackageManager mPm;
    private List<String> ignoreApp;

    public AppsLoader(Context context) {
        super(context);
        mPm = context.getPackageManager();
        initIgnoreApp();
        new PackageIntentReceiver(this);
    }

    private void initIgnoreApp() {
        ignoreApp = new ArrayList<>();
        //这里可添加需要过滤的app
        ignoreApp.addAll(Arrays.asList("com.android.traceur"));
        ignoreApp.add(getContext().getPackageName());
        List<ApplicationInfo> applications = mPm.getInstalledApplications(0);
        for (ApplicationInfo appInfo : applications) {
            String pkg = appInfo.packageName;
            if (getContext().getPackageManager().getLaunchIntentForPackage(pkg) == null) {
                ignoreApp.add(appInfo.packageName);
            }
        }
    }

    @Override
    public ArrayList<App> loadInBackground() {
        List<ApplicationInfo> applications = mPm.getInstalledApplications(0);
        ArrayList<App> appList = filterApp(applications);
        String[] strings = PreferencesUtility.getSavedAppInstance();
        ArrayList<App> returnList = new ArrayList<>();

        if (strings == null) {
            Collections.sort(appList, ALPHA_COMPARATOR);
            returnList = appList;
        } else {
            for (String str : strings) {
                for (App app : appList) {
                    if (app.getApplicationPackageName().equals(str)) {
                        returnList.add(app);
                        break;
                    }
                }
            }

            for (App app : appList) {
                if (!returnList.contains(app)) {
                    returnList.add(app);
                }
            }
        }

        PreferencesUtility.saveAppInstance(returnList);
        Log.d(TAG, "loadInBackground: " + Arrays.toString(PreferencesUtility.getSavedAppInstance()));
        return returnList;
    }

    private ArrayList<App> filterApp(List<ApplicationInfo> applicationInfos) {
        ArrayList<App> appList = new ArrayList<>();
        ignoreApp.add(getContext().getPackageName());
        for (ApplicationInfo appInfo : applicationInfos) {
            String pkg = appInfo.packageName;
            if (ignoreApp.contains(pkg)) {
                continue;
            }

            App app = new App(getContext(), appInfo);
            appList.add(app);
        }
        return appList;
    }

    /**
     * Perform alphabetical comparison of application entry objects.
     */
    public static final Comparator<App> ALPHA_COMPARATOR = new Comparator<App>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(App object1, App object2) {
            return sCollator.compare(object1.getLabel(), object2.getLabel());
        }
    };
}
