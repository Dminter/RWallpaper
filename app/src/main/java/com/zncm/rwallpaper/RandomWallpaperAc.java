package com.zncm.rwallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.zncm.rwallpaper.api.Data;
import com.zncm.rwallpaper.api.Img;
import com.zncm.rwallpaper.api.ServiceFactory;
import com.zncm.rwallpaper.service.BingService;
import com.zncm.rwallpaper.service.UnsplashService;
import com.zncm.rwallpaper.service.unsplash.UnsplashData;
import com.zncm.rwallpaper.service.unsplash.UnsplashUrl;
import com.zncm.rwallpaper.utils.ColorGenerator;
import com.zncm.rwallpaper.utils.EnumInfo;
import com.zncm.rwallpaper.utils.MyPath;
import com.zncm.rwallpaper.utils.SPHelper;
import com.zncm.rwallpaper.utils.Xutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wallpaperManager = WallpaperManager.getInstance(this);
        initData();
    }


    public class MyTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            try {

                if (SPHelper.getTypeSite() == EnumInfo.typeSite.BING.getValue()) {
                    ServiceFactory.getInstance().createService(BingService.class)
                            .getRandomBing()
                            .subscribe(new Subscriber<Data<Img>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Xutils.debug("e::" + e);
                                }

                                @Override
                                public void onNext(Data<Img> imgData) {
                                    Xutils.debug("111111111::");
                                    Xutils.debug("imgData111::" + imgData.getData());
                                    Xutils.debug("imgData11222::" + imgData.getData().getUrl());


                                    oriDownload(imgData.getData().getUrl());

                                }
                            });
                } else if (SPHelper.getTypeSite() == EnumInfo.typeSite.UNSPLASH.getValue()) {
                    ServiceFactory.getInstance().createService(UnsplashService.class)
                            .getRandomImg()
                            .subscribe(new Subscriber<UnsplashData<UnsplashUrl>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Xutils.debug("e::" + e);
                                }

                                @Override
                                public void onNext(UnsplashData<UnsplashUrl> img) {
                                    oriDownload(img.getUrls().getRegular());
                                }
                            });
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
        Xutils.debug("url::" + url);
        Request request = new Request.Builder()
                //下载地址
                .url(url)
                .build();
        ServiceFactory.mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("failure");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    //将返回结果转化为流，并写入文件
                    int len;
                    byte[] buf = new byte[2048];
                    InputStream inputStream = response.body().byteStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] buffer = new byte[1024];
                    while ((len = inputStream.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();

                    InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
                    InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

//                    wallpaperManager.setStream(is1);


                    //可以在这里自定义路径
                    String filePath = MyPath.getPathImg() + "/" + Xutils.getSaveTime() + ".png";
                    File file = new File(filePath);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((len = is2.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, len);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    is2.close();
                    Bitmap oldBitmap = BitmapFactory.decodeFile(filePath).copy(Bitmap.Config.ARGB_8888, true);
                    Bitmap bitmap = getNewBitMap(oldBitmap);
                    wallpaperManager.setBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
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

        if (SPHelper.getTypeSource() == EnumInfo.typeSource.COLOR.getValue()) {

            try {
                Bitmap bitmap = getNewBitMap(getBitmapColor());
                wallpaperManager.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (SPHelper.getTypeSource() == EnumInfo.typeSource.SITE.getValue()) {

            MyTask myTask = new MyTask();
            myTask.execute();
        }


//        try {
//
//            if (!Xutils.listNotNull(wordLines)) {
//                Xutils.readTxtFileToApp();
//                wordLines = MyApp.wordLines;
//            }
//
//
//            String show = "";
//
//            show = wordLines.get(new Random().nextInt(wordLines.size()));
////            show = "人之所以有一张嘴，而有两只耳朵，原因是听的要比说的多一倍人之所以有一张嘴，而有两只耳朵，原因是听的要比说的多一倍人之所以有一张嘴，而有两只耳朵，原因是听的要比说的多一倍人之所以有一张嘴，而有两只耳朵，原因是听的要比说的多一倍人之所以有一张嘴，而有两只耳朵，原因是听的要比说的多一倍人之所以有一张嘴，而有两只耳朵，原因是听的要比说的多一倍人之所以有一张嘴，而有两只耳朵，原因是听的要比说的多一倍";
//
//            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
//            Bitmap bitmap = getNewBitMap(show, Xutils.dip2px(200), 0);
//            wallpaperManager.setBitmap(bitmap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        finish();
    }

    public Bitmap getNewBitMap(Bitmap newBitmap) {
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(newBitmap, 0, 0, null);
        if (SPHelper.isFloatText() && Xutils.notEmptyOrNull(nextLine)) {
            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            float textSize = 36;
            textPaint.setTextSize(textSize);
            textPaint.setColor(getResources().getColor(R.color.ms_white));
            int textWidth = Xutils.dip2px(120);
            StaticLayout sl = new StaticLayout(nextLine, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            canvas.translate(Xutils.getDeviceWidth() / 2 - textWidth / 2, 80);
            sl.draw(canvas);
        }
        return newBitmap;
    }

    private Bitmap getBitmapColor() {
        Bitmap newBitmap = Bitmap.createBitmap(Xutils.getDeviceWidth(), Xutils.getDeviceHeight(),
                Bitmap.Config.ARGB_8888);
        Xutils.debug("===>>>" + Xutils.getDeviceWidth() + " --- " + Xutils.getDeviceHeight());
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
