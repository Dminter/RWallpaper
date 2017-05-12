package com.zncm.rwallpaper;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jiaomx on 2017/4/19.
 */

public class MyApp extends Application {
    public static Context ctx;

    public static ArrayList<String> wordLines = new ArrayList<>();
    public static Queue<String> urlQueue = new LinkedList<String>();


    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
    }
}
