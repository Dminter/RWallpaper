package com.zncm.rwallpaper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.kenumir.materialsettings.MaterialSettings;
import com.kenumir.materialsettings.items.CheckboxItem;
import com.kenumir.materialsettings.items.TextItem;
import com.kenumir.materialsettings.storage.StorageInterface;
import com.zncm.rwallpaper.utils.Constant;
import com.zncm.rwallpaper.utils.EnumInfo;
import com.zncm.rwallpaper.utils.MyPath;
import com.zncm.rwallpaper.utils.SPHelper;
import com.zncm.rwallpaper.utils.Xutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 设置
 */

public class SettingAc extends MaterialSettings {

    Activity ctx;
    String app_pkg;
    private File[] files = null;
    String fileLocal = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        app_pkg = getPackageName();

        File tmpDir = new File(MyPath.getPathText());
        if (tmpDir.exists() && tmpDir.length() > 0) {
            files = tmpDir.listFiles();
        }

        /**
         *初始导入数据
         */
        if (files == null || files.length == 0) {
            copyRawToSdcard(R.raw.cgt, MyPath.getPathText() + "/菜根谭.txt");
            copyRawToSdcard(R.raw.zgxw, MyPath.getPathText() + "/增广贤文.txt");
        } else {
            /**
             *默认选中一个文本源
             */
            if (!Xutils.notEmptyOrNull(SPHelper.getTextPath())) {
                SPHelper.setTextPath(files[0].getAbsolutePath());
            }
        }

        /**
         *权限申请
         */
        Xutils.verifyStoragePermissions(this);
        addItem(new TextItem(ctx, "").setTitle("换壁纸").setSubtitle("创建桌面快捷方式").setOnclick(new TextItem.OnClickListener() {
            public void onClick(TextItem textItem) {
                shortCutAdd(ctx, "换壁纸");
            }
        }));
        String fileName = "";
        if (Xutils.notEmptyOrNull(SPHelper.getTextPath())) {
            fileName = Xutils.getFileName(SPHelper.getTextPath());
        }


        addItem(new CheckboxItem(this, "").setTitle("文本").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                SPHelper.setIsFloatText(isChecked);
            }
        }).setDefaultValue(SPHelper.isFloatText()));

        addItem(new TextItem(ctx, "").setTitle("切换文本").setSubtitle(fileName).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                chooseText(textItem);
            }
        }));

        addItem(new TextItem(ctx, "").setTitle("纯色壁纸").setSubtitle(EnumInfo.typeColor.getTypeColor(SPHelper.getTypeColor()).getStrName()).setOnclick(new TextItem.OnClickListener() {
            public void onClick(final TextItem textItem) {
                ArrayList<String> items = new ArrayList();
                items.add(EnumInfo.typeColor.MATERIAL.getStrName());
                items.add(EnumInfo.typeColor.DEFAULT.getStrName());
                items.add(EnumInfo.typeColor.RANDOM.getStrName());
                new MaterialDialog.Builder(ctx).title("纯色壁纸").items(items).theme(Theme.LIGHT).itemsCallbackSingleChoice(SPHelper.getTypeColor() - 1, new MaterialDialog.ListCallbackSingleChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        SPHelper.setTypeColor(which + 1);
                        textItem.updateSubTitle(text.toString());
                        return false;
                    }
                }).positiveText("确定").negativeText("取消").onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).show();

            }
        }));
        addItem(new TextItem(ctx, "").setTitle("网络壁纸").setSubtitle(EnumInfo.typeSite.getTypeSite(SPHelper.getTypeSite()).getStrName()).setOnclick(new TextItem.OnClickListener() {
            public void onClick(final TextItem textItem) {
                ArrayList<String> items = new ArrayList();
                items.add(EnumInfo.typeSite.BING.getStrName());
                items.add(EnumInfo.typeSite.UNSPLASH.getStrName());
                items.add(EnumInfo.typeSite.BACKDROPS.getStrName());
                new MaterialDialog.Builder(ctx).title("网络壁纸").items(items).theme(Theme.LIGHT).itemsCallbackSingleChoice(SPHelper.getTypeSite() - 1, new MaterialDialog.ListCallbackSingleChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        SPHelper.setTypeSite(which + 1);
                        textItem.updateSubTitle(text.toString());
                        return false;
                    }
                }).positiveText("确定").negativeText("取消").onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).show();

            }
        }));


        if (Xutils.notEmptyOrNull(SPHelper.getLocalPath())) {
            fileLocal = SPHelper.getLocalPath();
        }
        addItem(new TextItem(ctx, "").setTitle("本地壁纸").setSubtitle(fileLocal).setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(final TextItem textItem) {

                final EditText editText = new EditText(ctx);
                editText.setTextColor(getResources().getColor(R.color.colorPrimary));
                new MaterialDialog.Builder(ctx).title("本地壁纸路径").customView(editText, false).positiveText("好").negativeText("不").onAny(new MaterialDialog.SingleButtonCallback() {
                    public void onClick(@NonNull MaterialDialog paramAnonymous3MaterialDialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            String localPath = editText.getText().toString();
                            if (!fileLocal.equals(localPath)) {
                                SPHelper.setLocalPath(localPath);
                                textItem.updateSubTitle(localPath);
                            }
                            MyApp.urlQueue.clear();
                        }
                    }
                }).show();

            }
        }));




        addItem(new TextItem(ctx, "").setTitle("壁纸来源").setSubtitle(EnumInfo.typeSource.getTypeSouce(SPHelper.getTypeSource()).getStrName()).setOnclick(new TextItem.OnClickListener() {
            public void onClick(final TextItem textItem) {
                ArrayList<String> items = new ArrayList();
                items.add(EnumInfo.typeSource.COLOR.getStrName());
                items.add(EnumInfo.typeSource.SITE.getStrName());
                items.add(EnumInfo.typeSource.LOCAL.getStrName());
                new MaterialDialog.Builder(ctx).title("壁纸来源").items(items).theme(Theme.LIGHT).itemsCallbackSingleChoice(SPHelper.getTypeSource() - 1, new MaterialDialog.ListCallbackSingleChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        SPHelper.setTypeSource(which + 1);
                        textItem.updateSubTitle(text.toString());
                        MyApp.urlQueue.clear();
                        return false;
                    }
                }).positiveText("确定").negativeText("取消").onPositive(new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).show();

            }
        }));


        addItem(new CheckboxItem(this, "").setTitle("仅WiFi下使用网络壁纸").setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(CheckboxItem cbi, boolean isChecked) {
                SPHelper.setIsOnlyWifi(isChecked);
            }
        }).setDefaultValue(SPHelper.isOnlyWifi()));


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ver：").append(Xutils.getVersionName(ctx)).append("\n");
        stringBuilder.append("Dev By：").append(Constant.author).append("\n");
        stringBuilder.append("Open Source：").append(Constant.github_url).append("\n");
        stringBuilder.append("Notice ：").append(Constant.notice).append("\n");
        addItem(new TextItem(ctx, "").setTitle("关于").setSubtitle(stringBuilder.toString()).setOnclick(new TextItem.OnClickListener() {
            public void onClick(TextItem textItem) {
                Xutils.openUrl(Constant.github_url);
            }
        }));


    }

    private void copyRawToSdcard(int resId, String fileSavePath) {
        try {

            InputStream inStream = getResources().openRawResource(resId);
            FileOutputStream fileOutputStream = new FileOutputStream(fileSavePath);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] bs = outStream.toByteArray();
            fileOutputStream.write(bs);
            outStream.close();
            inStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            SPHelper.setTextPath(fileSavePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseText(final TextItem textItem) {

        if (files == null || files.length == 0) {
            File tmpDir = new File(MyPath.getPathText());
            if (tmpDir.exists() && tmpDir.length() > 0) {
                files = tmpDir.listFiles();
            } else {
                return;
            }
        }

        LinearLayout view = new LinearLayout(ctx);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ListView listView = new ListView(ctx);
        List<Map<String, String>> nameList = new ArrayList<Map<String, String>>();
        for (int i = 0; i < files.length; i++) {
            Map<String, String> nameMap = new HashMap<String, String>();
            nameMap.put("name", files[i].getName());
            nameList.add(nameMap);
        }
        SimpleAdapter adapter = new SimpleAdapter(ctx,
                nameList, R.layout.cell_pj,
                new String[]{"name"},
                new int[]{R.id.tvTitle});
        listView.setAdapter(adapter);
        view.addView(listView);


        String item[] = null;
        item = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            item[i] = files[i].getName();
        }


        MaterialDialog md = new MaterialDialog.Builder(ctx)
                .title("切换文本")
                .items(item)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, final int pos, CharSequence charSequence) {
                        String path = files[pos].getAbsolutePath();
                        SPHelper.setTextPath(path);
                        textItem.updateSubTitle(files[pos].getName());
                        Xutils.readTxtFileToApp();
                        materialDialog.dismiss();
                    }
                })
                .positiveText("导入文本")
                .neutralText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {
                        showFileChooser();
                        materialDialog.dismiss();
                    }

                    @Override
                    public void onNeutral(MaterialDialog materialDialog) {
                        materialDialog.dismiss();
                    }
                })
                .autoDismiss(false)
                .build();
        md.setCancelable(false);
        md.show();

    }

    public void shortCutAdd(Context context, String name) {
        Intent intent = new Intent(context, RandomWallpaperAc.class);
        intent.setAction("android.intent.action.VIEW");
        intent.putExtra("android.intent.extra.UID", 0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent send = new Intent();
        send.putExtra("android.intent.extra.shortcut.INTENT", intent);
        send.putExtra("android.intent.extra.shortcut.NAME", name);
        send.putExtra("random", new Random().nextLong());
        try {
            intent.putExtra("duplicate", false);
            send.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(context.createPackageContext(app_pkg, 0), Xutils.getAppIconId(app_pkg)));
            send.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            context.sendBroadcast(send);
            Xutils.tShort("已创建快捷方式" + name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择文件导入/仅支持TXT"), 103);
        } catch (android.content.ActivityNotFoundException ex) {
            Xutils.tShort("没有找到文件管理器");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 103:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        String path = Xutils.getPathFromUri(ctx, uri);
                        String fileName = Xutils.getFileName(path);
                        String newPath = MyPath.getPathText() + File.separator + fileName;
                        boolean flag = Xutils.copyFileTo(new File(path), new File(newPath));
                        if (flag) {
                            Xutils.tShort("已导入~ ");
                            File tmpDir = new File(MyPath.getPathText());
                            if (tmpDir.exists() && tmpDir.length() > 0) {
                                files = tmpDir.listFiles();
                            }
                        } else {
                            Xutils.tShort("导入失败~");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    @Override
    public StorageInterface initStorageInterface() {
        return null;
    }


}
