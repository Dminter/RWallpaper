package com.zncm.rwallpaper.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jiaomx on 2017/4/19.
 */

public class BaseData {


    public static <T> List<T> getListFromJSON(String str, Class<T> type) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();
        List<T> list = new Gson().fromJson(str, listType);
        return list;
    }

    public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr);
    }

    public static <T> T getObj(String s, Class<T> clazz) {
        T arr = new Gson().fromJson(s, clazz);
        return arr;
    }


}
