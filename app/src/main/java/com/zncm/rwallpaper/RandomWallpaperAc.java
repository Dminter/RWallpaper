package com.zncm.rwallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.zncm.rwallpaper.api.ServiceFactory;
import com.zncm.rwallpaper.service.BackdropsService;
import com.zncm.rwallpaper.service.BingService;
import com.zncm.rwallpaper.service.UnsplashService;
import com.zncm.rwallpaper.service.backdrops.BackdropsData;
import com.zncm.rwallpaper.service.backdrops.BackdropsImg;
import com.zncm.rwallpaper.service.bing.BingData;
import com.zncm.rwallpaper.service.bing.BingImg;
import com.zncm.rwallpaper.service.unsplash.UnsplashData;
import com.zncm.rwallpaper.service.unsplash.UnsplashImg;
import com.zncm.rwallpaper.utils.ColorGenerator;
import com.zncm.rwallpaper.utils.EnumInfo;
import com.zncm.rwallpaper.utils.MyPath;
import com.zncm.rwallpaper.utils.SPHelper;
import com.zncm.rwallpaper.utils.Xutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;


public class RandomWallpaperAc extends AppCompatActivity {
    ArrayList<String> wordLines = new ArrayList<>();
    WallpaperManager wallpaperManager = null;
    String nextLine = "";
    String nextUrl = "";
    Context ctx;
    static Long lastChange = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
        wallpaperManager = WallpaperManager.getInstance(this);
        if (System.currentTimeMillis() - lastChange < 2 * 60 * 1000) {
            Xutils.tShort("正在准备壁纸请稍后...");
            finish();
            return;
        }
        lastChange = System.currentTimeMillis();
        initData();
    }


    public class MyTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            try {


                if (SPHelper.getTypeSite() == EnumInfo.typeSite.BING.getValue()) {
                    ServiceFactory.getInstance().createService(BingService.class)
                            .getRandomBing()
                            .subscribe(new Subscriber<BingData<BingImg>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Xutils.debug("e::" + e);
                                }

                                @Override
                                public void onNext(BingData<BingImg> imgData) {
                                    oriDownload(imgData.getData().getUrl());
                                }
                            });
                } else if (SPHelper.getTypeSite() == EnumInfo.typeSite.UNSPLASH.getValue()) {
                    ServiceFactory.getInstance().createService(UnsplashService.class)
                            .getRandomImg()
                            .subscribe(new Subscriber<UnsplashData<UnsplashImg>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Xutils.debug("e::" + e);
                                }

                                @Override
                                public void onNext(UnsplashData<UnsplashImg> img) {
                                    oriDownload(img.getUrls().getRegular());
                                }
                            });
                } else if (SPHelper.getTypeSite() == EnumInfo.typeSite.BACKDROPS.getValue()) {

                    if (!Xutils.listNotNull(MyApp.urlQueue)) {
                        ServiceFactory.getInstance().createService(BackdropsService.class)
                                .getRandomImg()
                                .subscribe(new Subscriber<BackdropsData<BackdropsImg>>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Xutils.debug("e::" + e);
                                    }

                                    @Override
                                    public void onNext(BackdropsData<BackdropsImg> img) {

                                        List<BackdropsImg> imgUrls = img.getEntertainment();
                                        if (Xutils.listNotNull(imgUrls)) {
                                            Collections.shuffle(imgUrls);
                                            for (BackdropsImg url : imgUrls
                                                    ) {
                                                MyApp.urlQueue.add(BackdropsService.WALLS_URL + url.getWallpaper_image());
                                            }
                                            nextUrl = MyApp.urlQueue.poll();
                                            oriDownload(nextUrl);
                                        }
                                    }
                                });
                    } else {
                        nextUrl = MyApp.urlQueue.poll();
                        oriDownload(nextUrl);
                    }


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


    private void oriDownload(String url) {
        if (!Xutils.notEmptyOrNull(url)) {
            Xutils.tShort("图片地址出错~");
            return;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        ServiceFactory.mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    int len;
                    byte[] buf = new byte[2048];
                    InputStream inputStream = response.body().byteStream();
                    String filePath = MyPath.getPathImg() + "/" + Xutils.getSaveTime() + ".png";
                    File file = new File(filePath);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((len = inputStream.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, len);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();
                    Bitmap oldBitmap = decodeBitmap(filePath, Xutils.getDeviceWidth(), Xutils.getDeviceHeight());
                    Bitmap bitmap = getNewBitMap(oldBitmap);
                    wallpaperManager.setBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static Bitmap decodeBitmap(String path, int displayWidth, int displayHeight) {
        if (!Xutils.notEmptyOrNull(path)) {
            return getBitmapColor();
        }

        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        int wRatio = (int) Math.ceil(op.outWidth / (float) displayWidth);
        int hRatio = (int) Math.ceil(op.outHeight / (float) displayHeight);
        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                op.inSampleSize = wRatio;
            } else {
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, op).copy(Bitmap.Config.ARGB_8888, true);
        return Bitmap.createScaledBitmap(bmp, displayWidth, displayHeight, true);
    }

    private void initData() {


        if (SPHelper.isFloatText()) {
            wordLines = MyApp.wordLines;
            if (!Xutils.listNotNull(wordLines)) {
                Xutils.readTxtFileToApp();
                wordLines = MyApp.wordLines;
            }
            if (Xutils.listNotNull(wordLines)) {
                nextLine = wordLines.get(new Random().nextInt(wordLines.size()));
            }
        }

        if (Xutils.listNotNull(MyApp.urlQueue)) {
            nextUrl = MyApp.urlQueue.poll();
        }

        if (SPHelper.getTypeSource() == EnumInfo.typeSource.COLOR.getValue()) {

            try {
                Bitmap bitmap = getNewBitMap(getBitmapColor());
                wallpaperManager.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (SPHelper.getTypeSource() == EnumInfo.typeSource.SITE.getValue()) {
            if (SPHelper.isOnlyWifi() && !Xutils.isWiFiActive(ctx)) {
                Xutils.tShort("仅WiFi下使用网络壁纸！");
            } else {
                new MyTask().execute();
            }
        } else if (SPHelper.getTypeSource() == EnumInfo.typeSource.LOCAL.getValue()) {
            if (Xutils.listNotNull(MyApp.urlQueue)) {
                nextUrl = MyApp.urlQueue.poll();
                setLocalImg();
            } else {
                File[] files = null;
                if (Xutils.notEmptyOrNull(SPHelper.getLocalPath())) {
                    if (files == null || files.length == 0) {
                        File tmpDir = new File(SPHelper.getLocalPath());
                        if (tmpDir.exists() && tmpDir.length() > 0) {
                            files = tmpDir.listFiles();
                            ArrayList<String> imgs = new ArrayList<>();
                            for (int i = 0; i < files.length; i++) {
                                File tmpFile = files[i];
                                if (Xutils.notEmptyOrNull(tmpFile.getName()) && Xutils.isImageEnd(tmpFile.getName())) {
                                    imgs.add(files[i].getAbsolutePath());
                                }
                            }
                            MyApp.urlQueue.clear();
                            if (Xutils.listNotNull(imgs)) {
                                Collections.shuffle(imgs);
                                for (String url : imgs
                                        ) {
                                    MyApp.urlQueue.add(url);
                                }
                                nextUrl = MyApp.urlQueue.poll();
                                setLocalImg();
                            }
                        }
                    }
                } else {
                    Xutils.tShort("请先设置本地壁纸路径~");
                }
            }
        }
        finish();
    }

    private void setLocalImg() {
        try {
            Bitmap oldBitmap = decodeBitmap(nextUrl, Xutils.getDeviceWidth(), Xutils.getDeviceHeight());
            Bitmap bitmap = getNewBitMap(oldBitmap);
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getNewBitMap(Bitmap newBitmap) {
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(newBitmap, 0, 0, null);
        if (SPHelper.isFloatText() && Xutils.notEmptyOrNull(nextLine)) {
            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            float textSize = Xutils.dip2px(20);
            textPaint.setTextSize(textSize);
            textPaint.setColor(getResources().getColor(R.color.ms_white));
            int textWidth = Xutils.dip2px(140);
            StaticLayout sl = new StaticLayout(nextLine, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            canvas.translate(Xutils.getDeviceWidth() / 2 - textWidth / 2, 80);
            sl.draw(canvas);
        }
        lastChange = 0L;
        return newBitmap;
    }

    public static Bitmap getBitmapColor() {
        Bitmap newBitmap = Bitmap.createBitmap(Xutils.getDeviceWidth(), Xutils.getDeviceHeight(),
                Bitmap.Config.ARGB_8888);
        if (EnumInfo.typeColor.MATERIAL.getValue() == SPHelper.getTypeColor()) {
            newBitmap.eraseColor(ColorGenerator.MATERIAL.getRandomColor());
        } else if (EnumInfo.typeColor.DEFAULT.getValue() == SPHelper.getTypeColor()) {
            newBitmap.eraseColor(ColorGenerator.DEFAULT.getRandomColor());
        } else if (EnumInfo.typeColor.RANDOM.getValue() == SPHelper.getTypeColor()) {
            newBitmap.eraseColor(Xutils.getRandColorCode());
        }
        return newBitmap;
    }


}
