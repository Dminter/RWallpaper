package com.zncm.rwallpaper.service;

/**
 * Created by jiaomx on 2017/4/19.
 */


import com.zncm.rwallpaper.service.bing.BingData;
import com.zncm.rwallpaper.service.bing.BingImg;
import com.zncm.rwallpaper.service.gank.GankData;
import com.zncm.rwallpaper.service.gank.GankImg;

import retrofit2.http.GET;
import rx.Observable;

public interface GankService {
    String BASE_URL = "http://gank.io/api/";

    @GET("data/%E7%A6%8F%E5%88%A9/1000/1")
    Observable<GankData<GankImg>> getRandomBing();
}
