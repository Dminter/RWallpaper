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
import com.zncm.rwallpaper.service.GankService;
import com.zncm.rwallpaper.service.LovebizhiService;
import com.zncm.rwallpaper.service.UnsplashService;
import com.zncm.rwallpaper.service.backdrops.BackdropsData;
import com.zncm.rwallpaper.service.backdrops.BackdropsImg;
import com.zncm.rwallpaper.service.bing.BingData;
import com.zncm.rwallpaper.service.bing.BingImg;
import com.zncm.rwallpaper.service.gank.GankData;
import com.zncm.rwallpaper.service.gank.GankImg;
import com.zncm.rwallpaper.service.lovebizhi.LovebizhiData;
import com.zncm.rwallpaper.service.lovebizhi.LovebizhiImg;
import com.zncm.rwallpaper.service.lovebizhi.LovebizhiImgUrl;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class RandomWallpaperAc extends AppCompatActivity {
    ArrayList<String> wordLines = new ArrayList<>();
    WallpaperManager wallpaperManager = null;
   static String nextLine = "";
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


                } else if (SPHelper.getTypeSite() == EnumInfo.typeSite.LOVEBIZHI.getValue()) {

                    if (!Xutils.listNotNull(MyApp.urlQueue)) {
                        ServiceFactory.getInstance().createService(LovebizhiService.class)
                                .getRandomImg()
                                .subscribe(new Subscriber<LovebizhiData<LovebizhiImg<LovebizhiImgUrl>>>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(LovebizhiData<LovebizhiImg<LovebizhiImgUrl>> img) {
                                        List<LovebizhiImg<LovebizhiImgUrl>> imgUrls = img.getData();
                                        if (Xutils.listNotNull(imgUrls)) {
                                            Collections.shuffle(imgUrls);
                                            for (LovebizhiImg<LovebizhiImgUrl> url : imgUrls
                                                    ) {
                                                MyApp.urlQueue.add(url.getImage().getBig());
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


                } else if (SPHelper.getTypeSite() == EnumInfo.typeSite.WALLHAVEN.getValue()) {
                    //https://alpha.wallhaven.cc/latest
                    String url = "https://wallpapers.wallhaven.cc/wallpapers/full/wallhaven-" + new Random().nextInt(507840) + ".jpg";
                    oriDownload(url);
                }if (SPHelper.getTypeSite() == EnumInfo.typeSite.GANK.getValue()) {
                    ServiceFactory.getInstance().createService(GankService.class)
                            .getRandomBing()
                            .subscribe(new Subscriber<GankData<GankImg>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    Xutils.debug("e::" + e);
                                }

                                @Override
                                public void onNext(GankData<GankImg> imgData) {

                                    List<GankImg> imgUrls = imgData.getResults();
                                    if (Xutils.listNotNull(imgUrls)) {
                                        Collections.shuffle(imgUrls);
                                        for (GankImg url : imgUrls
                                                ) {
                                            MyApp.urlQueue.add(url.getUrl());
                                        }
                                        nextUrl = MyApp.urlQueue.poll();
                                        oriDownload(nextUrl);
                                    }
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
        if (!Xutils.notEmptyOrNull(url)) {
            Xutils.tShort("图片地址出错~");
            return;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        if (ServiceFactory.mOkHttpClient == null) {
            ServiceFactory.mOkHttpClient = new OkHttpClient.Builder().build();
        }
        ServiceFactory.mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                lastChange = 0L;
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
                    Bitmap oldBitmap = decodeBitmap(ctx, filePath, Xutils.getDeviceWidth(), Xutils.getDeviceHeight());

                } catch (Exception e) {
                    lastChange = 0L;
                    e.printStackTrace();
                }
            }
        });
    }


    public static Bitmap decodeBitmap(final Context ctx, final String path, final int displayWidth, final int displayHeight) {
        Bitmap retBitmap = getBitmapColor();
        try {
            if (!Xutils.notEmptyOrNull(path)) {
                return retBitmap;
            }

//            BitmapFactory.Options op = new BitmapFactory.Options();
//            op.inJustDecodeBounds = true;
//            int wRatio = (int) Math.ceil(op.outWidth / (float) displayWidth);
//            int hRatio = (int) Math.ceil(op.outHeight / (float) displayHeight);
//            if (wRatio > 1 && hRatio > 1) {
//                if (wRatio > hRatio) {
//                    op.inSampleSize = wRatio;
//                } else {
//                    op.inSampleSize = hRatio;
//                }
//            }
//            op.inJustDecodeBounds = false;
//            Bitmap bmp = BitmapFactory.decodeFile(path, op).copy(Bitmap.Config.ARGB_8888, true);
//            retBitmap = Bitmap.createScaledBitmap(bmp, displayWidth, displayHeight, true);


//            Luban.get(ctx)
//                    .load(new File(path))
//                    .putGear(Luban.THIRD_GEAR)
//                    .asObservable()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnError(new Action1<Throwable>() {
//                        @Override
//                        public void call(Throwable throwable) {
//                            throwable.printStackTrace();
//                        }
//                    })
//                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends File>>() {
//                        @Override
//                        public Observable<? extends File> call(Throwable throwable) {
//                            return Observable.empty();
//                        }
//                    })
//                    .subscribe(new Action1<File>() {
//                        @Override
//                        public void call(File file) {
//                            // TODO 压缩成功后调用，返回压缩后的图片文件
//                        }
//                    }).launch();    //启动压缩
            Luban.get(ctx)
                    .load(new File(path))                     //传人要压缩的图片
                    .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                    .setCompressListener(new OnCompressListener() { //设置回调

                        @Override
                        public void onStart() {
                            // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        }

                        @Override
                        public void onSuccess(File file) {
                            // TODO 压缩成功后调用，返回压缩后的图片文件
//                            BitmapFactory.Options op = new BitmapFactory.Options();
//                            op.inJustDecodeBounds = true;
//                            int wRatio = (int) Math.ceil(op.outWidth / (float) displayWidth);
//                            int hRatio = (int) Math.ceil(op.outHeight / (float) displayHeight);
//                            if (wRatio > 1 && hRatio > 1) {
//                                if (wRatio > hRatio) {
//                                    op.inSampleSize = wRatio;
//                                } else {
//                                    op.inSampleSize = hRatio;
//                                }
//                            }
//                            op.inJustDecodeBounds = false;
                            Bitmap bmp = BitmapFactory.decodeFile(path).copy(Bitmap.Config.RGB_565, true);
                            Bitmap   retBitmap = Bitmap.createScaledBitmap(bmp, displayWidth, displayHeight, true);
                            Bitmap bitmap = getNewBitMap(ctx,retBitmap);
                            try {
                                WallpaperManager.getInstance(ctx).setBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过去出现问题时调用
                        }
                    }).launch();    //启动压缩


        } catch (Exception e) {
            e.printStackTrace();
        }
        return retBitmap;
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
                Bitmap bitmap = getNewBitMap(ctx,getBitmapColor());
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
            Bitmap oldBitmap = decodeBitmap(ctx, nextUrl, Xutils.getDeviceWidth(), Xutils.getDeviceHeight());
//            Bitmap bitmap = getNewBitMap(oldBitmap);
//            wallpaperManager.setBitmap(bitmap);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getNewBitMap(Context ctx,Bitmap newBitmap) {
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(newBitmap, 0, 0, null);
        if (SPHelper.isFloatText() && Xutils.notEmptyOrNull(nextLine)) {
            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            float textSize = Xutils.dip2px(20);
            textPaint.setTextSize(textSize);
            textPaint.setColor(ctx.getResources().getColor(R.color.ms_white));
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
