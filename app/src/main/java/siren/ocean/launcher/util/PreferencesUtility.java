package siren.ocean.launcher.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;

import siren.ocean.launcher.AppContext;
import siren.ocean.launcher.model.App;

public class PreferencesUtility {
    private static final String TAG = "PreferencesUtility";
    private final static String SAVED_APP_INSTANCES = "saved_app_instances";
    private final static String WALL_PAPER_INDEX = "wall_paper_index";

    static {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(AppContext.sInstance);
    }

    private static SharedPreferences mPreferences;

    public static int getCurrentWallPaperIndex() {
        return mPreferences.getInt(WALL_PAPER_INDEX, 0);
    }

    public static void setCurrentWallPaperIndex(int index) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(WALL_PAPER_INDEX, index);
        editor.apply();
    }

    public static String[] getSavedAppInstance() {
        String[] array = null;
        try {
            String data = mPreferences.getString(SAVED_APP_INSTANCES, "");
            if (TextUtils.isEmpty(data)) return null;
            array = new Gson().fromJson(data, String[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    public static void saveAppInstance(ArrayList<App> data) {
        try {
            String[] strings = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                strings[i] = data.get(i).getApplicationPackageName();
            }
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(SAVED_APP_INSTANCES, new Gson().toJson(strings, String[].class));
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}