package com.zncm.rwallpaper.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zncm.rwallpaper.MyApp;

public class SPHelper {
    public static boolean isFloatText() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        return sp.getBoolean("float_text", true);
    }

    public static void setIsFloatText(boolean float_text) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        sp.edit().putBoolean("float_text", float_text).commit();
    }

    public static String getTextPath() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        return sp.getString("text_path", "");
    }

    public static void setTextPath(String text_path) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        sp.edit().putString("text_path", text_path).commit();
    }

    public static int getTypeColor() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        return sp.getInt("type_color", EnumInfo.typeColor.MATERIAL.getValue());
    }

    public static void setTypeColor(int type_color) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        sp.edit().putInt("type_color", type_color).commit();
    }

    public static int getTypeSite() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        return sp.getInt("type_site", EnumInfo.typeSite.BING.getValue());
    }

    public static void setTypeSite(int type_site) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        sp.edit().putInt("type_site", type_site).commit();
    }

    public static int getTypeSource() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        return sp.getInt("type_source", EnumInfo.typeSource.COLOR.getValue());
    }

    public static void setTypeSource(int type_source) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyApp.ctx);
        sp.edit().putInt("type_source", type_source).commit();
    }
}