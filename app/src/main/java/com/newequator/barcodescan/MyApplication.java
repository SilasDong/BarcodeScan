package com.newequator.barcodescan;

import android.app.Application;
import android.content.Context;



/**
 * Created by dongzhongmin on 2017/8/2.
 */

public class MyApplication extends Application {
    private static MyApplication instance;

    public static Context getAppContext(){
        return instance == null ? null : instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        com.tencent.smtt.sdk.WebView webView = new com.tencent.smtt.sdk.WebView(this);
        int width = webView.getView().getWidth();
    }
}
