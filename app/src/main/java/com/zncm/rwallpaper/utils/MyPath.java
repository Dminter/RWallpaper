package com.zncm.rwallpaper.utils;

import android.os.Environment;

import java.io.File;

public class MyPath {
    public static final String PATH_TEXT = "txt";
    public static final String PATH_IMG = "image";

    public static String getFolder(String folderName) {
        if (folderName == null) {
            return null;
        }
        File dir = Xutils.createFolder(folderName);
        if (dir != null) {
            return dir.getAbsolutePath();
        } else {
            return null;
        }
    }


    private static String getPathFolder(String path) {
        File rootPath = Environment.getExternalStoragePublicDirectory(Constant.PATH_ROOT);
        return getFolder(rootPath + File.separator
                + path + File.separator);
    }


    public static String getPathText() {
        return getPathFolder(PATH_TEXT);
    }
    public static String getPathImg() {
        return getPathFolder(PATH_IMG);
    }


}
