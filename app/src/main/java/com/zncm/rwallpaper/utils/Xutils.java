package com.zncm.rwallpaper.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.zncm.rwallpaper.MyApp;
import com.zncm.rwallpaper.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by jiaomx on 2017/4/19.
 */

public class Xutils {

    public static String getPathFromUri(Context context, Uri uri) {
        String path = "";
        if (uri == null || "content://media/external/file/-1".equals(uri.toString())) {
            tShort("文件选取失败~");
            return null;
        }
        String[] projection = {"_data"};
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                    }
                    cursor.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    public static void readTxtFileToApp() {
        ArrayList<String> list = new ArrayList<>();
        try {
            String filePath = SPHelper.getTextPath();
            if (!notEmptyOrNull(filePath)) {
                return;
            }
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt != null && lineTxt.length() > 0) {
                        System.out.println(lineTxt.trim());
                        list.add(lineTxt.trim());
                    }
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
            MyApp.wordLines.clear();
            if (listNotNull(list)) {
                MyApp.wordLines.addAll(list);
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }

    public static boolean copyFileTo(File srcFile, File destFile)
            throws IOException {
        if (srcFile == null || destFile == null) {
            return false;
        }
        if (srcFile.isDirectory() || destFile.isDirectory())
            return false;
        if (!srcFile.exists()) {
            return false;
        }
        if (!destFile.exists()) {
            createFile(destFile.getAbsolutePath());
        }
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = fis.read(buf)) != -1) {
            fos.write(buf, 0, readLen);
        }
        fos.flush();
        fos.close();
        fis.close();
        return true;
    }

    public static boolean copyFileTo(InputStream inputStream, File destFile)
            throws IOException {
        if (inputStream == null || destFile == null) {
            return false;
        }
        if (destFile.isDirectory())
            return false;

        if (!destFile.exists()) {
            createFile(destFile.getAbsolutePath());
        }
        FileOutputStream fos = new FileOutputStream(destFile);
        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = inputStream.read(buf)) != -1) {
            fos.write(buf, 0, readLen);
        }
        fos.flush();
        fos.close();
        inputStream.close();
        return true;
    }

    public static File createFile(String path) throws IOException {
        if (notEmptyOrNull(path)) {
            File file = new File(path);
            if (!file.exists()) {
                int lastIndex = path.lastIndexOf(File.separator);
                String dir = path.substring(0, lastIndex);
                if (createFolder(dir) != null) {
                    file.createNewFile();
                    return file;
                }
            } else {
                file.createNewFile();
                return file;
            }
        }
        return null;
    }


    public static void deleteFile(File oldPath) {
        if (oldPath.isDirectory()) {
            File[] files = oldPath.listFiles();
            for (File file : files) {
                deleteFile(file);
            }
        }
        oldPath.delete();
    }


    public static String getFileName(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return null;
        return file.getName();
    }

    public static int getAppIconId(String packname) {
        try {
            PackageManager pm = MyApp.ctx.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return R.mipmap.ic_launcher;
    }

    public static File createFolder(String path) {
        if (notEmptyOrNull(path)) {
            File dir = new File(path);
            if (dir.exists()) {
                if (dir.isDirectory()) {
                    return dir;
                }
            }
            dir.mkdirs();
            return dir;
        } else {
            return null;
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = MyApp.ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = MyApp.ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static final WindowManager wm = (WindowManager) MyApp.ctx.getSystemService(Context.WINDOW_SERVICE);

    public static DisplayMetrics getDeviceMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static int getDeviceHeight() {
        return getDeviceMetrics().heightPixels;
    }

    public static int getDeviceWidth() {
        return getDeviceMetrics().widthPixels;
    }

    public static boolean notEmptyOrNull(String string) {
        if (string != null && !string.equalsIgnoreCase("null") && string.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public static <T> boolean listNotNull(List<T> t) {
        if (t != null && t.size() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public static void debug(Object string) {
        if (true) {
            try {
                Log.i("[rwallpaper]", String.valueOf(string));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void tShort(String msg) {
        if (!notEmptyOrNull(msg)) {
            return;
        }
        Toast.makeText(MyApp.ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public static void tLong(String msg) {
        if (!notEmptyOrNull(msg)) {
            return;
        }
        Toast.makeText(MyApp.ctx, msg, Toast.LENGTH_LONG).show();
    }

    // 获取十六进制的颜色代码.例如 "#6E36B4"
    public static int getRandColorCode() {
        String r, g, b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase();
        g = Integer.toHexString(random.nextInt(256)).toUpperCase();
        b = Integer.toHexString(random.nextInt(256)).toUpperCase();
        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;
        return Color.parseColor("#" + r + g + b);
    }

    public static String getSaveTime() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
    }
}
