package siren.ocean.launcher;

import android.app.Application;

public class AppContext extends Application {
    public static AppContext sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        sInstance = null;
        super.onTerminate();
    }
}
